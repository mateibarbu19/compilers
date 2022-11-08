public interface ASTVisitor<T> {
    T visit(Id id);
    T visit(Int intt);
    T visit(If iff);
    T visit(Bool booll);
    T visit(Assign assign);
    T visit(Paren paren);
    T visit(FloatNode floatNode);
    T visit(Call call);
    T visit(Minus minus);
    T visit(MultDiv multDiv);
    T visit(PlusMinus plustDiv);
    T visit(Relational relational);
}
