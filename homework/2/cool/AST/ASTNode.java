package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public abstract class ASTNode {
    ParserRuleContext context;

    public ASTNode(final ParserRuleContext context) {
        this.context = context;
    }

    public abstract <T> T accept(ASTVisitor<T> visitor);

    /**
     * @return the context
     */
    public ParserRuleContext getContext() {
        return context;
    }
}
