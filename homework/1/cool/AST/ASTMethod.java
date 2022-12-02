package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTMethod extends ASTFeature {
    Token name;
    List<ASTFormal> parameters;
    Token type;
    ASTExpression body;

    public ASTMethod(final Token start, final Token name, final List<ASTFormal> parameters, final Token type,
            final ASTExpression body) {
        super(start);
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
    public Token getName() {
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
    public Token getType() {
        return type;
    }

    /**
     * @return the body
     */
    public ASTExpression getBody() {
        return body;
    }

}
