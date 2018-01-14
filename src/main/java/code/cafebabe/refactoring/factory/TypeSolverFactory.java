package code.cafebabe.refactoring.factory;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JarTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import code.cafebabe.refactoring.CodeBase;
import code.cafebabe.refactoring.CodeBaseRootFile;

public class TypeSolverFactory {

    private TypeSolverFactory() {
    }

    /**
     * Creates a type solver (respecting Java JDK source files) according to the passed in CodeBase.<br/>
     * For each:<br/>
     * <ul>
     *  <li>JavaSourceFile root -> An instance of an according JavaParserTypeSolver</li>
     *  <li>Jar/ JarFile root -> An instance of an according JarTypeSolver</li>
     * </ul>
     * 
     * @param pCodeBase
     *      The CodeBase to create the solver for.
     * @return
     *      A CombinedTypeSolver (also respecting Java JDK source files) instance including/ considering all CodeBaseRootFiles whether they are referencing a JavaSourceFile or a JarFile.
     */
    public static TypeSolver createFrom(final CodeBase pCodeBase) {
        final CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver(new ReflectionTypeSolver());
        pCodeBase.getCodeBaseRoots().stream().filter(CodeBaseRootFile::isSourceFile).map(CodeBaseRootFile::getReferencedFile).map(JavaParserTypeSolver::new)
                .forEach(combinedTypeSolver::add);
        pCodeBase.getCodeBaseRoots().stream().filter(CodeBaseRootFile::isJar).map(CodeBaseRootFile::getReferencedFile).map(File::getPath).map(p -> {
            try {
                return Optional.of(JarTypeSolver.getJarTypeSolver(p));
            } catch (IOException e) {
                return Optional.empty();
            }
        }).filter(Optional::isPresent).forEach(solver -> combinedTypeSolver.add((JarTypeSolver) solver.get()));
        return combinedTypeSolver;
    }

}
