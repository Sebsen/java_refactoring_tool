package code.cafebabe.refactoring.action;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public final class FieldConverterAction extends Action {

	@Override
	public void consume(List<Node> pNodesToProcess,
			Optional<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {
		System.out.println(pNodesToProcess);
	}

	@Override
	public void consumeFieldDeclarations(Set<FieldDeclaration> pFieldDeclarations) {
		System.out.println(pFieldDeclarations);
	}

	@Override
	public void consumeImports(final List<ImportDeclaration> pImports, String pTargetType) {
	}

	@Override
	public <T extends Node> boolean isApplyable(T pNode, String pTargetType, TypeSolver pMySolver) {
		return false;
	}

}
