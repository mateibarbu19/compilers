package cool.AST;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public class ASTFormal extends ASTNode {
    ASTObjectId name;
    ASTTypeId type;

    public ASTFormal(final ParserRuleContext context, final ASTObjectId name, final ASTTypeId type) {
        super(context);
        this.name = name;
        this.type = type;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the name
     */
    public ASTObjectId getName() {
        return name;
    }

    /**
     * @return the type
     */
    public ASTTypeId getType() {
        return type;
    }
}
