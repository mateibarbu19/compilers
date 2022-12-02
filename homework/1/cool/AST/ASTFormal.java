package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTFormal extends ASTNode {
    Token name;
    Token type;

    public ASTFormal(final Token start, final Token name, final Token type) {
        super(start);
        this.name = name;
        this.type = type;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the name
     */
    public Token getName() {
        return name;
    }

    /**
     * @return the type
     */
    public Token getType() {
        return type;
    }
}
