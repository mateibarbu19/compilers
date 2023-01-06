package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public class ASTNew extends ASTExpression {
    ASTTypeId type;

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
}
