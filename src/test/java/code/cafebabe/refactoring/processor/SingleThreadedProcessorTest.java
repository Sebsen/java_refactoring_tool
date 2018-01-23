package code.cafebabe.refactoring.processor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Optional;
import java.util.Set;

import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;

import code.cafebabe.refactoring.Change;
import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.MethodCallRefactoring;
import code.cafebabe.refactoring.Refactoring;
import code.cafebabe.refactoring.action.RemovingAction;
import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import complexClass.custom.Logger;

@RunWith(JUnitPlatform.class)
public class SingleThreadedProcessorTest {

	private static final File TEST_RESOURCES_BASE = new File("src/test/resources/");
	private static final File TEST_RESOURCES_TARGETS = new File(TEST_RESOURCES_BASE, "targets");

	@Test
	public void test() throws FileNotFoundException {
		final Refactoring change = MethodCallRefactoring.RefactoringBuilder.of(new RemovingAction())
				.andTarget("complexClass.custom.Logger").build();
		final String testFolderName = "complexClass/";
		final String testFileName = "ComplexClass.java";
		final String targetFileName = "ComplexClassAfterRemoval.java";
		final File testBase = new File(TEST_RESOURCES_BASE, testFolderName);
		final File target = new File(TEST_RESOURCES_TARGETS, testFolderName + targetFileName);

		final CodeBase codeBase = CodeBase.CodeBaseBuilder.fromRoots(testBase).addJarRoot("lib/slf4j-api-1.7.25.jar")
				.build();

		final Set<Change> changes = new SingleThreadedProcessor().process(codeBase, change);

		final Optional<String> transformed = changes.stream()
				.filter(c -> testFileName.equals(c.getOriginal().getSourceFile().getName())).map(Change::getTransformed)
				.map(LexicalPreservingPrinter::print).findFirst();

		if (!transformed.isPresent()) {
			fail("Missing transformed file: \"" + targetFileName + "\"!");
		}

		assertEquals(
				LexicalPreservingPrinter
						.print(CompilationUnitFactory.createPreservingCompilationUnit(target).getCompilationUnit()),
				transformed.get());
	}

	@Test
	public void methodCallInstanceIsConvertedToField() throws FileNotFoundException {
		final Refactoring change = Refactoring.RefactoringBuilder.of(new RemovingAction())
				.andTarget("complexClass.custom.Logger").build();
		final String testFolderName = "introduceField/";
		final String testFileName = "MethodCallClass.java";
		final String targetFileName = "MethodCallClassTarget.java";
		final File testBase = new File(TEST_RESOURCES_BASE, testFolderName);
		final File target = new File(TEST_RESOURCES_TARGETS, testFolderName + targetFileName);
		
		final CodeBase codeBase = CodeBase.CodeBaseBuilder.fromRoots(testBase).addJarRoot("lib/slf4j-api-1.7.25.jar")
				.build();
		
		final Set<Change> changes = new SingleThreadedProcessor().process(codeBase, change);
		
		final Optional<String> transformed = changes.stream()
				.filter(c -> testFileName.equals(c.getOriginal().getSourceFile().getName())).map(Change::getTransformed)
				.map(LexicalPreservingPrinter::print).findFirst();
		
		if (!transformed.isPresent()) {
			fail("Missing transformed file: \"" + targetFileName + "\"!");
		}
		
		assertEquals(
				LexicalPreservingPrinter
				.print(CompilationUnitFactory.createPreservingCompilationUnit(target).getCompilationUnit()),
				transformed.get());
	}

}
