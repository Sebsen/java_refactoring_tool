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
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class MethodCallMapperAction extends Action {

	private HashMap<String, String> methodCallMapping = new HashMap<>();
	private HashMap<String, String> importMappings = new HashMap<>();

	public MethodCallMapperAction(final Map<String, String> pDesiredMapping, Map<String, String> pImportMapping) {
		methodCallMapping.putAll(pDesiredMapping);
		importMappings.putAll(pImportMapping);
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
		for (final FieldDeclaration fieldDeclaration : pFieldDeclarations) {
			for (VariableDeclarator variableDeclarator : fieldDeclaration.getVariables()) {
				variableDeclarator.getInitializer().ifPresent(i -> {
					SimpleName parsedSimpleName = JavaParser.parseSimpleName("getLogger");
					Expression parsedExpression = JavaParser.parseExpression("LoggerFactory");
					MethodCallExpr newInitializer = new MethodCallExpr(parsedExpression, parsedSimpleName);
					newInitializer.addArgument(i.findCompilationUnit().get()
							.findFirst(ClassOrInterfaceDeclaration.class).get().getNameAsString() + ".class");
					i.replace(newInitializer);
				});
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
