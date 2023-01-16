package cool.codegen;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

public class CodeGenHelper {
    private static int stringConstCounter = 7;
    private static int intConstCounter = 5;

    static STGroupFile templates;

    public CodeGenHelper(STGroupFile t) {
        templates = t;
    }

    public ST getStringConst(String value) {
        return templates.getInstanceOf("strConstant")
                .add("i", stringConstCounter++)
                .add("size", (int) (4 + Math.ceil((value.length() + 1) / 4)))
                .add("len", value.length())
                .add("str", "\"" + value + "\"");
    }

    public ST getIntConst(int value) {
        return templates.getInstanceOf("intConstant")
                .add("i", intConstCounter++)
                .add("n", value);
    }

    public ST getWordConst(int immediate) {
        return templates.getInstanceOf("word")
                .add("val", immediate);
    }

    public ST getWordConst(String label) {
        return templates.getInstanceOf("word")
                .add("val", label);
    }
}
