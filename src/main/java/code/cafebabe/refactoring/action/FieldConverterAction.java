package code.cafebabe.refactoring.action;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

/**
 * This class is intended to convert a given method call expression on a given
 * target type and replace the method call expression by a field access
 * expression.
 * 
 * @author Sebastian
 *
 */
public final class FieldConverterAction extends Action {

	private static final Logger logger = LoggerFactory.getLogger(FieldConverterAction.class);
	
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
		// Anything to do with field declarations
	}

	@Override
	public void consumeImports(final List<ImportDeclaration> pImports, String pTargetType) {
		// Anything to do with imports
	}

	@Override
	public <T extends Node> boolean isApplyable(T pNode, String pTargetType, TypeSolver pTypeSolver) {
		try {
			return pNode instanceof MethodCallExpr && isReturnTypeTargetType(
					JavaParserFacade.get(pTypeSolver).solveMethodAsUsage((MethodCallExpr) pNode), pTargetType);
		} catch (RuntimeException e) {
			String typeName = "null";
			if (pNode.findCompilationUnit().isPresent()
					&& pNode.findCompilationUnit().get().findFirst(ClassOrInterfaceDeclaration.class).isPresent()) {
				typeName = pNode.findCompilationUnit().get()
						.findFirst(ClassOrInterfaceDeclaration.class).get().getNameAsString();
			}
			logger.error("Error while parsing type " + typeName + " causing node: " + pNode, e);
			return false;
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
