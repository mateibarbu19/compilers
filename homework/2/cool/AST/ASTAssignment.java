package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import cool.visitor.ASTVisitor;

public class ASTAssignment extends ASTExpression {
    ASTObjectId name;
    ASTExpression expression;

    public ASTAssignment(final ParserRuleContext context, final ASTObjectId name,
            final ASTExpression expression) {
        super(context);
        this.name = name;
        this.expression = expression;
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
     * @return the expression
     */
    public ASTExpression getExpression() {
        return expression;
    }

}
