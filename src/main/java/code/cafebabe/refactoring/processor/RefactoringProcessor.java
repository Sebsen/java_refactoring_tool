package code.cafebabe.refactoring.processor;

import java.util.Collections;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;

import code.cafebabe.refactoring.Change;
import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.Refactoring;

public abstract class RefactoringProcessor {

	/**
	 * This method handles the main program flow of the refactoring of an code
	 * base. First it applies the provided refactorings on the given code base
	 * by calling the
	 * {@link RefactoringProcessor#processRefactorings(CodeBase, Refactoring)}
	 * method. Afterwards it calles the {@link #reviewRefactorings(Set)} where
	 * it post-processes the beforhand computed changes on the code base and
	 * gives you the ability to filter out undesired changes which then are NOT
	 * going to be persisted in any way!<br />
	 * Lastly the {@link #persistChanges(Set)} method is going to be called
	 * which then finally persists the changes in the specified way.
	 * 
	 * @param pCodeBase
	 *            The code base to operate on
	 * @param pRefactoring
	 *            The refactoring to execute on the code base
	 * @return All computed and approved changes after they having been
	 *         persisted
	 */
	public final Set<Change> process(final CodeBase pCodeBase, final Refactoring pRefactoring) {
		final Set<Change> changes = processRefactorings(pCodeBase, pRefactoring);
		final Set<Change> filteredChanges = Collections.unmodifiableSet(reviewRefactorings(changes));
		persistChanges(filteredChanges);
		return filteredChanges;
	}

	/**
	 * Actual code which processes refactorings on a code base and computes the
	 * changes planned to be executed (persisted) in the future
	 * 
	 * @param pCodeBase
	 *            The code base to operate on
	 * @param pRefactoring
	 *            The refactoring to apply to the files in the given code base
	 * @return The computed (and planned) changes
	 */
	protected abstract Set<Change> processRefactorings(final CodeBase pCodeBase, final Refactoring pRefactoring);

	/**
	 * Callback method which is executed after the entire code base (the source
	 * files contained within) is processed. Here you have the possibility to
	 * post-process all computed changes on the given code base and 'review' it.
	 * Since the Change class should be immutable (at least in future) remove
	 * any undesired change from the list of originally computed changes, so
	 * that it won't be fulfilled nor persisted in any other way!
	 * 
	 * @see #persistChanges(Set)
	 * 
	 * @param pChangesToReview
	 *            The changes originally computed by the refactoring tool
	 * 
	 * @return A set of changes which have been post-processed ('reviewed') and
	 *         approved and thus should be stored to file or otherwise
	 *         persisted.
	 */
	protected abstract Set<Change> reviewRefactorings(final Set<Change> pChangesToReview);

	/**
	 * Callback method which is executed after all changes have been processed
	 * by the {@link #reviewRefactorings(Set)} method. Overwrite method this
	 * method in your sence of 'persisting' the computed and approved changes.
	 * 
	 * @param pChangesToPersist
	 *            The changes which should be persisted
	 */
	protected abstract void persistChanges(final Set<Change> pChangesToPersist);

}
