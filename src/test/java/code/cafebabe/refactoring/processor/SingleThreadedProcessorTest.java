package code.cafebabe.refactoring.processor;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import code.cafebabe.refactoring.Refactoring;
import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.MethodCallChange;
import code.cafebabe.refactoring.MethodCallChange.ActionType;
import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import complexClass.custom.Logger;

@RunWith(JUnitPlatform.class)
public class SingleThreadedProcessorTest {
	
	private static final File TEST_RESOURCES_BASE = new File("src/test/resources/");
    private static final File TEST_RESOURCES_TARGETS = new File(TEST_RESOURCES_BASE, "targets");

	@Test
	public void test() throws FileNotFoundException {
        final Refactoring change = MethodCallChange.ChangeBuilder.ofAction(ActionType.REMOVAL).andTarget(Logger.class).build();
        final String testFolderName = "complexClass/";
        final String testFileName = "ComplexClass.java";
        final String targetFileName = "ComplexClassAfterRemoval.java";
        final File testBase = new File(TEST_RESOURCES_BASE, testFolderName);
        final File target = new File(TEST_RESOURCES_TARGETS, testFolderName + targetFileName);

        final CodeBase codeBase = CodeBase.CodeBaseBuilder.fromRoots(testBase).addJarRoot("lib/slf4j-api-1.7.25.jar")
                .build();
        
        final Set<CompilationUnit> changes = new SingleThreadedProcessor().process(codeBase, change);
        
        final Set<String> changes2 = changes.stream().map(LexicalPreservingPrinter::print).collect(Collectors.toSet());
        
        assertTrue(changes2.contains(LexicalPreservingPrinter
                .print(CompilationUnitFactory.createPreservingCompilationUnit(target).getCompilationUnit())));

	}

}
