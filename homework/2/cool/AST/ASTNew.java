package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.symbols.TypeSymbol;
import cool.visitor.ASTVisitor;

public class ASTNew extends ASTExpression {
    ASTTypeId type;
    TypeSymbol runtimeType;

    public ASTNew(final ParserRuleContext context, final ASTTypeId type) {
        super(context);
        this.type = type;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the type
     */
    public ASTTypeId getType() {
        return type;
    }

    /**
     * @return the runtimeType
     */
    public TypeSymbol getRuntimeType() {
        return runtimeType;
    }

    /**
     * @param runtimeType the runtimeType to set
     */
    public void setRuntimeType(TypeSymbol runtimeType) {
        this.runtimeType = runtimeType;
    }
}
