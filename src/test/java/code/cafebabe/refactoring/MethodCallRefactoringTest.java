package code.cafebabe.refactoring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.visitor.ModifierVisitor;
import com.github.javaparser.ast.visitor.Visitable;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.resolution.declarations.ResolvedFieldDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.action.ExtendingAction;
import code.cafebabe.refactoring.action.RemovingAction;
import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import code.cafebabe.refactoring.factory.TypeSolverFactory;
import code.cafebabe.refactoring.util.CompilationUnitWriter;
import complexClass.custom.Logger;

@RunWith(JUnitPlatform.class)
public class MethodCallRefactoringTest {

    private static final File TEST_RESOURCES_BASE = new File("src/test/resources/");
    private static final File TEST_RESOURCES_TARGETS = new File(TEST_RESOURCES_BASE, "targets");

    @DisplayName("Verifying \"RemovingAction\" results")
    @ParameterizedTest(name = "on \"{1}\" in {0}")
    @CsvSource({ "fieldAccessMessageLoggerTestFiles/, MessageLoggerFieldAccessCalls.java, MessageLoggerFieldAccessCalls.java",
            "complexClass/, ComplexClass.java, ComplexClassAfterRemoval.java" })
    public void removingActionGivesExpectedResult(final String pTestFolderName, final String pTestClassName, final String pTestClassTargetName) {

        // Given
        final Refactoring change = MethodCallRefactoring.RefactoringBuilder.of(new RemovingAction()).andTarget(Logger.class).build();
        final File testBase = new File(TEST_RESOURCES_BASE, pTestFolderName);
        final File target = new File(TEST_RESOURCES_TARGETS, pTestFolderName + pTestClassTargetName);

        final CodeBase codeBase = CodeBase.CodeBaseBuilder.fromRoots(testBase).addJarRoot("lib/slf4j-api-1.7.25.jar")
                .build();

        // When
        final Iterator<File> codeBaseIterator = codeBase.iterator();
        while (codeBaseIterator.hasNext()) {
            final File next = codeBaseIterator.next();
            if (!next.getName().equals(pTestClassName)) {
                continue;
            }
            try {
                // Create type solver from CodeBase
                final TypeSolver mySolver = TypeSolverFactory.createFrom(codeBase);

                // Create new preserving compilation unit
                CompilationUnitWrapper cu = CompilationUnitFactory.createPreservingCompilationUnit(next, mySolver);

                // Apply change and print
                final CompilationUnit changed = change.apply(cu.getCompilationUnit(), mySolver);

                // Then
                assertEquals(CompilationUnitFactory.createPreservingCompilationUnit(target).getCompilationUnit().toString(), changed.toString());

            } catch (FileNotFoundException e) {
                fail("Exception has been thrown! " + e.getClass().getName());
            }
        }
    }

    @Test
    @Ignore
    public void removingActionGivesExpectedResult2() {

        // Given
        final Refactoring change = MethodCallRefactoring.RefactoringBuilder.of(new RemovingAction()).andTarget(Logger.class).build();
        final String testFolderMain = "complexClass/";
        final String testFileName = "ComplexClass.java";
        final String targetFileName = "ComplexClassAfterRemoval.java";
        final File testBase = new File(TEST_RESOURCES_BASE, testFolderMain);
        final File target = new File(TEST_RESOURCES_TARGETS, testFolderMain + targetFileName);

        final CodeBase codeBase = CodeBase.CodeBaseBuilder.fromRoots(testBase).addJarRoot("lib/slf4j-api-1.7.25.jar")
                .build();

        // When
        final Iterator<File> codeBaseIterator = codeBase.iterator();
        while (codeBaseIterator.hasNext()) {
            final File next = codeBaseIterator.next();
            if (!next.getName().equals(testFileName)) {
                continue;
            }
            try {
                // Create type solver from CodeBase
                final TypeSolver mySolver = TypeSolverFactory.createFrom(codeBase);

                // Create new preserving compilation unit
                CompilationUnitWrapper cu = CompilationUnitFactory.createPreservingCompilationUnit(next, mySolver);

                // Apply change and print
                final CompilationUnit changed = change.apply(cu.getCompilationUnit(), mySolver);

                boolean writeToFile = true;
                if (writeToFile) {
                    CompilationUnitWriter.writeToFile(changed, new File(cu.getSourceFile().getParent(), "out.java"));
                }

                // Then
                assertEquals(LexicalPreservingPrinter
                        .print(CompilationUnitFactory.createPreservingCompilationUnit(target).getCompilationUnit()),
                        LexicalPreservingPrinter.print(changed));

            } catch (IOException e) {
                fail("Exception has been thrown! " + e.getClass().getName());
            }
        }

        // Then

    }

