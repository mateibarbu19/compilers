package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.symbols.IdSymbol;
import cool.visitor.ASTVisitor;

public class ASTObjectId extends ASTExpression {
    Token token;
    IdSymbol symbol;
    Boolean isOnLhs;

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

    /**
     * @return the symbol
     */
    public IdSymbol getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(IdSymbol symbol) {
        this.symbol = symbol;
    }

    /**
     * @return the isOnLhs
     */
    public Boolean getIsOnLhs() {
        return isOnLhs;
    }

    /**
     * @param isOnLhs the isOnLhs to set
     */
    public void setIsOnLhs(Boolean isOnLhs) {
        this.isOnLhs = isOnLhs;
    }
}
