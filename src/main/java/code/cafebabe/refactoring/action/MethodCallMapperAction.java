package code.cafebabe.refactoring.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class MethodCallMapperAction extends Action {

	private HashMap<String, String> methodCallMapping = new HashMap<>();

	public MethodCallMapperAction(final Map<String, String> pDesiredMapping) {
		methodCallMapping.putAll(pDesiredMapping);
	}


	@Override
	public <T extends Node> boolean isApplyable(T pNode, String pTargetType, TypeSolver pTypeSolver) {
		return pNode instanceof MethodCallExpr && isDeclaringTypeTargetType(
				JavaParserFacade.get(pTypeSolver).solveMethodAsUsage((MethodCallExpr) pNode), pTargetType);
	}

	@Override
	public void consume(List<Node> pNodesToProcess, Set<FieldDeclaration> matchingFieldDeclarationsForTargetType,
			Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {
		// TODO Auto-generated method stub
	}

	@Override
	public void consumeFieldDeclarations(Set<FieldDeclaration> pFieldDeclarations) {
		// TODO Auto-generated method stub
	}

	@Override
	public void consumeImports(List<ImportDeclaration> imports, String pTargetType) {
		// TODO Auto-generated method stub
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

}
