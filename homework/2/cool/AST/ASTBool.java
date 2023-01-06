package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTBool extends ASTExpression {
    public ASTBool(final Token start) {
        super(start);
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
