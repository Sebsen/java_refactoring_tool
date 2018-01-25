package code.cafebabe.refactoring;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.action.Action;
import code.cafebabe.refactoring.action.FieldConverterAction;

public class MethodMappingRefactoring extends Refactoring {

	private boolean trustImportStatements = true;
	private final Action action;

	MethodMappingRefactoring(final String pTargetType, final Action pAction, final String pReplacement) {
		super(pTargetType, pReplacement);
		action = pAction;
	}

	@Override
	public CompilationUnit apply(final CompilationUnit pCompilationUnit, final TypeSolver pTypeSolver) {
		if (trustImportStatements && !doImportsContainTargetType(pCompilationUnit, targetType)) {
			return pCompilationUnit;
		}

		final Set<FieldDeclaration> matchingFieldDeclarationsForTargetType = resolveFieldDeclarations(pCompilationUnit,
				targetType);

		final Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType = resolveFieldDeclarations(
				pCompilationUnit, replacement);

		final List<MethodDeclaration> methodsToProcess = pCompilationUnit.findAll(MethodDeclaration.class);

		// Process each method individually
		methodsToProcess.forEach(m -> {
			final List<Node> nodesToProcess = m.findFirst(BlockStmt.class).get().findAll(MethodCallExpr.class).stream()
					.filter(child -> action.isApplyable(child, targetType, pTypeSolver)).collect(Collectors.toList());

			if (matchingFieldDeclarationsForTargetType.isEmpty() && !nodesToProcess.isEmpty()) {
				// Since we want to convert method calls to field access (but
				// any field of desired type is present in processed type)
				// manually add one!
				final FieldDeclaration fieldDeclartionToAdd = new FieldDeclaration(
						EnumSet.of(Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL),
						createVariableDeclaration(pCompilationUnit, nodesToProcess));

				// Add field to list of 'retrieved' fields of desired type from
				// processed file
				matchingFieldDeclarationsForTargetType.add(fieldDeclartionToAdd);

				// And then also add it to compilation unit itself
				addFieldToCompilationUnit(pCompilationUnit, fieldDeclartionToAdd);

				// TODO: Create issue! Import is not added properly -
				// x.addImport(Clazz) works fine though..!
				if (pCompilationUnit.getImports().stream().map(ImportDeclaration::getNameAsString)
						.filter(importDec -> importDec.equals(targetType)).count() <= 0) {
					pCompilationUnit.addImport(targetType, false, false);
				}

				// TODO: Create issue sorting imports: Imports get duplicated..
				// List<ImportDeclaration> importsToSort = new
				// ArrayList<>(pCompilationUnit.getImports());
				// pCompilationUnit.setImports(new NodeList<>());
				// importsToSort.add(new
				// ImportDeclaration(JavaParser.parseName(targetType), false,
				// false));

				// importsToSort.sort((i1, i2) ->
				// i1.getNameAsString().compareTo(i2.getNameAsString()));
				// pCompilationUnit.setImports(new NodeList<>(importsToSort));

			}

			// Let action consume retrieved nodes and do it's job
			action.consume(nodesToProcess, matchingFieldDeclarationsForTargetType,
					matchingFieldDeclarationsForReplacementType);
		});
		action.consumeFieldDeclarations(matchingFieldDeclarationsForTargetType);
		action.consumeImports(pCompilationUnit.getImports(), targetType);

		return pCompilationUnit;
	}

	/**
	 * Adds a given field declaration as first member before any
	 * CallableDeclaration (eg ConstructorDeclaration or MethodDeclaration)
	 * 
	 * @param pCompilationUnit
	 *            The compilation unit to add the field to
	 * @param pFieldDeclartionToAdd
	 *            The field declaration to add to the compilation unit
	 * @param nodesToProcess
	 */
	private void addFieldToCompilationUnit(final CompilationUnit pCompilationUnit,
			final FieldDeclaration pFieldDeclartionToAdd) {
		final Optional<ClassOrInterfaceDeclaration> classDeclaration = pCompilationUnit
				.findFirst(ClassOrInterfaceDeclaration.class);
		classDeclaration.ifPresent(f -> {
			Optional<CallableDeclaration> firstCallableDeclaration = f.findFirst(CallableDeclaration.class);

			if (firstCallableDeclaration.isPresent()) {
				// If any callable declaration is present - add before
				f.getMembers().addBefore(pFieldDeclartionToAdd, firstCallableDeclaration.get());
			} else {
				// Else (fallback) add as first member
				// TODO: Create issue: When new member is added as first one
				// intendation is not properly - as last one works though..
				f.getMembers().addBefore(pFieldDeclartionToAdd, f.getMember(0));
			}

		});
	}

	private VariableDeclarator createVariableDeclaration(final CompilationUnit pCompilationUnit,
			final List<Node> nodesToProcess) {

		final Optional<ClassOrInterfaceDeclaration> classDeclaration = pCompilationUnit
				.findFirst(ClassOrInterfaceDeclaration.class);

		String newFieldName = "javaRefactoringToolCreatedField";
		final Set<String> desiredFieldNames = ((FieldConverterAction) action).getDesiredFieldNames();
		// Process actions desired new field names and exclude the ones from the
		// list, which are already declared in type - otherwise create with new
		// random UUID!
		if (classDeclaration.isPresent() && !desiredFieldNames.isEmpty()) {
			final ClassOrInterfaceDeclaration classDec = classDeclaration.get();
			classDec.getFields().stream().flatMap(f -> f.getVariables().stream())
					.map(VariableDeclarator::getNameAsString).filter(desiredFieldNames::contains)
					.forEach(desiredFieldNames::remove);
			if (!desiredFieldNames.isEmpty()) {
				newFieldName = desiredFieldNames.iterator().next();
			}
		}

		// Create variable/ field initializer expression out of target
		// MethodCallExpr which shall be replaced
		return new VariableDeclarator(convertTargetTypeToType(), newFieldName,
				(MethodCallExpr) nodesToProcess.get(0).clone());
	}

	private ClassOrInterfaceType convertTargetTypeToType() {
		return JavaParser
				.parseClassOrInterfaceType(targetType.substring(targetType.lastIndexOf('.') + 1, targetType.length()));
	}

}
