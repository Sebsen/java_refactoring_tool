package code.cafebabe.refactoring;

import java.util.List;

import com.github.javaparser.ast.ArrayCreationLevel;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.AnnotationMemberDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.EnumConstantDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.ReceiverParameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.comments.BlockComment;
import com.github.javaparser.ast.comments.JavadocComment;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.CharLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.ConditionalExpr;
import com.github.javaparser.ast.expr.DoubleLiteralExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.InstanceOfExpr;
import com.github.javaparser.ast.expr.IntegerLiteralExpr;
import com.github.javaparser.ast.expr.LambdaExpr;
import com.github.javaparser.ast.expr.LongLiteralExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.MethodReferenceExpr;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.expr.SuperExpr;
import com.github.javaparser.ast.expr.ThisExpr;
import com.github.javaparser.ast.expr.TypeExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.modules.ModuleDeclaration;
import com.github.javaparser.ast.modules.ModuleExportsStmt;
import com.github.javaparser.ast.modules.ModuleOpensStmt;
import com.github.javaparser.ast.modules.ModuleProvidesStmt;
import com.github.javaparser.ast.modules.ModuleRequiresStmt;
import com.github.javaparser.ast.modules.ModuleUsesStmt;
import com.github.javaparser.ast.stmt.AssertStmt;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.BreakStmt;
import com.github.javaparser.ast.stmt.CatchClause;
import com.github.javaparser.ast.stmt.ContinueStmt;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt;
import com.github.javaparser.ast.stmt.ExpressionStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.ForeachStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.LabeledStmt;
import com.github.javaparser.ast.stmt.LocalClassDeclarationStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.stmt.SwitchEntryStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.SynchronizedStmt;
import com.github.javaparser.ast.stmt.ThrowStmt;
import com.github.javaparser.ast.stmt.TryStmt;
import com.github.javaparser.ast.stmt.UnparsableStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.type.ArrayType;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.IntersectionType;
import com.github.javaparser.ast.type.PrimitiveType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.type.UnionType;
import com.github.javaparser.ast.type.UnknownType;
import com.github.javaparser.ast.type.VoidType;
import com.github.javaparser.ast.type.WildcardType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.javaparsermodel.JavaParserFacade;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;

public class MyVoidVisitor extends VoidVisitorAdapter<List<String>> {

    private final TypeSolver solver;
    private String targetTypeName;

