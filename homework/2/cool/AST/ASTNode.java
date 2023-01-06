package cool.AST;

import org.antlr.v4.runtime.Token;

import cool.visitor.ASTVisitor;

public abstract class ASTNode {
    Token token;

    public ASTNode(final Token token) {
        this.token = token;
    }

    public abstract <T> T accept(ASTVisitor<T> visitor);

    /**
     * @return the token
     */
    public Token getToken() {
        return token;
    }
}
