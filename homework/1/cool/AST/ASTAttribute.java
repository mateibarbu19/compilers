package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTAttribute extends ASTFeature {
    Token name;
    Token type;
    ASTExpression initialization;

    public ASTAttribute(final Token start, final Token name, final Token type, final ASTExpression initialization) {
        super(start);
        this.name = name;
        this.type = type;
        this.initialization = initialization;
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

    /**
     * @return the expression
     */
    public ASTExpression getInitialization() {
        return initialization;
    }

}
