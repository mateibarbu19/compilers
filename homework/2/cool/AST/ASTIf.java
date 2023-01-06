package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTIf extends ASTExpression {
    ASTExpression condition;
    ASTExpression consequent;
    ASTExpression alternative;

    public ASTIf(final Token start, final ASTExpression condition, final ASTExpression consequent,
            final ASTExpression alternative) {
        super(start);
        this.condition = condition;
        this.consequent = consequent;
        this.alternative = alternative;
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
     * @return the consequent
     */
    public ASTExpression getConsequent() {
        return consequent;
    }

    /**
     * @return the alternative
     */
    public ASTExpression getAlternative() {
        return alternative;
    }
}
