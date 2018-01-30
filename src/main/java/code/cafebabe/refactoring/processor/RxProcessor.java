package code.cafebabe.refactoring.processor;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.Change;
import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.CompilationUnitWrapper;
import code.cafebabe.refactoring.Refactoring;
import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import code.cafebabe.refactoring.factory.TypeSolverFactory;
import code.cafebabe.refactoring.util.CompilationUnitWriter;
import io.reactivex.Flowable;
import io.reactivex.schedulers.Schedulers;

public class RxProcessor {

	private static final Logger logger = LoggerFactory.getLogger(SingleThreadedProcessor.class);

	private final boolean isDryRun;
	private int initialCodeBaseSize;
	private int changedFiles;

	public RxProcessor(final boolean pIsDryRun) {
		isDryRun = pIsDryRun;
	}

	public final void process(final CodeBase pCodeBase, final Refactoring pRefactoring) {
		initialCodeBaseSize = pCodeBase.size();

		// Create type solver from CodeBase
		final TypeSolver mySolver = TypeSolverFactory.createFrom(pCodeBase);

		Flowable.fromIterable(pCodeBase).subscribeOn(Schedulers.io())
				.map((File file) -> CompilationUnitFactory.createPreservingCompilationUnit(file, mySolver))
				.map((CompilationUnitWrapper compilationUnitWrapper) -> Change.createFrom(compilationUnitWrapper,
						pRefactoring.apply(compilationUnitWrapper.getCompilationUnit(), mySolver)))
				.filter((Change change) -> change.getTransformed() != null).blockingSubscribe((Change change) -> {
					changedFiles++;
					if (!isDryRun) {
						// Persist changes by simply storing it into it's
						// original source
						// files
						try {
							logger.info("Writing changes to file: " + change.getOriginal().getSourceFile());
							CompilationUnitWriter.writeToFile(change.getTransformed(),
									change.getOriginal().getSourceFile());
						} catch (IOException e) {
							logger.error(
									"Error writing changes to file: " + change.getOriginal().getSourceFile() + "!");
						}
					} else {
						// logger.info("Files that would change [" +
						// pChangesToPersist.size()
						// + " in total from originally " +
						// initialCodeBaseSize + "]:");
						logger.info("File that would change: " + change.getOriginal().getSourceFile().getPath());
					}
				}, error -> logger.error("Error while subscribing to flowable!", error), () -> {
					logger.info("Completed subscription!");
					logger.info("Number of files which would have changed: {} (from originally {})", changedFiles,
							initialCodeBaseSize);
				});
	}
}
