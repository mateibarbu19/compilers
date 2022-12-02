package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTAssignment extends ASTExpression {
    Token name;
    ASTExpression expression;

    public ASTAssignment(final Token start, final Token name, final ASTExpression expression) {
        super(start);
        this.name = name;
        this.expression = expression;
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
     * @return the expression
     */
    public ASTExpression getExpression() {
        return expression;
    }

}
