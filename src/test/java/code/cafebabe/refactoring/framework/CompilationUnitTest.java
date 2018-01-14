package code.cafebabe.refactoring.framework;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileNotFoundException;

import org.junit.jupiter.api.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

@RunWith(JUnitPlatform.class)
public class CompilationUnitTest {

    private static final String UGLY_TEST_FILE = "public class UglyTestFile {\r\n    public void foo(\r\n            int e\r\n        ) {\r\n        int a\r\n                    = 20;\r\n    }\r\n}";

    @Test
    public void compilationUnitIsPrintedAsIs() throws FileNotFoundException {
        final CompilationUnit cu = LexicalPreservingPrinter.setup(JavaParser.parse(new File("src/test/resources/UglyTestFile.java")));
        assertEquals("The lexical preservation of original and parsed source file is lost!", UGLY_TEST_FILE, LexicalPreservingPrinter.print(cu));
    }

}
