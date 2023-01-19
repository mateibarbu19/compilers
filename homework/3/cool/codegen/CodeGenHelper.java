package cool.codegen;

import java.util.HashMap;
import java.util.List;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import cool.AST.ASTAttribute;
import cool.AST.ASTMethodCall;
import cool.symbols.MethodSymbol;
import cool.symbols.TypeSymbol;

public class CodeGenHelper {
    private static int stringConstCounter = 6;
    private static int intConstCounter = 5;
    private static int dispatchCounter = 0;
    private static int classCounter = 5;

    private static HashMap<Integer, String> intConsts = new HashMap<Integer, String>() {
        {
            put(0, "int_const0");
            put(6, "int_const1");
            put(2, "int_const2");
            put(3, "int_const3");
            put(4, "int_const4");
        }
    };

    private static HashMap<String, String> strConsts = new HashMap<String, String>() {
        {
            put("", "str_const0");
            put("Object", "str_const1");
            put("IO", "str_const2");
            put("Int", "str_const3");
            put("String", "str_const4");
            put("Bool", "str_const5");
        }
    };

    private static HashMap<Boolean, String> boolConsts = new HashMap<Boolean, String>() {
        {
            put(false, "bool_const0");
            put(true, "bool_const1");
        }
    };

    private static HashMap<String, List<String>> dispatchTables = new HashMap<String, List<String>>() {
        {
            put("Object", List.of("Object.abort", "Object.type_name", "Object.copy"));
            put("IO", List.of("Object.abort", "Object.type_name", "Object.copy", "IO.out_string", "IO.out_int", "IO.in_string", "IO.in_int"));
            put("Int", List.of("Object.abort", "Object.type_name", "Object.copy"));
            put("String", List.of("Object.abort", "Object.type_name", "Object.copy", "String.length", "String.concat", "String.substr"));
            put("Bool", List.of("Object.abort", "Object.type_name", "Object.copy"));
        }
    };

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

    // region HELPERS
    public ST getStringConst(String value) {
        strConsts.put(value, "str_const" + stringConstCounter);
        var size = (int) (4 + Math.ceil((float) (value.length() + 1) / 4));
        return templates.getInstanceOf("strConstant")
                .add("i", stringConstCounter++)
                .add("size", size)
                .add("len", getIntConstAddress(value.length()))
                .add("str", "\"" + value + "\"");
    }

    public ST getIntConst(int value) {
        intConsts.put(value, "int_const" + intConstCounter);
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

    // returns const index
    public int addStringConst(String value) {
        strConstants.add("e", getStringConst(value));
        return stringConstCounter - 1;
    }

    // returns const index
    public int addIntConst(int value) {
        intConstants.add("e", getIntConst(value));
        return intConstCounter - 1;
    }

    public String getIntConstAddress(int value) {
        if (!intConsts.containsKey(value)) {
            addIntConst(value);
        }
        return intConsts.get(value);
    }

    public String getStringConstAddress(String value) {
        if (!strConsts.containsKey(value)) {
            addStringConst(value);
        }
        return strConsts.get(value);
    }

    public String getBoolConstAddress(Boolean value) {
        return boolConsts.get(value);
    }

    //endregion

    //region CLASS HANDLING

    public void addClassDefine(String name, int nrAttributes, List<String> methodsNames, ST attributes) {
        var i = addStringConst(name);

        addClassObjTab(name);

        // TODO: check Danger zone 👻
        classNameTabs.add("e", getWordConst("str_const" + i));

        classProtObjs.add("e", templates.getInstanceOf("classPrototype")
                .add("name", name)
                .add("i", classCounter++)
                .add("size", 3 + nrAttributes)
                .add("attributes", attributes));

        addClassDispatchTab(name, methodsNames);
        dispatchTables.put(name, methodsNames);
    }

    public void addClassInit(String name, String parent, ST attributes) {
        classInits.add("e", templates.getInstanceOf("classInit")
                .add("name", name)
                .add("parent", parent)
                .add("fieldsInits", attributes));
    }

    public void addClassDispatchTab(String name, List<String> methodsNames) {
        ST classDispatchTab = templates.getInstanceOf("classDisptachTab")
                .add("name", name);

        for (String method : methodsNames) {
            classDispatchTab.add("methods", getWordConst(method));
        }

        classDispatchTabs.add("e", classDispatchTab);
    }

    private void addClassObjTab(String name) {
        classObjTabs.add("e", getWordConst(name + "_init"));
        classObjTabs.add("e", getWordConst(name + "_protObj"));
    }

    public ST getAttributeDefaultAddress(ASTAttribute attribute) {
        return getAttributeDefault(attribute.getName().getSymbol().getType());
    }

    //endregion

    //region METHOD HANDLING
    public void addMethod(String name, ST body, int nrParmas) {
        var methodDefine = templates.getInstanceOf("methodDefine")
                .add("label", name)
                .add("expression", body);

        if (nrParmas != 0) {
            methodDefine.add("params_size", nrParmas * 4);
        }

        methodDefines.add("e", methodDefine);
    }

    public ST getMethodCall(ASTMethodCall method, String filename, ST callerAddress, ST arguments) {
        var methodName = method.getMethod().getToken().getText();

        var runtimeCallerType = method.getRuntimeCallerType();

        var s = (MethodSymbol) runtimeCallerType.lookup(methodName);

        var offset = getMethodOffset(runtimeCallerType.getName(), ((TypeSymbol) s.getParent()).getName() + "." + methodName);

        var dispatch = templates.getInstanceOf("dispatch")
                            .add("i", dispatchCounter++)
                            .add("filename", getStringConstAddress(filename))
                            .add("methodOffset", offset)
                            .add("fileline", method.getMethod().getToken().getLine());

        if (callerAddress != null) {
            dispatch.add("callerAddress", callerAddress);
        }

        if (arguments != null) {
            dispatch.add("arguments", arguments);
        }

        return dispatch;
    }

    private int getMethodOffset(String cls, String method) {
        return dispatchTables.get(cls).indexOf(method) * 4;
    }
    //endregion

    //region DEFAULTS HANDLING
    public ST getAttributeDefault(TypeSymbol type) {
        if (type == TypeSymbol.INT) {
            return getWordConst(getIntConstAddress(0));
        }
        if (type == TypeSymbol.BOOL) {
            return getWordConst(getBoolConstAddress(false));
        }
        if (type == TypeSymbol.STRING) {
            return getWordConst(getStringConstAddress(""));
        }
        return getWordConst("0");
    }
    //endregion
}
