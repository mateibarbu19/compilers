package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTAttribute extends ASTFeature {
    ASTObjectId name;
    ASTTypeId type;
    ASTExpression initialization;

    public ASTAttribute(final Token start, final ASTObjectId name, final ASTTypeId type, final ASTExpression initialization) {
        super(start);
        this.name = name;
        this.type = type;
        this.initialization = initialization;
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

    /**
     * @return the expression
     */
    public ASTExpression getInitialization() {
        return initialization;
    }

}
