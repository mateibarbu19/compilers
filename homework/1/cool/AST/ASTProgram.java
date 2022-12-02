package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTProgram extends ASTNode {
    List<ASTClassDefine> classes;

    public ASTProgram(final Token start, final List<ASTClassDefine> classes) {
        super(start);
        this.classes = classes;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the classes
     */
    public List<ASTClassDefine> getClasses() {
        return classes;
    }
}
