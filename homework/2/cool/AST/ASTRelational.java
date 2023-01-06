package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTRelational extends ASTExpression {
    ASTExpression lhs;
    Token operator;
    ASTExpression rhs;

    public ASTRelational(final ParserRuleContext context, final ASTExpression lhs,
            final Token operator, final ASTExpression rhs) {
        super(context);
        this.lhs = lhs;
        this.operator = operator;
        this.rhs = rhs;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the lhs
     */
    public ASTExpression getLhs() {
        return lhs;
    }

    /**
     * @return the operator
     */
    public Token getOperator() {
        return operator;
    }

    /**
     * @return the rhs
     */
    public ASTExpression getRhs() {
        return rhs;
    }
}
