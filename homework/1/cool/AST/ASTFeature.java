package cool.AST;

import org.antlr.v4.runtime.Token;

public abstract class ASTFeature extends ASTNode {
    public ASTFeature(final Token start) {
        super(start);
    }
}
