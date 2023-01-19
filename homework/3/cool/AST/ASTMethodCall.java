package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.symbols.TypeSymbol;
import cool.visitor.ASTVisitor;

public class ASTMethodCall extends ASTExpression {
    ASTExpression caller;
    ASTTypeId actualCaller;
    ASTMethodId method;
    List<ASTExpression> arguments;
    TypeSymbol runtimeCallerType;

    public ASTMethodCall(final ParserRuleContext context, final ASTExpression caller,
            ASTTypeId actualCaller, final ASTMethodId method,
            final List<ASTExpression> arguments) {
        super(context);
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
    public ASTTypeId getActualCaller() {
        return actualCaller;
    }

    /**
     * @return the method
     */
    public ASTMethodId getMethod() {
        return method;
    }

    /**
     * @return the arguments
     */
    public List<ASTExpression> getArguments() {
        return arguments;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */

    @Override
    public String toString() {
        return "ASTMethodCall [caller=" + caller + ", actualCaller=" + actualCaller + ", method=" + method
                + ", arguments=" + arguments + "]";
    }

    public void setRuntimeCallerType(TypeSymbol type) {
        runtimeCallerType = type;
    }

    public TypeSymbol getRuntimeCallerType() {
        return runtimeCallerType;
    }
}