    public MyVoidVisitor(TypeSolver solver, String targetTypeName) {
        this.solver = solver;
        this.targetTypeName = targetTypeName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.AnnotationDeclaration, java.lang.Object)
     */
    @Override
    public void visit(AnnotationDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.AnnotationMemberDeclaration, java.lang.Object)
     */
    @Override
    public void visit(AnnotationMemberDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.ArrayAccessExpr, java.lang.Object)
     */
    @Override
    public void visit(ArrayAccessExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.ArrayCreationExpr, java.lang.Object)
     */
    @Override
    public void visit(ArrayCreationExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.ArrayInitializerExpr, java.lang.Object)
     */
    @Override
    public void visit(ArrayInitializerExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.AssertStmt, java.lang.Object)
     */
    @Override
    public void visit(AssertStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.AssignExpr, java.lang.Object)
     */
    @Override
    public void visit(AssignExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.BinaryExpr, java.lang.Object)
     */
    @Override
    public void visit(BinaryExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.comments.BlockComment, java.lang.Object)
     */
    @Override
    public void visit(BlockComment n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.BlockStmt, java.lang.Object)
     */
    @Override
    public void visit(BlockStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.BooleanLiteralExpr, java.lang.Object)
     */
    @Override
    public void visit(BooleanLiteralExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.BreakStmt, java.lang.Object)
     */
    @Override
    public void visit(BreakStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.CastExpr, java.lang.Object)
     */
    @Override
    public void visit(CastExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.CatchClause, java.lang.Object)
     */
    @Override
    public void visit(CatchClause n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.CharLiteralExpr, java.lang.Object)
     */
    @Override
    public void visit(CharLiteralExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.ClassExpr, java.lang.Object)
     */
    @Override
    public void visit(ClassExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.ClassOrInterfaceDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ClassOrInterfaceDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.ClassOrInterfaceType, java.lang.Object)
     */
    @Override
    public void visit(ClassOrInterfaceType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.CompilationUnit, java.lang.Object)
     */
    @Override
    public void visit(CompilationUnit n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.ConditionalExpr, java.lang.Object)
     */
    @Override
    public void visit(ConditionalExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.ConstructorDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ConstructorDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.ContinueStmt, java.lang.Object)
     */
    @Override
    public void visit(ContinueStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.DoStmt, java.lang.Object)
     */
    @Override
    public void visit(DoStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.DoubleLiteralExpr, java.lang.Object)
     */
    @Override
    public void visit(DoubleLiteralExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.EmptyStmt, java.lang.Object)
     */
    @Override
    public void visit(EmptyStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.EnclosedExpr, java.lang.Object)
     */
    @Override
    public void visit(EnclosedExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.EnumConstantDeclaration, java.lang.Object)
     */
    @Override
    public void visit(EnumConstantDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.EnumDeclaration, java.lang.Object)
     */
    @Override
    public void visit(EnumDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt, java.lang.Object)
     */
    @Override
    public void visit(ExplicitConstructorInvocationStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.ExpressionStmt, java.lang.Object)
     */
    @Override
    public void visit(ExpressionStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.FieldAccessExpr, java.lang.Object)
     */
    @Override
    public void visit(FieldAccessExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.FieldDeclaration, java.lang.Object)
     */
    @Override
    public void visit(FieldDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.ForeachStmt, java.lang.Object)
     */
    @Override
    public void visit(ForeachStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.ForStmt, java.lang.Object)
     */
    @Override
    public void visit(ForStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.IfStmt, java.lang.Object)
     */
    @Override
    public void visit(IfStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.InitializerDeclaration, java.lang.Object)
     */
    @Override
    public void visit(InitializerDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.InstanceOfExpr, java.lang.Object)
     */
    @Override
    public void visit(InstanceOfExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.IntegerLiteralExpr, java.lang.Object)
     */
    @Override
    public void visit(IntegerLiteralExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.comments.JavadocComment, java.lang.Object)
     */
    @Override
    public void visit(JavadocComment n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.LabeledStmt, java.lang.Object)
     */
    @Override
    public void visit(LabeledStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.comments.LineComment, java.lang.Object)
     */
    @Override
    public void visit(LineComment n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.LongLiteralExpr, java.lang.Object)
     */
    @Override
    public void visit(LongLiteralExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.MarkerAnnotationExpr, java.lang.Object)
     */
    @Override
    public void visit(MarkerAnnotationExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.MemberValuePair, java.lang.Object)
     */
    @Override
    public void visit(MemberValuePair n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.MethodCallExpr, java.lang.Object)
     */
    @Override
    public void visit(MethodCallExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.MethodDeclaration, java.lang.Object)
     */
    @Override
    public void visit(MethodDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.NameExpr, java.lang.Object)
     */
    @Override
    public void visit(NameExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.NormalAnnotationExpr, java.lang.Object)
     */
    @Override
    public void visit(NormalAnnotationExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.NullLiteralExpr, java.lang.Object)
     */
    @Override
    public void visit(NullLiteralExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.ObjectCreationExpr, java.lang.Object)
     */
    @Override
    public void visit(ObjectCreationExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.PackageDeclaration, java.lang.Object)
     */
    @Override
    public void visit(PackageDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.Parameter, java.lang.Object)
     */
    @Override
    public void visit(Parameter n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.PrimitiveType, java.lang.Object)
     */
    @Override
    public void visit(PrimitiveType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.Name, java.lang.Object)
     */
    @Override
    public void visit(Name n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.SimpleName, java.lang.Object)
     */
    @Override
    public void visit(SimpleName n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.ArrayType, java.lang.Object)
     */
    @Override
    public void visit(ArrayType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.ArrayCreationLevel, java.lang.Object)
     */
    @Override
    public void visit(ArrayCreationLevel n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.IntersectionType, java.lang.Object)
     */
    @Override
    public void visit(IntersectionType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.UnionType, java.lang.Object)
     */
    @Override
    public void visit(UnionType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.ReturnStmt, java.lang.Object)
     */
    @Override
    public void visit(ReturnStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.SingleMemberAnnotationExpr, java.lang.Object)
     */
    @Override
    public void visit(SingleMemberAnnotationExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.StringLiteralExpr, java.lang.Object)
     */
    @Override
    public void visit(StringLiteralExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.SuperExpr, java.lang.Object)
     */
    @Override
    public void visit(SuperExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.SwitchEntryStmt, java.lang.Object)
     */
    @Override
    public void visit(SwitchEntryStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.SwitchStmt, java.lang.Object)
     */
    @Override
    public void visit(SwitchStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.SynchronizedStmt, java.lang.Object)
     */
    @Override
    public void visit(SynchronizedStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.ThisExpr, java.lang.Object)
     */
    @Override
    public void visit(ThisExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.ThrowStmt, java.lang.Object)
     */
    @Override
    public void visit(ThrowStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.TryStmt, java.lang.Object)
     */
    @Override
    public void visit(TryStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.LocalClassDeclarationStmt, java.lang.Object)
     */
    @Override
    public void visit(LocalClassDeclarationStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.TypeParameter, java.lang.Object)
     */
    @Override
    public void visit(TypeParameter n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.UnaryExpr, java.lang.Object)
     */
    @Override
    public void visit(UnaryExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.UnknownType, java.lang.Object)
     */
    @Override
    public void visit(UnknownType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.VariableDeclarationExpr, java.lang.Object)
     */
    @Override
    public void visit(VariableDeclarationExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.VariableDeclarator, java.lang.Object)
     */
    @Override
    public void visit(VariableDeclarator n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.VoidType, java.lang.Object)
     */
    @Override
    public void visit(VoidType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.WhileStmt, java.lang.Object)
     */
    @Override
    public void visit(WhileStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.type.WildcardType, java.lang.Object)
     */
    @Override
    public void visit(WildcardType n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.LambdaExpr, java.lang.Object)
     */
    @Override
    public void visit(LambdaExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.MethodReferenceExpr, java.lang.Object)
     */
    @Override
    public void visit(MethodReferenceExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.expr.TypeExpr, java.lang.Object)
     */
    @Override
    public void visit(TypeExpr n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.NodeList, java.lang.Object)
     */
    @Override
    public void visit(NodeList n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.ImportDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ImportDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.modules.ModuleDeclaration, java.lang.Object)
     */
    @Override
    public void visit(ModuleDeclaration n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.modules.ModuleRequiresStmt, java.lang.Object)
     */
    @Override
    public void visit(ModuleRequiresStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.modules.ModuleExportsStmt, java.lang.Object)
     */
    @Override
    public void visit(ModuleExportsStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.modules.ModuleProvidesStmt, java.lang.Object)
     */
    @Override
    public void visit(ModuleProvidesStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.modules.ModuleUsesStmt, java.lang.Object)
     */
    @Override
    public void visit(ModuleUsesStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.modules.ModuleOpensStmt, java.lang.Object)
     */
    @Override
    public void visit(ModuleOpensStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.stmt.UnparsableStmt, java.lang.Object)
     */
    @Override
    public void visit(UnparsableStmt n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.github.javaparser.ast.visitor.VoidVisitorAdapter#visit(com.github.javaparser.ast.body.ReceiverParameter, java.lang.Object)
     */
    @Override
    public void visit(ReceiverParameter n, List<String> arg) {
        // TODO Auto-generated method stub
        super.visit(n, arg);
    }

    private void defaultHandling(final Statement stmt, final List<String> arg) {
        ResolvedType type = JavaParserFacade.get(solver).getType(stmt);
        if (type.asReferenceType().getQualifiedName().equals(targetTypeName)) {
            arg.add("yes");
        }
    }

}
