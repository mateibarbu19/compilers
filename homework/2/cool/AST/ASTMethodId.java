package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTMethodId extends ASTExpression {
    Token token;

    public ASTMethodId(final ParserRuleContext context, final Token methodId) {
        super(context);
        this.token = methodId;
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
