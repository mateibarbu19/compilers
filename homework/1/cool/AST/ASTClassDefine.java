package cool.AST;

import java.util.List;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public class ASTClassDefine extends ASTNode {
    Token name;
    Token parent;
    List<ASTFeature> features;

    public ASTClassDefine(final Token start, final Token name, final Token parent, final List<ASTFeature> features) {
        super(start);
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
    public Token getName() {
        return name;
    }

    /**
     * @return the parent
     */
    public Token getParent() {
        return parent;
    }

    /**
     * @return the features
     */
    public List<ASTFeature> getFeatures() {
        return features;
    }
}