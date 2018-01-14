package de.refactoring.factory;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.Test;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.utils.Pair;

import code.cafebabe.refactoring.factory.CompilationUnitFactory;

public class CompilationUnitFactoryTest {

    private static final String UGLY_TEST_FILE = "public class UglyTestFile {\r\n    public void foo(\r\n            int e\r\n        ) {\r\n        int a\r\n                    = 20;\r\n    }\r\n}";

    @Test
    public void compilationUnitIsPrintedAsIs() throws FileNotFoundException {
        final Pair<File, CompilationUnit> cu = CompilationUnitFactory.createPreservingCompilationUnit(new File("src/test/resources/UglyTestFile.java"));
        assertEquals("The lexical preservation of original and parsed source file is lost!", UGLY_TEST_FILE, LexicalPreservingPrinter.print(cu.b));
    }

    @Test
    public void compilationUnitIsPrintedAsIs2() throws FileNotFoundException {
        final Pair<File, CompilationUnit> cu = CompilationUnitFactory.createPreservingCompilationUnit(
                new File("src/test/resources/complexClass/complexClass/ComplexClass.java"));
        Navigator.findAllNodesOfGivenClass(cu.b, Expression.class).forEach(e -> {
            // if (e.isMethodCallExpr()) {
            // System.out.println("FoundExpression: " + e);
            // System.out.println("\tChildren: " + e.getChildNodes());
            // }
        });
        // assertEquals("The lexical preservation of original and parsed source file is lost!", UGLY_TEST_FILE, LexicalPreservingPrinter.print(cu.b));
    }

}