    @Test
    @Ignore
    public void extendingActionGivesExpectedResult() {

        // Given
        final Refactoring change = MethodCallRefactoring.RefactoringBuilder.of(new ExtendingAction()).andTarget(Logger.class).andReplacement(org.slf4j.Logger.class).build();
        final String testFolderMain = "complexClass/";
        final String testFileName = "ComplexClass.java";
        final String testFileTargetName = "ComplexClassAfterExtension.java";
        final File testBase = new File(TEST_RESOURCES_BASE, testFolderMain);
        final File target = new File(TEST_RESOURCES_TARGETS, testFolderMain + testFileTargetName);

        final CodeBase codeBase = CodeBase.CodeBaseBuilder.fromRoots(testBase).addJarRoot("lib/slf4j-api-1.7.25.jar")
                .build();

        // When
        final Iterator<File> codeBaseIterator = codeBase.iterator();
        while (codeBaseIterator.hasNext()) {
            final File next = codeBaseIterator.next();
            if (!next.getName().equals(testFileName)) {
                continue;
            }
            try {
                // Create type solver from CodeBase
                final TypeSolver mySolver = TypeSolverFactory.createFrom(codeBase);

                // Create new preserving compilation unit
                CompilationUnitWrapper cu = CompilationUnitFactory.createPreservingCompilationUnit(next, mySolver);

                // Apply change and print
                final CompilationUnit changed = change.apply(cu.getCompilationUnit(), mySolver);

                boolean writeToFile = true;
                if (writeToFile) {
                    CompilationUnitWriter.writeToFile(changed, new File(cu.getSourceFile().getParent(), "out.java"));
                }

                // Then
                assertEquals(CompilationUnitFactory.createPreservingCompilationUnit(target).getCompilationUnit().toString(), changed.toString());

            } catch (IOException e) {
                fail("Exception has been thrown! " + e.getClass().getName());
            }
        }

        // Then

    }

    // #######################
    // Old code - remove soon!
    // #######################

    private static class ReplacingVisitor extends ModifierVisitor<Void> {

        private Set<FieldDeclaration> matchingFields = new LinkedHashSet<>();
        private Class<?> targetType;

        public ReplacingVisitor(final Class<?> pTargetType) {
            targetType = pTargetType;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.github.javaparser.ast.visitor.ModifierVisitor#visit(com.github.javaparser.ast.body.FieldDeclaration, java.lang.Object)
         */
        @Override
        public Visitable visit(FieldDeclaration fd, Void arg) {
            super.visit(fd, arg);
            System.out.println("Called!");
            fd.getVariables().forEach(v -> {
                ResolvedFieldDeclaration resolved = v.resolve();
                if (targetType.getName().equals(resolved.getType().asReferenceType().getId())) {
                    // fd.remove();
                    matchingFields.add(fd);
                }
            });
            System.out.println(matchingFields);

            return fd;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.github.javaparser.ast.visitor.ModifierVisitor#visit(com.github.javaparser.ast.expr.FieldAccessExpr, java.lang.Object)
         */
        @Override
        public Visitable visit(FieldAccessExpr arg0, Void arg1) {
            super.visit(arg0, arg1);
            System.out.println("Field-Access: " + arg0);
            return arg0;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.github.javaparser.ast.visitor.ModifierVisitor#visit(com.github.javaparser.ast.expr.MethodCallExpr, java.lang.Object)
         */
        @Override
        public Visitable visit(MethodCallExpr m, Void arg1) {
            super.visit(m, arg1);
            ResolvedType resolved = m.calculateResolvedType();
            if (resolved.isReferenceType() && targetType.getName().equals(resolved.asReferenceType().getId())) {
                m.remove();
            }
            matchingFields.stream().flatMap(fd -> fd.findAll(VariableDeclarator.class).stream()).map(VariableDeclarator::getNameAsString).forEach(n -> {
                if (m.findFirst(NameExpr.class).get().getNameAsString().equals(n)) {
                    m.findFirst(NameExpr.class).get().remove();
                    System.out.println("Found: " + m);
                }
            });
            ;
            return m;
        }

    }

}
