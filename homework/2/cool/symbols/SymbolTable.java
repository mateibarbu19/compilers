package cool.symbols;

import java.io.File;

import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;
import cool.parser.CoolParser;
import cool.scopes.Scope;
import cool.scopes.GlobalScope;

public class SymbolTable {
    public static Scope globals;

    private static boolean semanticErrors;

    public static void defineBasicClasses() {
        globals = new GlobalScope();
        semanticErrors = false;

        globals.add(TypeSymbol.OBJECT);
        globals.add(TypeSymbol.BOOL);
        globals.add(TypeSymbol.INT);
        globals.add(TypeSymbol.STRING);
        globals.add(TypeSymbol.IO);

        // Object methods
        var abort = new MethodSymbol("abort", TypeSymbol.OBJECT, TypeSymbol.OBJECT);
        TypeSymbol.OBJECT.add(abort);

        var typeName = new MethodSymbol("type_name", TypeSymbol.OBJECT, TypeSymbol.STRING);
        TypeSymbol.OBJECT.add(typeName);

        // TODO copy

        // IO methods
        // TODO out_string, out_int

        var inString = new MethodSymbol("in_string", TypeSymbol.IO, TypeSymbol.STRING);
        TypeSymbol.IO.add(inString);

        var inInt = new MethodSymbol("in_int", TypeSymbol.IO, TypeSymbol.INT);
        TypeSymbol.IO.add(inInt);

        // String methods
        var concat = new MethodSymbol("concat", TypeSymbol.STRING, TypeSymbol.STRING);
        var otherStr = new IdSymbol("x", TypeSymbol.STRING);
        concat.add(otherStr);
        TypeSymbol.STRING.add(concat);

        var length = new MethodSymbol("length", TypeSymbol.STRING, TypeSymbol.INT);
        TypeSymbol.STRING.add(length);

        var substr = new MethodSymbol("substr", TypeSymbol.STRING, TypeSymbol.STRING);
        var index = new IdSymbol("i", TypeSymbol.INT);
        var len = new IdSymbol("l", TypeSymbol.INT);
        substr.add(index);
        substr.add(len);
        TypeSymbol.STRING.add(substr);
    }

    /**
     * Displays a semantic error message.
     * 
     * @param ctx  Used to determine the enclosing class context of this error,
     *             which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str  The error message.
     */
    public static void error(ParserRuleContext ctx, Token info, String str) {
        while (!(ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();

        String message = "\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + "\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;

        System.err.println(message);

        semanticErrors = true;
    }

    public static void error(String str) {
        String message = "Semantic error: " + str;

        System.err.println(message);

        semanticErrors = true;
    }

    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }
}
