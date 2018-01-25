package code.cafebabe.refactoring.action;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public final class FieldConverterAction extends Action {

	private final Set<String> desiredFieldNames = new LinkedHashSet<>();

	public FieldConverterAction(final List<String> pDesiredFieldNames) {
		desiredFieldNames.addAll(pDesiredFieldNames);
	}

	@Override
	public void consume(List<Node> pNodesToProcess, Set<FieldDeclaration> matchingFieldDeclarationsForTargetType,
			Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {

		if (!pNodesToProcess.isEmpty() && !matchingFieldDeclarationsForTargetType.isEmpty()) {
			final FieldDeclaration matchingField = matchingFieldDeclarationsForTargetType.iterator().next();

			for (Node nodeToProcess : pNodesToProcess) {
				MethodCallExpr convertedNode = (MethodCallExpr) nodeToProcess;
				convertedNode.replace(JavaParser.parseExpression(matchingField.getVariable(0).getNameAsString()));
			}
		}

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
		try {
			return pNode instanceof MethodCallExpr && isReturnTypeTargetType(
					JavaParserFacade.get(pTypeSolver).solveMethodAsUsage((MethodCallExpr) pNode), pTargetType);
		} catch (RuntimeException e) {
			System.out.println("Error causing node: " + pNode);
			pNode.findCompilationUnit().ifPresent(System.out::println);
			e.printStackTrace();
			return false;
//			throw e;
		}
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

	public Set<String> getDesiredFieldNames() {
		return new LinkedHashSet<>(desiredFieldNames);
	}
}
