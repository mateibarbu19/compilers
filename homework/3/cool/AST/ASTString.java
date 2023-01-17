package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTString extends ASTExpression {
    Token token;

    public ASTString(final ParserRuleContext context, final Token string) {
        super(context);
        this.token = string;
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
    public String getValue() {
        return token.getText();
    }
}
