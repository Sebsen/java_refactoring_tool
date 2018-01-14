package code.cafebabe.refactoring;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class ClassOrInterfaceTypeChange extends Change {
    
    public ClassOrInterfaceTypeChange(final Class<?> pTargetType, final Class<?> pReplacementType) {
        super(pTargetType, pReplacementType);
    }

    @Override
    public <T extends Node> boolean isApplyable(final T pNode, TypeSolver pMySolver) {
        return pNode instanceof ImportDeclaration && ((ImportDeclaration) pNode).getNameAsString().equals(targetType.getName());
    }

    @Override
    public CompilationUnit apply(CompilationUnit pCompilataionUnit, TypeSolver pTypeSolver) {
        // TODO Auto-generated method stub
        return null;
    }
    
}