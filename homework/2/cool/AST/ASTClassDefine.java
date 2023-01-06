package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.ParserRuleContext;

import cool.visitor.ASTVisitor;

public class ASTClassDefine extends ASTNode {
    ASTTypeId name;
    ASTTypeId parent;
    List<ASTFeature> features;

    public ASTClassDefine(final ParserRuleContext context, final ASTTypeId name,
            final ASTTypeId parent, final List<ASTFeature> features) {
        super(context);
        this.name = name;
        this.parent = parent;
        this.features = features;
    }

    public <T> T accept(final ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /**
     * @return the name
     */
    public ASTTypeId getName() {
        return name;
    }

    /**
     * @return the parent
     */
    public ASTTypeId getParent() {
        return parent;
    }

    /**
     * @return the features
     */
    public List<ASTFeature> getFeatures() {
        return features;
    }
}