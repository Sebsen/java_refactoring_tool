package code.cafebabe.refactoring;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;

public abstract class Action {

    public abstract void consume(final List<Node> pNodesToProcess, Optional<FieldDeclaration> matchingFieldDeclarationsForReplacementType);

    public abstract void consumeFieldDeclarations(final Set<FieldDeclaration> pFieldDeclarations);

    public abstract void consumeImports(final CompilationUnit pCompilationUnit, final Class<?> pTargetType);

}