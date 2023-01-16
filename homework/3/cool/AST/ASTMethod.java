package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public class ASTMethod extends ASTFeature {
    ASTMethodId name;
    List<ASTFormal> parameters;
    ASTTypeId type;
    ASTExpression body;

    public ASTMethod(final ParserRuleContext context, final ASTMethodId name,
            final List<ASTFormal> parameters, final ASTTypeId type,
            final ASTExpression body) {
        super(context);
        this.name = name;
        this.type = type;
        this.parameters = parameters;
        this.body = body;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the name
     */
    public ASTMethodId getName() {
        return name;
    }

    /**
     * @return the parameters
     */
    public List<ASTFormal> getParameters() {
        return parameters;
    }

    /**
     * @return the type
     */
    public ASTTypeId getType() {
        return type;
    }

    /**
     * @return the body
     */
    public ASTExpression getBody() {
        return body;
    }

}
