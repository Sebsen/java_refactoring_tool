package code.cafebabe.refactoring;

import java.io.File;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.Pair;

/**
 * Mutable wrapper class for {@link com.github.javaparser.ast.CompilationUnit
 * CompilationUnit} class. It wraps a given CompilationUnit and associates it to
 * an given file. This file might be it's original source file but could also be
 * a different one (like a different target file).
 * 
 * @author Sebastian
 *
 */
public class CompilationUnitWrapper {

	private final Pair<File, CompilationUnit> wrappedObjects;

	private CompilationUnitWrapper(final Pair<File, CompilationUnit> pPairToWrap) {
		this.wrappedObjects = pPairToWrap;
	}

	public File getSourceFile() {
		return wrappedObjects.a;
	}

	public CompilationUnit getCompilationUnit() {
		return wrappedObjects.b;
	}

	public static CompilationUnitWrapper createCompilationUnitWrapper(final File pFile,
			final CompilationUnit pCompilationUnit) {
		return new CompilationUnitWrapper(new Pair<>(pFile, pCompilationUnit));
	}

	public static CompilationUnitWrapper createCompilationUnitWrapper(final Pair<File, CompilationUnit> pPairToWrap) {
		return createCompilationUnitWrapper(pPairToWrap.a, pPairToWrap.b);
	}

}
