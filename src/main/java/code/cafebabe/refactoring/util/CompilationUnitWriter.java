package code.cafebabe.refactoring.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.utils.Pair;

public class CompilationUnitWriter {

	private CompilationUnitWriter() {
	}

	/**
	 * Writes back the compilation unit stored in the compilation unit wrapper
	 * object into the it's original file (also stored in wrapper object)
	 * 
	 * @param pCompilationUnit
	 *            The compilation unit wrapper object holding the compilation
	 *            unit being written into it's original file (also stored in the
	 *            wrapper object )
	 * @throws IOException
	 */
	public static void writeToFile(final Pair<File, CompilationUnit> pCompilationUnit) throws IOException {
		writeToFile(pCompilationUnit, pCompilationUnit.a);
	}

	/**
	 * Writes the compilation unit contained in the wrapper object into the
	 * specified file.
	 * 
	 * @see #writeToFile(CompilationUnit, File)
	 * @param pCompilationUnit
	 *            The wrapper object containing the compilation unit to write to
	 *            the specified file
	 * @param pNewFile
	 *            The file to write the compilation unit to
	 * @throws IOException
	 */
	public static void writeToFile(final Pair<File, CompilationUnit> pCompilationUnit, final File pNewFile)
			throws IOException {
		writeToFile(pCompilationUnit.b, pNewFile);
	}

	/**
	 * Writes a compilation unit to a specified file.
	 * 
	 * @param pCompilationUnit
	 *            The compilation unit which should be written to the specified
	 *            file
	 * @param pNewFile
	 *            The file to write the compilation unit to
	 * @throws IOException
	 */
	public static void writeToFile(final CompilationUnit pCompilationUnit, final File pNewFile) throws IOException {
		try (final BufferedWriter oos = new BufferedWriter(new FileWriter(pNewFile))) {
			oos.write(LexicalPreservingPrinter.print(pCompilationUnit));
		}
	}

}
