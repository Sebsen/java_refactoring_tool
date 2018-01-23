package code.cafebabe.refactoring;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.google.common.collect.Lists;

import code.cafebabe.refactoring.action.Action;

public class MethodCallRefactoring extends Refactoring {

	private boolean trustImportStatements = false;
	private final Action action;

	MethodCallRefactoring(final String pTargetType, final Action pAction, final String pReplacement) {
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
		final Iterator<FieldDeclaration> matchingFieldDeclarationsForReplacementTypeIterator = resolveFieldDeclarations(
				pCompilationUnit, replacement).iterator();
		final Optional<FieldDeclaration> matchingFieldDeclarationsForReplacementType = matchingFieldDeclarationsForReplacementTypeIterator
				.hasNext() ? Optional.of(matchingFieldDeclarationsForReplacementTypeIterator.next()) : Optional.empty();

		final List<MethodDeclaration> methodsToProcess = pCompilationUnit.findAll(MethodDeclaration.class);

		methodsToProcess.forEach(m -> {
			// Process every method in class from bottom to up
			final List<Node> nodesToProcess = Lists.reverse(m.findFirst(BlockStmt.class).get().getChildNodes()).stream()
					.filter(child -> action.isApplyable(child, targetType, pTypeSolver)).collect(Collectors.toList());

			action.consume(nodesToProcess, matchingFieldDeclarationsForReplacementType);
		});
		action.consumeFieldDeclarations(matchingFieldDeclarationsForTargetType);
		action.consumeImports(pCompilationUnit.getImports(), targetType);

		return pCompilationUnit;
	}

}
