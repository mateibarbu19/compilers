package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public abstract class ASTNode {
    ParserRuleContext context;
    ASTError error;

    public ASTNode(final ParserRuleContext context) {
        this.context = context;
        this.error = ASTError.None;
    }

    public abstract <T> T accept(ASTVisitor<T> visitor);

    /**
     * @return the context
     */
    public ParserRuleContext getContext() {
        return context;
    }

    /**
     * @return the error
     */
    public ASTError getError() {
        return error;
    }

    /**
     * @param error the error to set
     */
    public void setError(ASTError error) {
        this.error = error;
    }
}
