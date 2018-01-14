package code.cafebabe.refactoring;

import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.declarations.ResolvedReferenceTypeDeclaration;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.SourceFileInfoExtractor;
import com.github.javaparser.symbolsolver.javaparser.Navigator;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.Pair;

import code.cafebabe.refactoring.factory.CompilationUnitFactory;
import code.cafebabe.refactoring.util.FileUtil;

public class Driver {

    private static final boolean RECURSIVE = true;

    public static void main(String[] args) throws FileNotFoundException {
        // generalTest();
        // printAllClassesContainingType();
        niceTest();
    }

    private static void niceTest() throws FileNotFoundException {
        final File rootFile = new File("src/test/resources");

        final TypeSolver mySolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(rootFile));

        final CodeBase cb = CodeBase.CodeBaseBuilder.fromRoots(rootFile).build();

        cb.stream().forEach(polledFile -> {
            try {
                System.out.println(polledFile.getName() + ": ");
                List<ClassOrInterfaceType> fieldDeclaration = Navigator.findAllNodesOfGivenClass(CompilationUnitFactory.createPreservingCompilationUnit(polledFile).b,
                        ClassOrInterfaceType.class);
                List<ObjectCreationExpr> fieldDeclaration2 = Navigator.findAllNodesOfGivenClass(CompilationUnitFactory.createPreservingCompilationUnit(polledFile).b,
                        ObjectCreationExpr.class);
//                ResolvedType fieldType = JavaParserFacade.get(mySolver).convertToUsage(f.getVariables().get(0).getType(), f);
                fieldDeclaration.forEach(d -> {
                    ResolvedType fieldType = JavaParserFacade.get(mySolver).convertToUsage(d);
                    if(fieldType.asReferenceType().getQualifiedName().equals(mySolver.solveType("java.lang.StringBuilder").getQualifiedName())) {
                        System.out.println("Found!");
                    }
                });
                SourceFileInfoExtractor ex = new SourceFileInfoExtractor();
//                System.out.println("\t" + fieldDeclaration);
//                System.out.println("\t" + fieldDeclaration2 + "\n");
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        });
    }

    private static void generalTest() throws FileNotFoundException {
        // final Set<File> matchingFiles = FileUtil.getMatchingFiles(new File("src/test/resources"), asList(Pattern.compile(".*\\.java")), RECURSIVE);
        final Set<File> matchingFiles = FileUtil.getMatchingFiles(new File("src/test/resources"), asList(Pattern.compile("ClassWithBufferAsField\\.java")), RECURSIVE);
        System.out.println(matchingFiles);

        final TypeSolver mySolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(matchingFiles.iterator().next().getParentFile()));
        List<FieldDeclaration> fieldDeclaration = Navigator.findAllNodesOfGivenClass(CompilationUnitFactory.createPreservingCompilationUnit(matchingFiles.iterator().next()).b,
                FieldDeclaration.class);
        fieldDeclaration.forEach(f -> {
            ResolvedType fieldType = JavaParserFacade.get(mySolver).convertToUsage(f.getVariables().get(0).getType(), f);
            System.out.println("Field type: " + fieldType.asReferenceType().getQualifiedName());
        });
    }

    private static void printAllClassesContainingType() throws FileNotFoundException {
        // Collect all Java src files under given root directory
        final Set<File> matchingFiles = FileUtil.getMatchingFiles(new File("src/test/resources"), asList(Pattern.compile("ClassWithBufferAsInlineInstance\\.java")), RECURSIVE);
        System.out.println(matchingFiles);

        // Create new TypeSolver instance
        final TypeSolver mySolver = new CombinedTypeSolver(new ReflectionTypeSolver(), new JavaParserTypeSolver(matchingFiles.iterator().next().getParentFile()));
        ((CombinedTypeSolver) mySolver).add(new JavaParserTypeSolver(new File("C:/Entwicklung/eclipse_workspaces/default/java-parser/src/main/java/")));

        // Create new preserving compilation unit
        final Pair<File, CompilationUnit> cu = CompilationUnitFactory.createPreservingCompilationUnit(matchingFiles.iterator().next());
        final List<ImportDeclaration> fieldDeclarations = Navigator.findAllNodesOfGivenClass(cu.b, ImportDeclaration.class);

        ImportDeclaration imp = fieldDeclarations.get(0);
        ResolvedReferenceTypeDeclaration solvedType = mySolver.solveType(StringBuffer.class.getName());
        System.out.println("PKG-Name: " + solvedType.getPackageName());
        solvedType = mySolver.solveType("ClassWithBufferAsInlineInstance");
        System.out.println("PKG-Name: " + solvedType.getName());

        System.out.println(imp.getName());
        System.out.println(imp.getNameAsString());

        // final List<String> state = new ArrayList<>();
        // cu.accept(new MyVoidVisitor(mySolver, "StringBuffer"), state);

        fieldDeclarations.stream().map(ImportDeclaration::getNameAsString).forEach(System.out::println);
        fieldDeclarations.forEach(i -> {
            i.getChildNodes().forEach(c -> {
                System.out.println(JavaParserFacade.get(mySolver).getTypeSolver().solveType(c.toString()));
            });
        });
        // ResolvedType fieldType = JavaParserFacade.get(mySolver).convertToUsage(i.get, i);
        // System.out.println("Field type: " + fieldType.asReferenceType().getQualifiedName());
    }
}
