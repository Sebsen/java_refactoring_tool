package code.cafebabe.refactoring.action;

import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public final class FieldConverterAction extends Action {

	@Override
	public void consume(List<Node> pNodesToProcess, Set<FieldDeclaration> matchingFieldDeclarationsForTargetType,
			Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {
		if (pNodesToProcess.isEmpty()) {
			return;
		}
		System.out.println(pNodesToProcess);
	}

	@Override
	public void consumeFieldDeclarations(Set<FieldDeclaration> pFieldDeclarations) {
		System.out.println(pFieldDeclarations);
	}

	@Override
	public void consumeImports(final List<ImportDeclaration> pImports, String pTargetType) {
	}

	@Override
	public <T extends Node> boolean isApplyable(T pNode, String pTargetType, TypeSolver pTypeSolver) {
		return pNode instanceof MethodCallExpr && isReturnTypeTargetType(
				JavaParserFacade.get(pTypeSolver).solveMethodAsUsage((MethodCallExpr) pNode), pTargetType);
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
