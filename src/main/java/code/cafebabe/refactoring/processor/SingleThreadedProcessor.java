package code.cafebabe.refactoring.processor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.Change;
import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.CompilationUnitWrapper;
import code.cafebabe.refactoring.Refactoring;
import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import code.cafebabe.refactoring.factory.TypeSolverFactory;
import code.cafebabe.refactoring.util.CompilationUnitWriter;

public class SingleThreadedProcessor extends RefactoringProcessor {

	private static final Logger logger = LoggerFactory.getLogger(SingleThreadedProcessor.class);

	private final boolean isDryRun;
	private int initialCodeBaseSize;

	public SingleThreadedProcessor(final boolean pIsDryRun) {
		isDryRun = pIsDryRun;
	}

	@Override
	protected Set<Change> processRefactorings(final CodeBase pCodeBase, final Refactoring pRefactoring) {
		initialCodeBaseSize = pCodeBase.size();

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
				logger.error("Could not create CompilationUnit of file: " + next, e);
			}

			// Apply change
			CompilationUnit changedCompilationUnit = pRefactoring.apply(cu.getCompilationUnit(), mySolver);
			if (changedCompilationUnit != null) {
				changes.add(Change.createFrom(cu, changedCompilationUnit));
			}
		}

		return changes;
	}

	@Override
	protected Set<Change> reviewRefactorings(final Set<Change> pChangesToReview) {
		return pChangesToReview;
	}

	@Override
	protected void persistChanges(final Set<Change> pChangesToPersist) {
		if (!isDryRun) {
			// Persist changes by simply storing it into it's original source
			// files
			for (Change change : pChangesToPersist) {
				try {
					logger.info("Writing changes to file: " + change.getOriginal().getSourceFile());
					CompilationUnitWriter.writeToFile(change.getTransformed(), change.getOriginal().getSourceFile());
				} catch (IOException e) {
					logger.error("Error writing changes to file: " + change.getOriginal().getSourceFile() + "!");
				}
			}
		} else {
			logger.info("Files that would change [" + pChangesToPersist.size() + " in total from originally "
					+ initialCodeBaseSize + "]:");
			for (final Change change : pChangesToPersist) {
				logger.info("\t" + change.getOriginal().getSourceFile().getPath());
			}
		}
	}

}
