package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTString extends ASTExpression {
    public ASTString(final Token token) {
        super(token);
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
