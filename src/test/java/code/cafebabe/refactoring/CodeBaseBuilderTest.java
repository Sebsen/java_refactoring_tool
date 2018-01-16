package code.cafebabe.refactoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.Test;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.CodeBase.CodeBaseBuilder.FileNotFoundExceptionException;
import code.cafebabe.refactoring.CodeBaseRootFile;

@RunWith(JUnitPlatform.class)
public class CodeBaseBuilderTest {

    private static final String CLASS_NAME = CodeBaseBuilderTest.class.getSimpleName();
    private static final String SRC_TEST_BASE = "src/test/resources/" + CLASS_NAME;

    private enum TEST_DIRECTORIES {
        BASE(SRC_TEST_BASE),
        SINGLE_SOURCE_FILE(BASE, "folderWithSingleSourceFile"),
        TWO_SOURCE_FILES(BASE, "folderWithTwoSourceFiles"),
        TWO_SOURCE_FILES_IN_SUBPACKAGES(BASE, "folderWithTwoSourceFilesInSubPackages");

        private final TEST_DIRECTORIES parent;
        private final String path;

        private TEST_DIRECTORIES(final String pPath) {
            this(null, pPath);
        }

        private TEST_DIRECTORIES(final TEST_DIRECTORIES pParent, final String pPath) {
            parent = pParent;
            path = pPath;
        }

        public final String getPath() {
            return resolvePath(this, new StringBuilder()).toString();
        }

        private StringBuilder resolvePath(final TEST_DIRECTORIES pTestFile, final StringBuilder pBuiltPath) {
            if (pTestFile.hasParent()) {
                resolvePath(pTestFile.parent, pBuiltPath);
            }
            // If computed path is empty or contains "/" at end simply append path value otherwise append an additional "/"
            return pBuiltPath.length() == 0 || pBuiltPath.lastIndexOf("/") == pBuiltPath.length() ? pBuiltPath.append(pTestFile.path)
                    : pBuiltPath.append('/').append(pTestFile.path);
        }

        private final boolean hasParent() {
            return parent != null;
        }
    }

