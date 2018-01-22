package code.cafebabe.refactoring;

import java.util.LinkedHashSet;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.action.Action;

public abstract class Refactoring {

	public static class RefactoringBuilder {

		private Action action;
		private Class<?> target;
		private Class<?> replacement;

		public RefactoringBuilder(final Action pAction) {
			action = pAction;
		}

		public static RefactoringBuilder ofAction(final Action pAction) {
			return new RefactoringBuilder(pAction);
		}

		public RefactoringBuilder andTarget(final Class<?> pTarget) {
			target = pTarget;
			return this;
		}

		public Refactoring build() {
			if (target == null) {
				throw new IllegalStateException("No target to look for! Call \"andTarget\" first!");
			}
			return new MethodCallRefactoring(target, action, replacement);
		}

		public RefactoringBuilder andReplacement(final Class<?> pClassToReplaceWith) {
			replacement = pClassToReplaceWith;
			return this;
		}

	}

	protected final Class<?> targetType;
	protected final Class<?> replacement;

	public Refactoring(final Class<?> pTargetType, final Class<?> pReplacement) {
		targetType = pTargetType;
		replacement = pReplacement;
	}

	private Set<VariableDeclarationExpr> resolveVariableDeclarations(
			final MethodDeclaration pMethodDeclarationToProcess, final Class<?> pTargetType) {
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

	protected Set<FieldDeclaration> resolveFieldDeclarations(final CompilationUnit pCompilataionUnit,
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

	protected boolean doImportsContainTargetType(final CompilationUnit pCompilationUnitToCheck,
			final Class<?> pTargetTypeToLookFor) {
		return 1 == Navigator.findAllNodesOfGivenClass(pCompilationUnitToCheck, ImportDeclaration.class).stream()
				.map(ImportDeclaration::getNameAsString).filter(pTargetTypeToLookFor.getName()::equals).count();
	}

	public abstract <T extends Node> boolean isApplyable(final T pNode, final Class<?> pTargetType,
			TypeSolver pMySolver);

	public abstract CompilationUnit apply(final CompilationUnit pCompilataionUnit, final TypeSolver pTypeSolver);

}