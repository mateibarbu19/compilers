package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTObjectId extends ASTExpression {
    Token token;

    public ASTObjectId(final ParserRuleContext context, final Token objectId) {
        super(context);
        this.token = objectId;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the token
     */
    public Token getToken() {
        return token;
    }
}
