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

	private CompilationUnitWrapper original;
	// TODO: Instead of storing compilation unit before and after only list of
	// diffs (or actions or similar) should be stored here. All diffs
	// applied to base code base then lead to final changed one => Each
	// individual change/ diff then could be reviewed
	// and individually approved or rejected
	private CompilationUnit transformed;

	private Change(final CompilationUnitWrapper pCompilationUnitWrapper, final CompilationUnit pChangedCompilationUnit) {
		original = pCompilationUnitWrapper;
		transformed = pChangedCompilationUnit;
	}

	public CompilationUnit getTransformed() {
		// TODO: Should return a clone of current compilation unit (to be immutable) but is currently not possible due to issues with
		// lexical preservation and issues with java-parser library
		return transformed;
	}

	public CompilationUnitWrapper getOriginal() {
		return original;
	}
	
	public static Change createFrom(final CompilationUnitWrapper pCompilationUnitWrapper,
			final CompilationUnit pChangedCompilationUnit) {
		return new Change(pCompilationUnitWrapper, pChangedCompilationUnit);
	}
}
