package cool.symbols;

import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.antlr.v4.runtime.*;

import cool.compiler.Compiler;
import cool.parser.CoolParser;
import cool.scopes.Scope;
import cool.scopes.GlobalScope;

public class SymbolTable {
    public static Scope globals;

    private static boolean semanticErrors;

    private static HashMap<String, List<String>> dispatchTables = new HashMap<String, List<String>>() {
        {
            put("Object", List.of("Object.abort", "Object.type_name", "Object.copy"));
            put("IO", List.of("Object.abort", "Object.type_name", "Object.copy", "IO.out_string", "IO.out_int",
                    "IO.in_string", "IO.in_int"));
            put("Int", List.of("Object.abort", "Object.type_name", "Object.copy"));
            put("String", List.of("Object.abort", "Object.type_name", "Object.copy", "String.length", "String.concat",
                    "String.substr"));
            put("Bool", List.of("Object.abort", "Object.type_name", "Object.copy"));
        }
    };

    private static HashMap<String, List<String>> fieldTables = new HashMap<String, List<String>>();

    public static void defineBasicClasses() {
        globals = new GlobalScope();
        semanticErrors = false;

        globals.add(TypeSymbol.OBJECT);
        globals.add(TypeSymbol.BOOL);
        globals.add(TypeSymbol.INT);
        globals.add(TypeSymbol.STRING);
        globals.add(TypeSymbol.IO);
        globals.add(TypeSymbol.SELF_TYPE);

        // Object methods
        var abort = new MethodSymbol("abort", TypeSymbol.OBJECT, TypeSymbol.OBJECT);
        TypeSymbol.OBJECT.add(abort);

        var typeName = new MethodSymbol("type_name", TypeSymbol.OBJECT, TypeSymbol.STRING);
        TypeSymbol.OBJECT.add(typeName);

        var copy = new MethodSymbol("copy", TypeSymbol.OBJECT, TypeSymbol.SELF_TYPE);
        TypeSymbol.OBJECT.add(copy);

        // IO methods
        var outString = new MethodSymbol("out_string", TypeSymbol.IO, TypeSymbol.SELF_TYPE);
        var theString = new IdSymbol("x", TypeSymbol.STRING);
        outString.add(theString);
        TypeSymbol.IO.add(outString);

        var outInt = new MethodSymbol("out_int", TypeSymbol.IO, TypeSymbol.SELF_TYPE);
        var theInt = new IdSymbol("x", TypeSymbol.INT);
        outInt.add(theInt);
        TypeSymbol.IO.add(outInt);

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

    public static void putInDispatchTables(String className, List<String> methodsNames) {
        dispatchTables.put(className, methodsNames);
    }

    /**
     * @return the dispatchTables
     */
    public static HashMap<String, List<String>> getDispatchTables() {
        return dispatchTables;
    }

    public static void putInFieldTables(String className, List<String> attributesNames) {
        fieldTables.put(className, attributesNames);
    }

    /**
     * @return the fieldTables
     */
    public static HashMap<String, List<String>> getFieldTables() {
        return fieldTables;
    }
}
