package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTBlock extends ASTExpression {
    List<ASTExpression> statements;

    public ASTBlock(final Token start, final List<ASTExpression> statements) {
        super(start);
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
