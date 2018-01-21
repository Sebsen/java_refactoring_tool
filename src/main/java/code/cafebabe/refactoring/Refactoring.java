package code.cafebabe.refactoring;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public abstract class Refactoring {

    protected final Class<?> targetType;
    protected final Class<?> replacement;

    public Refactoring(final Class<?> pTargetType, final Class<?> pReplacement) {
        targetType = pTargetType;
        replacement = pReplacement;
    }
    
    public abstract <T extends Node> boolean isApplyable(final T pNode, TypeSolver pMySolver);

    public abstract CompilationUnit apply(final CompilationUnit pCompilataionUnit, final TypeSolver pTypeSolver);

}