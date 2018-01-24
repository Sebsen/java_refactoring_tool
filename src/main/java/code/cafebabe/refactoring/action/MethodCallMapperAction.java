package code.cafebabe.refactoring.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class MethodCallMapperAction extends Action {

	private HashMap<String, String> methodCallMapping = new HashMap<>();
	private HashMap<String, String> importMappings = new HashMap<>();
	private final String newFieldInitializerExpression;

	public MethodCallMapperAction(final Map<String, String> pDesiredMapping, Map<String, String> pImportMapping,
			final String pNewFieldInitializerExpression) {
		methodCallMapping.putAll(pDesiredMapping);
		importMappings.putAll(pImportMapping);
		newFieldInitializerExpression = pNewFieldInitializerExpression;
	}

	@Override
	public <T extends Node> boolean isApplyable(T pNode, String pTargetType, TypeSolver pTypeSolver) {
		return pNode instanceof MethodCallExpr && isDeclaringTypeTargetType(
				JavaParserFacade.get(pTypeSolver).solveMethodAsUsage((MethodCallExpr) pNode), pTargetType);
	}

	@Override
	public void consume(List<Node> pNodesToProcess, Set<FieldDeclaration> matchingFieldDeclarationsForTargetType,
			Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {
		for (Node next : pNodesToProcess) {
			final MethodCallExpr call = (MethodCallExpr) next;
			call.setName(methodCallMapping.get(call.getNameAsString()));
		}
	}

	@Override
	public void consumeFieldDeclarations(final Set<FieldDeclaration> pFieldDeclarations) {
		if (newFieldInitializerExpression.isEmpty()) {
			return;
		}
		for (final FieldDeclaration fieldDeclaration : pFieldDeclarations) {
			for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
				// Replace old VariableInitializerExpression by new provided one
				// (for matching fields of target type)
				variableDeclarator.getInitializer()
						.ifPresent(i -> i.replace(JavaParser.parseExpression(newFieldInitializerExpression)));
			}
		}
	}

	@Override
	public void consumeImports(List<ImportDeclaration> pImports, String pTargetType) {
		Map<String, ImportDeclaration> imports = pImports.stream()
				.collect(Collectors.toMap(ImportDeclaration::getNameAsString, e -> e));
		for (Entry<String, String> importMapping : importMappings.entrySet()) {
			if (imports.containsKey(importMapping.getKey())) {
				pImports.remove(imports.get(importMapping.getKey()));
			}
			pImports.add(new ImportDeclaration(JavaParser.parseName(importMapping.getValue()), false, false));
		}
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
