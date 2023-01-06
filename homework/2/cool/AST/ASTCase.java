package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;
import cool.visitor.ASTVisitor;

public class ASTCase extends ASTExpression {
    ASTExpression condition;
    List<ASTAlternative> alternatives;

    public ASTCase(final ParserRuleContext context, final ASTExpression condition,
            final List<ASTAlternative> alternatives) {
        super(context);
        this.condition = condition;
        this.alternatives = alternatives;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the expression
     */
    public ASTExpression getCondition() {
        return condition;
    }

    /**
     * @return the alternatives
     */
    public List<ASTAlternative> getAlternatives() {
        return alternatives;
    }
}
