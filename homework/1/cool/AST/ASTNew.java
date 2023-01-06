package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTNew extends ASTExpression {
    ASTTypeId type;

    public ASTNew(final Token start, final ASTTypeId type) {
        super(start);
        this.type = type;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the type
     */
    public ASTTypeId getType() {
        return type;
    }
}
