import java.io.IOException;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Test {

    public static void main(String[] args) throws IOException {
        var input = CharStreams.fromFileName("inputs/if.txt");

        var lexer = new CPLangLexer(input);
        var tokenStream = new CommonTokenStream(lexer);

        var parser = new CPLangParser(tokenStream);
        var tree = parser.expr();
        System.out.println(tree.toStringTree(parser));

        // Visitor-ul de mai jos parcurge arborele de derivare și construiește
        // un arbore de sintaxă abstractă (AST).
        var astConstructionVisitor = new CPLangParserBaseVisitor<ASTNode>() {
            @Override
            public ASTNode visitId(CPLangParser.IdContext ctx) {
                return new Id(ctx.ID().getSymbol());
            }

            @Override
            public ASTNode visitInt(CPLangParser.IntContext ctx) {
                return new Int(ctx.INT().getSymbol());
            }

            @Override
            public ASTNode visitFloat(CPLangParser.FloatContext ctx) {
                return new FloatNode(ctx.FLOAT().getSymbol());
            }

            @Override
            public ASTNode visitBool(CPLangParser.BoolContext ctx) {
                return new Bool(ctx.BOOL().getSymbol());
            }

            @Override
            public ASTNode visitIf(CPLangParser.IfContext ctx) {
                return new If((Expression) visit(ctx.cond),
                        (Expression) visit(ctx.thenBranch),
                        (Expression) visit(ctx.elseBranch),
                        ctx.start);
            }

            // DONE 3: Completati cu alte metode pentru a trece din arborele
            // generat automat in reprezentarea AST-ului dorit

            @Override
            public ASTNode visitAssign(CPLangParser.AssignContext ctx) {
                return new Assign((Id) visit(ctx.ID()),
                        (Expression) visit(ctx.expr()),
                        ctx.start);
            }

            @Override
            public ASTNode visitParen(CPLangParser.ParenContext ctx) {
                return new Paren((Id) visit(ctx.expr()),
                        ctx.start);
            }

            @Override
            public ASTNode visitCall(CPLangParser.CallContext ctx) {
                return new Call((Id) visit(ctx.ID()),
                        ctx.expr()
                                .stream()
                                .map(e -> visit(e))
                                .map(Expression.class::cast)
                                .collect(Collectors.toList()),
                        ctx.start);
            }

            @Override
            public ASTNode visitUnaryMinus(CPLangParser.UnaryMinusContext ctx) {
                return new Minus(
                        (Expression) visit(ctx.expr()),
                        ctx.start);
            }

            @Override
            public ASTNode visitMultDiv(CPLangParser.MultDivContext ctx) {
                return new MultDiv((Expression) visit(ctx.left),
                        ctx.op,
                        (Expression) visit(ctx.right),
                        ctx.start);
            }

            @Override
            public ASTNode visitPlusMinus(CPLangParser.PlusMinusContext ctx) {
                return new PlusMinus((Expression) visit(ctx.left),
                        ctx.op,
                        (Expression) visit(ctx.right),
                        ctx.start);
            }

            @Override
            public ASTNode visitRelational(CPLangParser.RelationalContext ctx) {
                return new Relational((Expression) visit(ctx.left),
                        ctx.op,
                        (Expression) visit(ctx.right),
                        ctx.start);
            }
        };

        // ast este AST-ul proaspăt construit pe baza arborelui de derivare.
        var ast = astConstructionVisitor.visit(tree);

        // Acest visitor parcurge AST-ul generat mai sus.
        // ATENȚIE! Avem de-a face cu două categorii de visitori:
        // (1) Cei pentru arborele de derivare, care extind
        // CPLangParserBaseVisitor<T> și
        // (2) Cei pentru AST, care extind ASTVisitor<T>.
        // Aveți grijă să nu îi confundați!
        var printVisitor = new ASTVisitor<Void>() {
            int indent = 0;
            
            @Override
            public Void visit(Id id) {
                printIndent("ID " + id.token.getText());
                return null;
            }
            
            @Override
            public Void visit(Int intt) {
                printIndent("INT " + intt.token.getText());
                return null;
            }
            
            @Override
            public Void visit(If iff) {
                printIndent("IF");
                indent++;
                iff.cond.accept(this);
                iff.thenBranch.accept(this);
                iff.elseBranch.accept(this);
                indent--;
                return null;
            }
            // TODO 4: Afisati fiecare nod astfel incat nivelul pe care acesta
            // se afla in AST sa fie reprezentat de numarul de tab-uri.
            // Folositi functia de mai jos 'printIndent' si incrementati /
            // decrementati corespunzator numarul de tab-uri

            @Override
            public Void visit(Bool booll) {
                printIndent("Bool " + booll.token.getText());
                return null;
            };

            @Override
            public Void visit(Assign assign) {
                printIndent("ASSIGN");
                indent++;
                assign.name.accept(this);
                assign.e.accept(this);
                indent--;
                return null;
            }

            @Override
            public Void visit(Paren paren) {
                printIndent("PAREN");
                indent++;
                paren.e.accept(this);
                indent--;
                return null;
            }

            @Override
            public Void visit(FloatNode floatNode) {
                printIndent("FLOAT " + floatNode.token.getText());
                return null;
            }

            @Override
            public Void visit(Call call) {
                printIndent("CALL");
                indent++;
                call.name.accept(this);
                call.args.forEach(a -> a.accept(this));
                indent--;
                return null;
            }

            @Override
            public Void visit(Minus minus) {
                printIndent("MIUS");
                indent++;
                minus.e.accept(this);
                indent--;
                return null;
            }

            @Override
            public Void visit(MultDiv multDiv) {
                printIndent("MULTDIV");
                indent++;
                multDiv.left.accept(this);
                printIndent(multDiv.op.getText());
                multDiv.right.accept(this);
                indent--;
                return null;
            }

            @Override
            public Void visit(PlusMinus plustDiv) {
                printIndent("PLUSMINUS");
                indent++;
                plustDiv.left.accept(this);
                printIndent(plustDiv.op.getText());
                plustDiv.right.accept(this);
                indent--;
                return null;
            }

            @Override
            public Void visit(Relational relational) {
                printIndent("RELATIONAL");
                indent++;
                relational.left.accept(this);
                printIndent(relational.op.getText());
                relational.right.accept(this);
                indent--;
                return null;
            }

            void printIndent(String str) {
                for (int i = 0; i < indent; i++)
                    System.out.print("\t");
                System.out.println(str);
            }
        };

        System.out.println("The AST is");
        ast.accept(printVisitor);
    }

}
