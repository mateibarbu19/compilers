package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class ASTFeature extends ASTNode {
    public ASTFeature(final ParserRuleContext context) {
        super(context);
    }
}
