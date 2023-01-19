package cool.visitor;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroupFile;

import cool.AST.ASTAlternative;
import cool.AST.ASTArithmetical;
import cool.AST.ASTAssignment;
import cool.AST.ASTAttribute;
import cool.AST.ASTBlock;
import cool.AST.ASTBool;
import cool.AST.ASTCase;
import cool.AST.ASTClassDefine;
import cool.AST.ASTExpression;
import cool.AST.ASTFeature;
import cool.AST.ASTFormal;
import cool.AST.ASTIf;
import cool.AST.ASTInt;
import cool.AST.ASTIsvoid;
import cool.AST.ASTLet;
import cool.AST.ASTMethod;
import cool.AST.ASTMethodCall;
import cool.AST.ASTMethodId;
import cool.AST.ASTNegative;
import cool.AST.ASTNew;
import cool.AST.ASTNode;
import cool.AST.ASTNot;
import cool.AST.ASTObjectId;
import cool.AST.ASTProgram;
import cool.AST.ASTRelational;
import cool.AST.ASTString;
import cool.AST.ASTTypeId;
import cool.AST.ASTVariable;
import cool.AST.ASTWhile;
import cool.symbols.TypeSymbol;
import cool.codegen.CodeGenHelper;

public class ASTCodeGen implements ASTVisitor<ST> {
    private String filename;

    public ASTCodeGen(String filename) {
        this.filename = filename.split("/")[filename.split("/").length - 1];
    }

    static STGroupFile templates = new STGroupFile("cgen.stg");

    static CodeGenHelper helper = new CodeGenHelper(templates);

    @Override
    public ST visit(ASTAlternative alternative) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTArithmetical arithmetical) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTAssignment assignment) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTAttribute attribute) {
        return attribute.getInitialization().accept(this);
    }

    @Override
    public ST visit(ASTBlock block) {
        var blockST = templates.getInstanceOf("sequence");

        block.getStatements().forEach(e -> blockST.add("e", e.accept(this)));

        return blockST;
    }

    @Override
    public ST visit(ASTBool bool) {
        return templates.getInstanceOf("returnAddres").add("addr", helper.getBoolConstAddress(bool.getValue()));
    }

    @Override
    public ST visit(ASTCase casee) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTClassDefine classDefine) {
        int nrAttributes = 0;
        var type = classDefine.getType();
        List<String> classMethods = new LinkedList<>();

        while (type != null) {
            nrAttributes += type.getAttributesNames().size() - 1;

            final var typeName = type.getName();
            var typeMethods = type.getMethodsNames()
                    .stream()
                    .map(methodName -> typeName + "." + methodName)
                    .collect(Collectors.toList());

            typeMethods.addAll(classMethods);
            classMethods = typeMethods;

            type = (TypeSymbol) type.getParent();
        }

        // get this class
        List<ASTFeature> attributesList = new LinkedList<>();
        var currClass = classDefine;
        while (currClass != null) {
            var x = currClass
                    .getFeatures()
                    .stream()
                    .filter(f -> f instanceof ASTAttribute)
                    .collect(Collectors.toList());
            x.addAll(attributesList);
            attributesList = x;

            currClass = ((TypeSymbol) currClass.getType().getParent()).getClassDefine();
        }
        var attributesDefault = templates.getInstanceOf("sequence");
        attributesList.forEach(a -> attributesDefault.add("e", helper.getAttributeDefaultAddress((ASTAttribute) a)));

        var name = classDefine.getName().getToken().getText();
        helper.addClassDefine(name, nrAttributes, classMethods, attributesDefault);

        ST attributes = templates.getInstanceOf("sequence");
        {
            final var tmp = attributesList;
            classDefine
                    .getFeatures()
                    .stream()
                    .filter(f -> f instanceof ASTAttribute && ((ASTAttribute) f).getInitialization() != null)
                    .forEach(a -> attributes
                            .add("e", templates.getInstanceOf("fieldInit")
                                    .add("expr", a.accept(this))
                                    .add("offset", 4 * tmp.indexOf(a) + 12)));
        }

        helper.addClassInit(name, classDefine.getType().getParentName(), attributes);

        // visit methods
        classDefine.getFeatures().stream().filter(f -> f instanceof ASTMethod).forEach(f -> f.accept(this));

        return null;
    }

    @Override
    public ST visit(ASTExpression expression) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTFeature feature) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTFormal formal) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTIf iff) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTInt intt) {
        return templates.getInstanceOf("returnAddres").add("addr", helper.getIntConstAddress(intt.getValue()));
    }

    @Override
    public ST visit(ASTIsvoid isvoid) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTLet let) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTMethod method) {
        // Optional.ofNullable(method.getBody().accept(this)).map(st ->
        // st.render()).orElse(""),
        var symbol = method.getName().getSymbol();
        var className = ((TypeSymbol) symbol.getParent()).getName();

        // TODO add body evaluation
        var body = method.getBody().accept(this);

        helper.addMethod(
                className + "." + symbol.getName(),
                body,
                method.getParameters().size());

        return null;
    }

    @Override
    public ST visit(ASTMethodCall methodCall) {
        var arguments = templates.getInstanceOf("sequence");
        methodCall.getArguments().forEach(a -> arguments.add("e", templates.getInstanceOf("putWordOnStack")
                                                                             .add("address", a.accept(this))));

        if (methodCall.getCaller() != null) {
            if (methodCall.getCaller() instanceof ASTObjectId && ((ASTObjectId) methodCall.getCaller()).getToken().getText().equals("self")) {
                return helper.getMethodCall(methodCall, filename, null, arguments);
            }
            return helper.getMethodCall(methodCall, filename, methodCall.getCaller().accept(this), arguments);
        }
        return helper.getMethodCall(methodCall, filename, null, arguments);
    }

    @Override
    public ST visit(ASTMethodId methodId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTNegative negative) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTNew neww) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTNode node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTNot not) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTObjectId objectId) {
        if (!(objectId.getSymbol().getScope() instanceof TypeSymbol))
            throw new UnsupportedOperationException("Not yet implemented");

        var attributeIndex = ((TypeSymbol) objectId.getSymbol().getScope()).getAttributesNames().indexOf(objectId.getToken().getText());
        return templates.getInstanceOf("loadWordFromClass")
                        .add("offset", 4 * (attributeIndex + 4));
    }

    @Override
    public ST visit(ASTProgram program) {
        helper.addStringConst(filename);

        program.getClasses().forEach(this::visit);

        // assembly-ing it all together. HA! get it?
        var programST = templates.getInstanceOf("program");
        programST.add("strConstants", helper.strConstants);
        programST.add("intConstants", helper.intConstants);
        programST.add("classNameTabs", helper.classNameTabs);
        programST.add("classObjTabs", helper.classObjTabs);
        programST.add("classProtObjs", helper.classProtObjs);
        programST.add("classDispatchTabs", helper.classDispatchTabs);
        programST.add("classInits", helper.classInits);
        programST.add("methodDefines", helper.methodDefines);

        return programST;
    }

    @Override
    public ST visit(ASTRelational relational) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTString string) {
        return templates.getInstanceOf("returnAddres").add("addr", helper.getStringConstAddress(string.getValue()));
    }

    @Override
    public ST visit(ASTTypeId typeId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTVariable variable) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTWhile whilee) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
