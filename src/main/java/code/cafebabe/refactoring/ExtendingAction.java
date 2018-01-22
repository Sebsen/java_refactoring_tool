package code.cafebabe.refactoring;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.Statement;

public final class ExtendingAction extends Action {

    @Override
    public void consume(final List<Node> pNodesToProcess, final Optional<FieldDeclaration> p) {
        // Workaround to add multiple children from same node in LexicalPreserving printer => Save former parent
        Optional<BlockStmt> parent = Optional.empty();
        for (final Node n : pNodesToProcess) {
            parent = parent.isPresent() ? parent : n.findParent(BlockStmt.class);
            parent.ifPresent(surroundingBlock -> {
                final NodeList<Statement> newStatements = new NodeList<>();
                Iterator<Node> iterator = surroundingBlock.getChildNodes().iterator();
                while (iterator.hasNext()) {
                    Statement next = (Statement) iterator.next();
                    Statement child = copy(next);
                    newStatements.add(child);
                    if (pNodesToProcess.contains(child)) {
                        process(p, newStatements, child);
                    }
                }
                surroundingBlock.replace(new BlockStmt(newStatements));
            });
        }
    }

    private void process(final Optional<FieldDeclaration> p, final NodeList<Statement> newStatements, Statement child) {
        final Optional<MethodCallExpr> firstContainedMethodCall = child.findFirst(MethodCallExpr.class);
        if (firstContainedMethodCall.isPresent()) {
            
            final MethodCallExpr methodCallExpr = firstContainedMethodCall.get().clone();
            Expression expression = methodCallExpr.getScope().get();
            if (expression.isNameExpr()) {
                expression.asNameExpr().setName(p.get().findFirst(VariableDeclarator.class).get().getNameAsString());
            } else if (expression.isFieldAccessExpr()) {
                expression.asFieldAccessExpr().getName().setIdentifier(p.get().findFirst(VariableDeclarator.class).get().getNameAsString());
            }
            newStatements.add(new ExpressionStmt(methodCallExpr));
        }
    }

    private Statement copy(final Statement next) {
        if (next.isExpressionStmt()) {
            ExpressionStmt expressionStmt = new ExpressionStmt(next.asExpressionStmt().getExpression());
            next.getRange().ifPresent(r -> {
                expressionStmt.setRange(r);
            });
            return expressionStmt;
        }
        return next.clone();
    }

    public void consumeOld(final List<Node> pNodesToProcess) {
        // Workaround to add multiple children from same node in LexicalPreserving printer => Save former parent
        Optional<BlockStmt> parent = Optional.empty();
        for (final Node n : pNodesToProcess) {
            parent = parent.isPresent() ? parent : n.findParent(BlockStmt.class);
            parent.ifPresent(surroundingBlock -> {
                final Optional<MethodCallExpr> firstContainedMethodCall = n.findFirst(MethodCallExpr.class);
                if (firstContainedMethodCall.isPresent()) {
                    final MethodCallExpr methodCallExpr = new MethodCallExpr();
                    methodCallExpr.setName(firstContainedMethodCall.get().getName());
                    methodCallExpr.setArguments(firstContainedMethodCall.get().getArguments());
                    surroundingBlock.addStatement(methodCallExpr);
                }
            });
        }
    }

    @Override
    public void consumeFieldDeclarations(final Set<FieldDeclaration> pFieldDeclarations) {
        // TODO Auto-generated method stub

    }

    @Override
    public void consumeImports(final CompilationUnit pCompilationUnit, final Class<?> pTargetType) {
        // TODO Auto-generated method stub

    }

}
