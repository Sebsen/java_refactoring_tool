package code.cafebabe.refactoring.processor;

import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import code.cafebabe.refactoring.Change;
import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.Refactoring;

public abstract class RefactoringProcessor {

	public Set<Change> process(final CodeBase pCodeBase, final Refactoring pRefactoring) {
		final Set<Change> changes = processRefactorings(pCodeBase, pRefactoring);
		reviewRefactorings();
		writeRefactoringsToFile();
		return changes;
	}
	
	protected abstract Set<Change> processRefactorings(final CodeBase pCodeBase, final Refactoring pRefactoring);
	protected abstract void reviewRefactorings();
	protected abstract void writeRefactoringsToFile();
	
}
