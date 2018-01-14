package code.cafebabe.refactoring;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.resolution.MethodUsage;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.google.common.collect.Lists;

public class MethodCallChange extends Change {

    private boolean trustImportStatements = true;
    private final Action action;

    private MethodCallChange(final Class<?> pTargetType, final Action pAction, final Class<?> pReplacement) {
        super(pTargetType, pReplacement);
        action = pAction;
    }

    @Override
    public <T extends Node> boolean isApplyable(T pNode, TypeSolver pMySolver) {
        return true;
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
                    .filter(child -> process(child, matchingFieldDeclarationsForTargetType, targetType, pTypeSolver))
                    .collect(Collectors.toList());

            action.consume(nodesToProcess, matchingFieldDeclarationsForReplacementType);
        });
        action.consumeFieldDeclarations(matchingFieldDeclarationsForTargetType);
        action.consumeImports(pCompilationUnit, targetType);

        return pCompilationUnit;
    }

    private void debugMatchingFields(final Set<FieldDeclaration> matchingFieldDeclarations) {
        System.out.println("Matching FieldDeclarations: " + matchingFieldDeclarations);
    }

    private boolean process(final Node pChild, Set<FieldDeclaration> pMatchingFieldDeclarations,
                            final Class<?> pTargetType, final TypeSolver pTypeSolver) {
        if (pChild instanceof ExpressionStmt) {
            return process((ExpressionStmt) pChild, pMatchingFieldDeclarations, pTargetType, pTypeSolver);
        }
        return false;
    }

    private boolean process(final ExpressionStmt pExpression, Set<FieldDeclaration> pMatchingFieldDeclarations,
                            final Class<?> pTargetType, final TypeSolver pTypeSolver) {
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
     * Checks if the declaring type of this MethodCall is the same as the target type to look for.
     * 
     * @param pResolvedMethodCall
     *            The resolved method call
     * @param pTargetType
     *            The target type to look for
     * @return true, if the declaring type for this method call is the same as the target type to look for otherwise
     *         false
     */
    private boolean isDeclaringTypeTargetType(final MethodUsage pResolvedMethodCall, final Class<?> pTargetType) {
        return pResolvedMethodCall.declaringType().getQualifiedName().equals(pTargetType.getName());
    }

    /**
     * Checks if the return type of this MethodCall is the same as the target type to look for.
     * 
     * @param pResolvedMethodCall
     *            The resolved method call
     * @param pTargetType
     *            The target type to look for
     * @return true, if the return type for this method call is the same as the target type to look for otherwise false
     */
    private boolean isReturnTypeTargetType(final MethodUsage pResolvedMethodCall, final Class<?> pTargetType) {
        return pResolvedMethodCall.returnType().isReferenceType()
                && pResolvedMethodCall.returnType().describe().equals(pTargetType.getName());
    }

    private Set<VariableDeclarationExpr> resolveVariableDeclarations(final MethodDeclaration pMethodDeclarationToProcess,
                                                                     final Class<?> pTargetType) {
        final Set<VariableDeclarationExpr> variableDeclarations = new LinkedHashSet<>();
        Navigator.findAllNodesOfGivenClass(pMethodDeclarationToProcess, VariableDeclarationExpr.class).forEach(v -> {
            final ResolvedType resolvedVariableType = v.calculateResolvedType();
            if (resolvedVariableType.isReferenceType()
                    && resolvedVariableType.describe().equals(targetType.getName())) {
                variableDeclarations.add(v);
            }
        });
        return variableDeclarations;
    }

    private Set<FieldDeclaration> resolveFieldDeclarations(final CompilationUnit pCompilataionUnit,
                                                           final Class<?> pTargetTypeToLookFor) {
        final Set<FieldDeclaration> matchingDeclarations = new LinkedHashSet<>();
        Navigator.findAllNodesOfGivenClass(pCompilataionUnit, FieldDeclaration.class).forEach(fieldDeclaration -> {
            final ResolvedFieldDeclaration resolvedFieldDeclaration = fieldDeclaration.resolve();
            if (pTargetTypeToLookFor != null && resolvedFieldDeclaration.getType().isReferenceType()
                    && resolvedFieldDeclaration.getType().describe().equals(pTargetTypeToLookFor.getName())) {
                matchingDeclarations.add(fieldDeclaration);
            }
        });
        return matchingDeclarations;
    }

    private boolean doImportsContainTargetType(final CompilationUnit pCompilationUnitToCheck,
                                               final Class<?> pTargetTypeToLookFor) {
        return 1 == Navigator.findAllNodesOfGivenClass(pCompilationUnitToCheck, ImportDeclaration.class).stream()
                .map(ImportDeclaration::getNameAsString)
                .filter(pTargetTypeToLookFor.getName()::equals).count();
    }

    public enum ActionType {
        REMOVAL, EXTENDER;
    }

    public static class ChangeBuilder {

        private Action action;
        private Class<?> target;
        private Class<?> replacement;

        public ChangeBuilder(final Action pAction) {
            action = pAction;
        }

        public static ChangeBuilder ofAction(final ActionType pAction) {
            switch (pAction) {
                case EXTENDER:
                    return new ChangeBuilder(new ExtendingAction());
                case REMOVAL:
                    return new ChangeBuilder(new RemovingAction());
                default:
                    throw new IllegalArgumentException();
            }
        }

        public ChangeBuilder andTarget(final Class<?> pTarget) {
            target = pTarget;
            return this;
        }

        public Change build() {
            if (target == null) {
                throw new IllegalStateException("No target to look for! Call \"andTarget\" first!");
            }
            return new MethodCallChange(target, action, replacement);
        }

        public ChangeBuilder andReplacement(final Class<?> pClassToReplaceWith) {
            replacement = pClassToReplaceWith;
            return this;
        }

    }
}
