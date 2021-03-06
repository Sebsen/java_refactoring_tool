package code.cafebabe.refactoring.factory;

import java.io.File;
import java.io.FileNotFoundException;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.validator.Java7Validator;
import com.github.javaparser.printer.lexicalpreservation.LexicalPreservingPrinter;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

import code.cafebabe.refactoring.CompilationUnitWrapper;

public class CompilationUnitFactory {
    
    private CompilationUnitFactory() {
    }
    
    public static CompilationUnitWrapper createPreservingCompilationUnit(final File pFromFile) throws FileNotFoundException {
        return CompilationUnitWrapper.createCompilationUnitWrapper(pFromFile, LexicalPreservingPrinter.setup(JavaParser.parse(pFromFile)));
    }

    public static CompilationUnitWrapper createPreservingCompilationUnit(final File pFromFile, final TypeSolver pTypeSolver) throws FileNotFoundException {
        JavaSymbolSolver symbolResolver = new JavaSymbolSolver(pTypeSolver);
//        debugParameter(pTypeSolver, symbolResolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolResolver);
        JavaParser.getStaticConfiguration().setValidator(new Java7Validator());
        JavaParser.getStaticConfiguration().setDoNotAssignCommentsPrecedingEmptyLines(false);
        
        return CompilationUnitWrapper.createCompilationUnitWrapper(pFromFile, LexicalPreservingPrinter.setup(JavaParser.parse(pFromFile)));
    }

    public static CompilationUnitWrapper createCompilationUnit(final File pFromFile, final TypeSolver pTypeSolver) throws FileNotFoundException {
        JavaSymbolSolver symbolResolver = new JavaSymbolSolver(pTypeSolver);
//        debugParameter(pTypeSolver, symbolResolver);
        JavaParser.getStaticConfiguration().setSymbolResolver(symbolResolver);
        return CompilationUnitWrapper.createCompilationUnitWrapper(pFromFile, JavaParser.parse(pFromFile));
    }

    private static void debugParameter(final TypeSolver pTypeSolver, JavaSymbolSolver symbolResolver) {
        System.out.println("From TypeSolver: " + pTypeSolver + " !");
        System.out.println("With SymbolResolver: " + symbolResolver + " !");
    }

}
