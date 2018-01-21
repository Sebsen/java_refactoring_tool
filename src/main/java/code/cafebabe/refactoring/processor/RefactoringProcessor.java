package code.cafebabe.refactoring.processor;

import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import code.cafebabe.refactoring.Refactoring;
import code.cafebabe.refactoring.CodeBase;

public abstract class RefactoringProcessor {

	public Set<CompilationUnit> process(final CodeBase pCodeBase, final Refactoring pRefactoring) {
		final Set<CompilationUnit> changes = processRefactorings(pCodeBase, pRefactoring);
		reviewRefactorings();
		writeRefactoringsToFile();
		return changes;
	}
	
	protected abstract Set<CompilationUnit> processRefactorings(final CodeBase pCodeBase, final Refactoring pRefactoring);
	protected abstract void reviewRefactorings();
	protected abstract void writeRefactoringsToFile();
	
}
