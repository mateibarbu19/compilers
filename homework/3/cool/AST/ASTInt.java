package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTInt extends ASTExpression {
    Token token;

    public ASTInt(final ParserRuleContext context, final Token intt) {
        super(context);
        this.token = intt;
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

    /**
     * @return the value
     */
    public Integer getValue() {
        return Integer.parseInt(token.getText());
    }
}
