package code.cafebabe.refactoring.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.Change;
import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.CompilationUnitWrapper;
import code.cafebabe.refactoring.Refactoring;
import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import code.cafebabe.refactoring.factory.TypeSolverFactory;

public class SingleThreadedProcessor extends RefactoringProcessor {

	@Override
	protected Set<Change> processRefactorings(final CodeBase pCodeBase, final Refactoring pRefactoring) {

		// Create type solver from CodeBase
		final TypeSolver mySolver = TypeSolverFactory.createFrom(pCodeBase);
		final Set<Change> changes = new LinkedHashSet<>();

		final Iterator<File> codeBaseIterator = pCodeBase.iterator();
		while (codeBaseIterator.hasNext()) {
			final File next = codeBaseIterator.next();

			// Create new preserving compilation unit
			CompilationUnitWrapper cu = null;
			try {
				cu = CompilationUnitFactory.createPreservingCompilationUnit(next, mySolver);
			} catch (FileNotFoundException e) {
				// Should never occur since we're processing a codebase and it
				// verifies it's contained files exist
				e.printStackTrace();
			}

			// Apply change
			changes.add(Change.createFrom(cu, pRefactoring.apply(cu.getCompilationUnit(), mySolver)));
		}

		return changes;
	}

	@Override
	protected Set<Change> reviewRefactorings(final Set<Change> pChangesToReview) {
		return pChangesToReview;
	}

	@Override
	protected void persistChanges(final Set<Change> pChangesToPersist) {
		// Persist changes by simply storing it into it's original source files
		
		// CompilationUnitWriter.writeToFile(changed, new
		// File(cu.getSourceFile().getParent(), "out.java"));

	}

}
