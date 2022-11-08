import org.antlr.v4.runtime.Token;
import java.util.*;

// Rădăcina ierarhiei de clase reprezentând nodurile arborelui de sintaxă
// abstractă (AST). Singura metodă permite primirea unui visitor.
public abstract class ASTNode {
    // Reținem un token descriptiv, pentru a putea afișa ulterior
    // informații legate de linia și coloana eventualelor erori semantice.
    Token token;

    ASTNode(Token token) {
        this.token = token;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

// Orice expresie.
abstract class Expression extends ASTNode {
    Expression(Token token) {
        super(token);
    }
}

// Identificatori
class Id extends Expression {
    Id(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Literali întregi
class Int extends Expression {
    Int(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class FloatNode extends Expression {
    FloatNode(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Bool extends Expression {
    Bool(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

// Construcția if.
class If extends Expression {
    // Sunt necesare trei câmpuri pentru cele trei componente ale expresiei.
    Expression cond;
    Expression thenBranch;
    Expression elseBranch;

    If(Expression cond,
            Expression thenBranch,
            Expression elseBranch,
            Token start) {
        super(start);
        this.cond = cond;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Assign extends Expression {
    Id name;
    Expression e;

    Assign(Id name, Expression e, Token start) {
        super(start);
        this.name = name;
        this.e = e;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Paren extends Expression {
    Expression e;

    Paren(Expression e, Token start) {
        super(start);
        this.e = e;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Call extends Expression {
    Id name;
    List<Expression> args;

    Call(Id name, List<Expression> args, Token start) {
        super(start);
        this.name = name;
        this.args = args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class Minus extends Expression {
    Expression e;

    Minus(Expression e, Token start) {
        super(start);
        this.e = e;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class MultDiv extends Expression {
    Expression left;
    Token op;
    Expression right;

    MultDiv(Expression left, Token op, Expression right, Token start) {
        super(start);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}

class PlusMinus extends Expression {
    Expression left;
    Token op;
    Expression right;

    PlusMinus(Expression left, Token op, Expression right, Token start) {
        super(start);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}


class Relational extends Expression {
    Expression left;
    Token op;
    Expression right;

    Relational(Expression left, Token op, Expression right, Token start) {
        super(start);
        this.left = left;
        this.op = op;
        this.right = right;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}