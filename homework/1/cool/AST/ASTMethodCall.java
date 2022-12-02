package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTMethodCall extends ASTExpression {
    ASTExpression caller;
    Token actualCaller;
    Token method;
    List<ASTExpression> arguments;

    public ASTMethodCall(final Token start, final ASTExpression caller, Token actualCaller, final Token method,
            final List<ASTExpression> arguments) {
        super(start);
        this.caller = caller;
        this.actualCaller = actualCaller;
        this.method = method;
        this.arguments = arguments;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the caller
     */
    public ASTExpression getCaller() {
        return caller;
    }

    /**
     * @return the actualCaller
     */
    public Token getActualCaller() {
        return actualCaller;
    }

    /**
     * @return the method
     */
    public Token getMethod() {
        return method;
    }

    /**
     * @return the arguments
     */
    public List<ASTExpression> getArguments() {
        return arguments;
    }
}
