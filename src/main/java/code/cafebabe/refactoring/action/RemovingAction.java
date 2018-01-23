package code.cafebabe.refactoring.action;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.FieldDeclaration;

public final class RemovingAction extends Action {

    @Override
    public void consume(final List<Node> pNodesToProcess, Optional<FieldDeclaration> matchingFieldDeclarationsForReplacementType) {
        // Workaround to remove multiple children from same node in LexicalPreserving printer => Save former parent
        Optional<Node> parent = Optional.empty();
        for (final Node node : pNodesToProcess) {
            parent = parent.isPresent() ? parent : node.getParentNode();
            parent.ifPresent(n -> n.remove(node));
        }
    }

    @Override
    public void consumeFieldDeclarations(final Set<FieldDeclaration> pFieldDeclarations) {
        pFieldDeclarations.forEach(FieldDeclaration::remove);
    }

    @Override
    public void consumeImports(final List<ImportDeclaration> pImports, final Class<?> pTargetType) {
        Set<ImportDeclaration> importsToRemove = pImports.stream().filter(i -> i != null).filter(i -> pTargetType.getName().equals(i.getNameAsString()))
                .collect(Collectors.toSet());
        importsToRemove.forEach(ImportDeclaration::remove);
    }

}