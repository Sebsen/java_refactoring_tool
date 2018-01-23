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

	private boolean trustImportStatements = true;
	private final Action action;

	MethodCallRefactoring(final Class<?> pTargetType, final Action pAction, final Class<?> pReplacement) {
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
		// debugMatchingFields(matchingFieldDeclarations);

		final List<MethodDeclaration> methodsToProcess = Navigator.findAllNodesOfGivenClass(pCompilationUnit,
				MethodDeclaration.class);
		methodsToProcess.forEach(m -> {
			// Process every method in class from bottom to up
			final List<Node> nodesToProcess = Lists.reverse(m.findFirst(BlockStmt.class).get().getChildNodes()).stream()
					.filter(child -> isApplyable(child, targetType, pTypeSolver)).collect(Collectors.toList());

			action.consume(nodesToProcess, matchingFieldDeclarationsForReplacementType);
		});
		action.consumeFieldDeclarations(matchingFieldDeclarationsForTargetType);
		action.consumeImports(pCompilationUnit.getImports(), targetType);

		return pCompilationUnit;
	}

	private void debugMatchingFields(final Set<FieldDeclaration> matchingFieldDeclarations) {
		System.out.println("Matching FieldDeclarations: " + matchingFieldDeclarations);
	}

	@Override
	public <T extends Node> boolean isApplyable(T pNode, Class<?> pTargetType, TypeSolver pTypeSolver) {
		if (pNode instanceof ExpressionStmt) {
			return isApplyable((ExpressionStmt) pNode, pTargetType, pTypeSolver);
		}
		return false;
	}
	
	private boolean isApplyable(final ExpressionStmt pExpression, final Class<?> pTargetType,
			final TypeSolver pTypeSolver) {
		final Optional<MethodCallExpr> m = pExpression.findFirst(MethodCallExpr.class);
		if (m.isPresent() && m.get().isMethodCallExpr()) {
			final MethodCallExpr methodCall = m.get();

			final MethodUsage resolvedMethodCall = JavaParserFacade.get(pTypeSolver).solveMethodAsUsage(methodCall);
			return isDeclaringTypeTargetType(resolvedMethodCall, pTargetType)
					|| isReturnTypeTargetType(resolvedMethodCall, pTargetType);

		}
		return false;
	}

	/**
	 * Checks if the declaring type of this MethodCall is the same as the target
	 * type to look for.
	 * 
	 * @param pResolvedMethodCall
	 *            The resolved method call
	 * @param pTargetType
	 *            The target type to look for
	 * @return true, if the declaring type for this method call is the same as
	 *         the target type to look for otherwise false
	 */
	private boolean isDeclaringTypeTargetType(final MethodUsage pResolvedMethodCall, final Class<?> pTargetType) {
		return pResolvedMethodCall.declaringType().getQualifiedName().equals(pTargetType.getName());
	}

	/**
	 * Checks if the return type of this MethodCall is the same as the target
	 * type to look for.
	 * 
	 * @param pResolvedMethodCall
	 *            The resolved method call
	 * @param pTargetType
	 *            The target type to look for
	 * @return true, if the return type for this method call is the same as the
	 *         target type to look for otherwise false
	 */
	private boolean isReturnTypeTargetType(final MethodUsage pResolvedMethodCall, final Class<?> pTargetType) {
		return pResolvedMethodCall.returnType().isReferenceType()
				&& pResolvedMethodCall.returnType().describe().equals(pTargetType.getName());
	}
}
