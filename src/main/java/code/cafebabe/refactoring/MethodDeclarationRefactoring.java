package code.cafebabe.refactoring;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.google.common.collect.Lists;

import code.cafebabe.refactoring.action.Action;

public class MethodDeclarationRefactoring extends Refactoring {
	
	private static final Logger logger = LoggerFactory.getLogger(MethodDeclarationRefactoring.class);

	private boolean trustImportStatements = true;
	private final Action action;

	MethodDeclarationRefactoring(final String pTargetType, final Action pAction, final String pReplacement) {
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
		// debugMatchingFields(matchingFieldDeclarations);

		final List<MethodDeclaration> methodsToProcess = Navigator.findAllNodesOfGivenClass(pCompilationUnit,
				MethodDeclaration.class);
		methodsToProcess.forEach(m -> {
			// Process every method in class from bottom to up
			final List<Node> nodesToProcess = Lists.reverse(m.findFirst(BlockStmt.class).get().getChildNodes()).stream()
					.filter(child -> action.isApplyable(child, targetType, pTypeSolver)).collect(Collectors.toList());

			action.consume(nodesToProcess, matchingFieldDeclarationsForTargetType, matchingFieldDeclarationsForReplacementType);
		});
		action.consumeFieldDeclarations(matchingFieldDeclarationsForTargetType);
		action.consumeImports(pCompilationUnit.getImports(), targetType);

		return pCompilationUnit;
	}

	private void debugMatchingFields(final Set<FieldDeclaration> matchingFieldDeclarations) {
		logger.info("Matching FieldDeclarations: " + matchingFieldDeclarations);
	}

}
