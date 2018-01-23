package code.cafebabe.refactoring.action;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public final class RemovingAction extends Action {

	@Override
	public void consume(final List<Node> pNodesToProcess, Set<FieldDeclaration> matchingFieldDeclarationsForTargetType,
			Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {
		// Workaround to remove multiple children from same node in
		// LexicalPreserving printer => Save former parent
		Optional<Node> parent = Optional.empty();
		for (final Node node : pNodesToProcess) {
			parent = parent.isPresent() ? parent : node.getParentNode();
			parent.ifPresent(n -> n.remove(node));
		}
	}

	@Override
	public void consumeFieldDeclarations(final Set<FieldDeclaration> pFieldDeclarations) {
		pFieldDeclarations.forEach(FieldDeclaration::remove);
	}

	@Override
	public void consumeImports(final List<ImportDeclaration> pImports, final String pTargetType) {
		Set<ImportDeclaration> importsToRemove = pImports.stream().filter(i -> i != null)
				.filter(i -> pTargetType.equals(i.getNameAsString())).collect(Collectors.toSet());
		importsToRemove.forEach(ImportDeclaration::remove);
	}

	@Override
	public <T extends Node> boolean isApplyable(T pNode, String pTargetType, TypeSolver pTypeSolver) {
		if (pNode instanceof ExpressionStmt) {
			return isApplyable((ExpressionStmt) pNode, pTargetType, pTypeSolver);
		}
		return false;
	}

	private boolean isApplyable(final ExpressionStmt pExpression, final String pTargetType,
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
	private boolean isDeclaringTypeTargetType(final MethodUsage pResolvedMethodCall, final String pTargetType) {
		return pResolvedMethodCall.declaringType().getQualifiedName().equals(pTargetType);
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
	private boolean isReturnTypeTargetType(final MethodUsage pResolvedMethodCall, final String pTargetType) {
		return pResolvedMethodCall.returnType().isReferenceType()
				&& pResolvedMethodCall.returnType().describe().equals(pTargetType);
	}

}