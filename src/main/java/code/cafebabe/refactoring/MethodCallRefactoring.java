package code.cafebabe.refactoring;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.action.Action;

public class MethodCallRefactoring extends Refactoring {

	private boolean trustImportStatements = false;
	private final Action action;

	MethodCallRefactoring(final String pTargetType, final Action pAction, final String pReplacement) {
		super(pTargetType, pReplacement);
		action = pAction;
	}

	@Override
	public CompilationUnit apply(final CompilationUnit pCompilationUnit, final TypeSolver pTypeSolver) {
		if (trustImportStatements && !doImportsContainTargetType(pCompilationUnit, targetType)) {
			return pCompilationUnit;
		}

		final Set<FieldDeclaration> matchingFieldDeclarationsForTargetType = resolveFieldDeclarations(pCompilationUnit,
				targetType);
		final Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType = resolveFieldDeclarations(
				pCompilationUnit, replacement);

		final List<MethodDeclaration> methodsToProcess = pCompilationUnit.findAll(MethodDeclaration.class);

		methodsToProcess.forEach(m -> {
			// Process every method in class from bottom to up
			final List<Node> nodesToProcess = m.findFirst(BlockStmt.class).get().findAll(MethodCallExpr.class).stream()
					.filter(child -> action.isApplyable(child, targetType, pTypeSolver)).collect(Collectors.toList());

			action.consume(nodesToProcess, matchingFieldDeclarationsForTargetType, matchingFieldDeclarationsForReplacementType);
		});
		action.consumeFieldDeclarations(matchingFieldDeclarationsForTargetType);
		action.consumeImports(pCompilationUnit.getImports(), targetType);

		return pCompilationUnit;
	}

}
