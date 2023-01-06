package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public class ASTNegative extends ASTExpression {
    ASTExpression expression;

    public ASTNegative(final ParserRuleContext context, final ASTExpression expression) {
        super(context);
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
