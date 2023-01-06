package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTIsvoid extends ASTExpression {
    ASTExpression expression;

    public ASTIsvoid(final Token start, final ASTExpression expression) {
        super(start);
        this.expression = expression;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the expression
     */
    public ASTExpression getExpression() {
        return expression;
    }
}
