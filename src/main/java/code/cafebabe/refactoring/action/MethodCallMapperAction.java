package code.cafebabe.refactoring.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class MethodCallMapperAction extends Action {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodCallMapperAction.class);

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
        try {
            return pNode instanceof MethodCallExpr && isDeclaringTypeTargetType(JavaParserFacade.get(pTypeSolver).solveMethodAsUsage((MethodCallExpr) pNode), pTargetType)
                    && methodCallMapping.containsKey(((MethodCallExpr) pNode).getNameAsString()) && !methodCallMapping.get(((MethodCallExpr) pNode).getNameAsString()).isEmpty();
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

	@Override
	public void consume(List<Node> pNodesToProcess, Set<FieldDeclaration> matchingFieldDeclarationsForTargetType,
			Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {
		for (Node next : pNodesToProcess) {
			final MethodCallExpr call = (MethodCallExpr) next;
			if (call.getNameAsString() != null && !call.getNameAsString().isEmpty()) {
                logger.debug("Replacing \"" + call.getNameAsString() + "\" with \"" + methodCallMapping.get(call.getNameAsString()) + "\"");
                call.setName(methodCallMapping.get(call.getNameAsString()));
			} else {
			    logger.error("Error - Empty string for: " + call.getNameAsString() + " -> " + call);
			}
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
        try {
            for (Entry<String, String> importMapping : importMappings.entrySet()) {
                if (imports.containsKey(importMapping.getKey())) {
                    pImports.remove(imports.get(importMapping.getKey()));
                }
                pImports.add(new ImportDeclaration(JavaParser.parseName(importMapping.getValue()), false, false));
            }
        } catch (RuntimeException e) {
            logger.error("RuntimeError! ", e);
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
