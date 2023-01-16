package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.symbols.MethodSymbol;
import cool.visitor.ASTVisitor;

public class ASTMethodId extends ASTExpression {
    Token token;
    MethodSymbol symbol;

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

    /**
     * @return the symbol
     */
    public MethodSymbol getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(MethodSymbol symbol) {
        this.symbol = symbol;
    }
}
