package cool.AST;

import org.antlr.v4.runtime.Token;

public abstract class ASTExpression extends ASTNode {
    public ASTExpression(final Token start) {
        super(start);
    }
}
