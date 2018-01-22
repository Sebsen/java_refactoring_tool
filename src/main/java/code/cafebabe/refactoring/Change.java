package code.cafebabe.refactoring;

import com.github.javaparser.ast.CompilationUnit;

/**
 * Class which should represent which changes have been applied to a code base
 * while processing it.
 * 
 * @author Sebastian
 *
 */
public final class Change {

	private CompilationUnitWrapper compilationUnitWrapper;
	// TODO: Instead of storing compilation unit before and after only list of
	// diffs (or actions or similar) should be stored here. All diffs
	// applied to base code base then lead to final changed one => Each
	// individual change/ diff then could be reviewed
	// and individually approved or rejected
	private CompilationUnit changed;

	private Change(final CompilationUnitWrapper pCompilationUnitWrapper, final CompilationUnit pChangedCompilationUnit) {
		compilationUnitWrapper = pCompilationUnitWrapper;
		changed = pChangedCompilationUnit;
	}

	public static Change createFrom(final CompilationUnitWrapper pCompilationUnitWrapper,
			final CompilationUnit pChangedCompilationUnit) {
		return new Change(pCompilationUnitWrapper, pChangedCompilationUnit);
	}
}