    @Test
    public void recursiveCodeBaseWithSingleSourceFileReturnsSingleSourceFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.SINGLE_SOURCE_FILE.getPath()).build();
        assertEquals("SingleSourceFile.java", builtCodeBase.iterator().next().getName());
    }

    @Test
    public void recursiveCodeBaseWithSingleSourceFileOnlyContainsOneSingleFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.SINGLE_SOURCE_FILE.getPath()).build();
        final Iterator<File> iterator = builtCodeBase.iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void nonRecursiveCodeBaseWithSingleSourceFileReturnsSingleSourceFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.SINGLE_SOURCE_FILE.getPath()).recursive(false).build();
        assertEquals("SingleSourceFile.java", builtCodeBase.iterator().next().getName());
    }

    @Test
    public void nonRecursiveCodeBaseWithSingleSourceFileOnlyContainsOneSingleFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.SINGLE_SOURCE_FILE.getPath()).recursive(false).build();
        final Iterator<File> iterator = builtCodeBase.iterator();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void recursiveCodeBaseWithTwoSourceFilesReturnsTwoSourceFiles() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES.getPath()).build();

        final Set<String> matchingFileNames = builtCodeBase.stream().map(File::getName).collect(Collectors.toSet());
        
        assertTrue(matchingFileNames.contains("FirstSourceFile.java"));
        assertTrue(matchingFileNames.contains("SecondSourceFile.java"));
    }

    @Test
    public void recursiveCodeBaseWithTwoSourceFilesReturnsTwoSourceFilesOnlyContainsTwoSourceFiles() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES.getPath()).build();
        final Iterator<File> iterator = builtCodeBase.iterator();
        
        iterator.next();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void nonRecursiveCodeBaseWithTwoSourceFilesInSubPackagesReturnsOnlyOneSourceFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES_IN_SUBPACKAGES.getPath()).recursive(false).build();
        
        final Set<String> matchingFileNames = builtCodeBase.stream().map(File::getName).collect(Collectors.toSet());
        
        assertTrue(matchingFileNames.contains("FirstSourceFile.java"));
    }
    
    @Test
    public void nonRecursiveCodeBaseWithTwoSourceFilesInSubPackagesOnlyContainsOneSourceFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES_IN_SUBPACKAGES.getPath()).recursive(false).build();
        final Iterator<File> iterator = builtCodeBase.iterator();
        
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }

    @Test
    public void recursiveCodeBaseWithTwoSourceFilesInSubPackagesReturnsOnlyOneSourceFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES_IN_SUBPACKAGES.getPath()).build();
        
        final Set<String> matchingFileNames = builtCodeBase.stream().map(File::getName).collect(Collectors.toSet());
        
        assertTrue(matchingFileNames.contains("FirstSourceFile.java"));
        assertTrue(matchingFileNames.contains("SecondSourceFile.java"));
    }
    
    @Test
    public void recursiveCodeBaseWithTwoSourceFilesInSubPackagesOnlyContainsOneSourceFile() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES_IN_SUBPACKAGES.getPath()).build();
        final Iterator<File> iterator = builtCodeBase.iterator();
        
        iterator.next();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }
    
    @Test
    public void codeBaseCanContainMultipleRoots() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES_IN_SUBPACKAGES.getPath())
                .addRoot("src/test/resources/fileWithImport")
                .build();
        
        final Set<String> matchingFileNames = builtCodeBase.stream().map(File::getName).collect(Collectors.toSet());
        
        assertTrue(matchingFileNames.contains("FirstSourceFile.java"));
        assertTrue(matchingFileNames.contains("SecondSourceFile.java"));
        assertTrue(matchingFileNames.contains("ClassWithDesiredImport.java"));
        assertTrue(matchingFileNames.contains("ClassWithoutDesiredImport.java"));
    }
    
    @Test
    public void codeBaseWithMultipleRootsWillReturnAllContainedSourceFiles() {
        final CodeBase builtCodeBase = CodeBase.CodeBaseBuilder.fromRoots(TEST_DIRECTORIES.TWO_SOURCE_FILES_IN_SUBPACKAGES.getPath())
                .addRoot("src/test/resources/fileWithImport")
                .build();
        
        final Iterator<File> iterator = builtCodeBase.iterator();
        
        iterator.next();
        iterator.next();
        iterator.next();
        iterator.next();
        assertThrows(NoSuchElementException.class, () -> iterator.next());
    }
    
    @Test
    public void whenExstingJarFilesAreAddedToCodeBaseThenNoExceptionIsThrown() {
        // Given
        final CodeBase codeBase = CodeBase.CodeBaseBuilder
                .fromRoots("src/test/resources/fieldAccessMessageLoggerTestFiles")
                .addJarRoot("lib/slf4j-api-1.7.25.jar")
                .addJarRoot("lib/jwordsplitter-3.4.jar").build();

        // When & Then
        // One JavaSource file root
        assertEquals(1, codeBase.getCodeBaseRoots().stream().filter(CodeBaseRootFile::isSourceFile).collect(Collectors.toSet()).size());

        // Two Jar file roots
        assertEquals(2, codeBase.getCodeBaseRoots().stream().filter(CodeBaseRootFile::isJar).collect(Collectors.toSet())
                .size());
    }

    @Test
    public void whenNonExistingJarFileIsAddedToCodebaseThenErrorIsThrown() {
        // Given
        final CodeBase.CodeBaseBuilder codeBaseBuilder = CodeBase.CodeBaseBuilder
                .fromRoots("src/test/resources/fieldAccessMessageLoggerTestFiles")
                .addJarRoot("lib/myNonExistingJar.jar");

        // When & Then
        // Jar does not exist!
        assertThrows(FileNotFoundExceptionException.class, () -> codeBaseBuilder.build());
    }

    @Test
    public void whenExistingAndNonExistingJarFileIsAddedToCodebaseThenErrorIsThrown() {
        // Given
        final CodeBase.CodeBaseBuilder codeBaseBuilder = CodeBase.CodeBaseBuilder
                .fromRoots("src/test/resources/fieldAccessMessageLoggerTestFiles")
                .addJarRoot("lib/slf4j-api-1.7.25.jar")
                .addJarRoot("lib/myNonExistingJar.jar");

        // When & Then
        // Jar does not exist!
        assertThrows(FileNotFoundExceptionException.class, () -> codeBaseBuilder.build());
    }

    @Test
    public void whenJarRootFolderIsAddedToCodebaseThenAllContainedJarsAreAddedToCodeBase() {
    	// Given
    	final CodeBase codeBase = CodeBase.CodeBaseBuilder
    			.fromRoots("src/test/resources/fieldAccessMessageLoggerTestFiles")
    			.addJarRoot("lib").build();
    	
    	// When & Then
    	assertEquals(2, codeBase.getCodeBaseRoots().stream().filter(CodeBaseRootFile::isJar).collect(Collectors.toSet())
                .size());
    }
}
