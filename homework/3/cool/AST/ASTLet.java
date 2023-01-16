package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.symbols.LetSymbol;
import cool.visitor.ASTVisitor;

public class ASTLet extends ASTExpression {
    List<ASTVariable> declarations;
    ASTExpression body;
    LetSymbol symbol;

    public ASTLet(final ParserRuleContext context, final List<ASTVariable> declarations,
            final ASTExpression body) {
        super(context);
        this.declarations = declarations;
        this.body = body;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the declarations
     */
    public List<ASTVariable> getDeclarations() {
        return declarations;
    }

    /**
     * @return the body
     */
    public ASTExpression getBody() {
        return body;
    }

    /**
     * @return the symbol
     */
    public LetSymbol getSymbol() {
        return symbol;
    }

    /**
     * @param symbol the symbol to set
     */
    public void setSymbol(LetSymbol symbol) {
        this.symbol = symbol;
    }
}
