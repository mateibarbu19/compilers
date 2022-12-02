package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTWhile extends ASTExpression {
    ASTExpression condition;
    ASTExpression body;

    public ASTWhile(final Token start, final ASTExpression condition, final ASTExpression body) {
        super(start);
        this.condition = condition;
        this.body = body;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the condition
     */
    public ASTExpression getCondition() {
        return condition;
    }

    /**
     * @return the body
     */
    public ASTExpression getBody() {
        return body;
    }
}
