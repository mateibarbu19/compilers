package cool.codegen;

import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

public class CodeGenHelper {
    private static int stringConstCounter = 6;
    private static int intConstCounter = 5;

    static STGroupFile templates;

    public ST strConstants;
    public ST intConstants;
    public ST classNameTabs;
    public ST classObjTabs;
    public ST classProtObjs;
    public ST classDispatchTabs;
    public ST classInits;
    public ST methodDefines;


    public CodeGenHelper(STGroupFile t) {
        templates = t;
        strConstants = templates.getInstanceOf("sequence");
        intConstants = templates.getInstanceOf("sequence");
        classNameTabs = templates.getInstanceOf("sequence");
        classObjTabs = templates.getInstanceOf("sequence");
        classProtObjs = templates.getInstanceOf("sequence");
        classDispatchTabs = templates.getInstanceOf("sequence");
        classInits = templates.getInstanceOf("sequence");
        methodDefines = templates.getInstanceOf("sequence");
    }

    public ST getStringConst(String value) {
        return templates.getInstanceOf("strConstant")
                .add("i", stringConstCounter++)
                .add("size", (int) (4 + Math.ceil((value.length() + 1) / 4)))
                .add("len", value.length())
                .add("str", "\"" + value + "\"");
    }


    // HELPERS
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

    public void addStringConst(String value) {
        strConstants.add("e", getStringConst(value));
    }

    public void addIntConst(int value) {
        intConstants.add("e", getIntConst(value));
    }

    // END HELPERS



    // CLASS HANDLING

    public void addClassDefine(String name) {
        addStringConst(name);
        addClassObjTab(name);
        // TODO: check Danger zone
        classNameTabs.add("e", getWordConst("str_const" + (stringConstCounter - 1)));
        classProtObjs.add("e", templates.getInstanceOf("classPrototype")
                                .add("name", name)
                                .add("i", stringConstCounter - 1)
                                // TODO: add size calculation and class fields
                                .add("size", 3));
    }

    public void addClassDispatchTab(String name, List<String> methods) {
        ST classDispatchTab = templates.getInstanceOf("classDisptachTab")
                .add("name", name);

        for (String method : methods) {
            classDispatchTab.add("methods", getWordConst(method));
        }

        classDispatchTabs.add("e", classDispatchTab);
    }

    private void addClassObjTab(String name) {
        classObjTabs.add("e", getWordConst(name + "_init"));
        classObjTabs.add("e", getWordConst(name + "_protObj"));
    }

    // END CLASS HANDLING
}
