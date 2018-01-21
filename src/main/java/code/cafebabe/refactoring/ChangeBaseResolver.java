package code.cafebabe.refactoring;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.utils.Pair;

import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import code.cafebabe.refactoring.factory.TypeSolverFactory;

public class ChangeBaseResolver {

    private final CodeBase codeBase;
    private final Refactoring changeToApply;

    public ChangeBaseResolver(final CodeBase pCodeBase, final Refactoring pRefactoring) {
        codeBase = pCodeBase;
        changeToApply = pRefactoring;
    }

    public Set<File> resolve() {
        final Set<File> matchingFiles = new HashSet<>();
        final Iterator<File> codeBaseIterator = codeBase.iterator();
        while (codeBaseIterator.hasNext()) {
            try {
                // Create new preserving compilation unit
                final TypeSolver mySolver = TypeSolverFactory.createFrom(codeBase);
                CompilationUnitWrapper cu = CompilationUnitFactory.createPreservingCompilationUnit(codeBaseIterator.next(), mySolver);
                // final List<ImportDeclaration> fieldDeclarations = Navigator.findAllNodesOfGivenClass(cu.b, ImportDeclaration.class);
                // final List<MethodCallExpr> fieldDeclarations = Navigator.findAllNodesOfGivenClass(cu.b, MethodCallExpr.class);
                final List<Node> fieldDeclarations = Navigator.findAllNodesOfGivenClass(cu.getCompilationUnit(), Node.class);

                fieldDeclarations.stream().filter(m -> changeToApply.isApplyable(m, mySolver)).forEach(e -> matchingFiles.add(cu.getSourceFile()));

            } catch (FileNotFoundException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
            // fieldDeclarations.forEach(
            // i -> i.getChildNodes().forEach(c -> System.out.println(JavaParserFacade.get(typeSolver).getTypeSolver().solveType(c.toString()).asClass().getQualifiedName())));
        }
        return matchingFiles;
    }

}
