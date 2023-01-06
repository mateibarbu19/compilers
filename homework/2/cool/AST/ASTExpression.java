package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

public abstract class ASTExpression extends ASTNode {
    public ASTExpression(final ParserRuleContext context) {
        super(context);
    }
}
