package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTAlternative extends ASTExpression {
    ASTObjectId name;
    ASTTypeId type;
    ASTExpression body;

    public ASTAlternative(final Token start, final ASTObjectId name, final ASTTypeId type, final ASTExpression body) {
        super(start);
        this.name = name;
        this.type = type;
        this.body = body;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the name
     */
    public ASTObjectId getName() {
        return name;
    }

    /**
     * @return the type
     */
    public ASTTypeId getType() {
        return type;
    }

    /**
     * @return the expression
     */
    public ASTExpression getBody() {
        return body;
    }
}