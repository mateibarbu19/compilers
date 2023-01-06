package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public class ASTBlock extends ASTExpression {
    List<ASTExpression> statements;

    public ASTBlock(final ParserRuleContext context, final List<ASTExpression> statements) {
        super(context);
        this.statements = statements;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the statements
     */
    public List<ASTExpression> getStatements() {
        return statements;
    }
}
