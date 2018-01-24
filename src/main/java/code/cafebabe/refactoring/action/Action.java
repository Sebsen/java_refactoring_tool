package code.cafebabe.refactoring.action;

import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public abstract class Action {

	public abstract <T extends Node> boolean isApplyable(final T pNode, final String pTargetType, TypeSolver pMySolver);

	public abstract void consume(final List<Node> pNodesToProcess,
			Set<FieldDeclaration> matchingFieldDeclarationsForTargetType,
			Set<FieldDeclaration> matchingFieldDeclarationsForReplacementType);

	public abstract void consumeFieldDeclarations(final Set<FieldDeclaration> pFieldDeclarations);

	public abstract void consumeImports(final List<ImportDeclaration> imports, final String pTargetType);

}