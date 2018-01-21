package code.cafebabe.refactoring.processor;

import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import code.cafebabe.refactoring.Refactoring;
import code.cafebabe.refactoring.CodeBase;

public abstract class RefactoringProcessor {

	public Set<CompilationUnit> process(final CodeBase pCodeBase, final Refactoring pRefactoring) {
		final Set<CompilationUnit> changes = processChanges(pCodeBase, pRefactoring);
		reviewChanges();
		writeChangesToFile();
		return changes;
	}
	
	protected abstract Set<CompilationUnit> processChanges(final CodeBase pCodeBase, final Refactoring pRefactoring);
	protected abstract void reviewChanges();
	protected abstract void writeChangesToFile();
	
}
