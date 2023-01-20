package cool.visitor;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
import cool.symbols.IdSymbol;
import cool.symbols.LetSymbol;
import cool.symbols.MethodSymbol;
import cool.symbols.SymbolTable;
import cool.symbols.TypeSymbol;
import cool.codegen.CodeGenHelper;
import cool.scopes.Scope;

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
        String operator = null;

        switch (arithmetical.getOperator().getText()) {
            case "+" -> operator = "add";
            case "-" -> operator = "sub";
            case "*" -> operator = "mul";
            case "/" -> operator = "div";
        }

        return templates.getInstanceOf("arithmetical")
                .add("lhs", arithmetical.getLhs().accept(this))
                .add("operator", operator)
                .add("rhs", arithmetical.getRhs().accept(this));
    }

    @Override
    public ST visit(ASTAssignment assignment) {
        assignment.getName().setIsOnLhs(true);

        return templates.getInstanceOf("sequence")
                .add("e", assignment.getExpression().accept(this))
                .add("e", assignment.getName().accept(this));
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
        var name = classDefine.getName().getToken().getText();
        var attributesList = SymbolTable.getFieldTables().get(name);
        var methodsList = SymbolTable.getDispatchTables().get(name);

        var attributesDefault = templates.getInstanceOf("sequence");
        for (var attributeName : attributesList) {
            IdSymbol symbol = (IdSymbol) classDefine.getType().lookup(attributeName);

            attributesDefault.add("e", helper.getAttributeDefault(symbol.getType()));
        }

        helper.addClassDefine(name,
                attributesList.size(),
                methodsList,
                attributesDefault);

        ST attributes = templates.getInstanceOf("sequence");
        classDefine
                .getFeatures()
                .stream()
                .filter(f -> f instanceof ASTAttribute)
                .map(ASTAttribute.class::cast)
                .filter(a -> a.getInitialization() != null)
                .forEach(a -> attributes
                        .add("e", templates.getInstanceOf("fieldInit")
                                .add("expr", a.accept(this))
                                .add("offset", 4 * attributesList.indexOf(a.getName().getToken().getText()) + 12)));

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
        return helper.getBranch(
                iff.getCondition().accept(this),
                iff.getConsequent().accept(this),
                iff.getAlternative().accept(this));
    }

    @Override
    public ST visit(ASTInt intt) {
        return templates.getInstanceOf("returnAddres").add("addr", helper.getIntConstAddress(intt.getValue()));
    }

    @Override
    public ST visit(ASTIsvoid isvoid) {
        return helper.getIsvoid(
                isvoid.getExpression().accept(this));
    }

    @Override
    public ST visit(ASTLet let) {
        ST variablesInit = templates.getInstanceOf("sequence");
        List<String> variablesList = new LinkedList<>();

        Scope currentScope = let.getSymbol();
        while (currentScope instanceof LetSymbol) {
            var parentVariablesList = ((LetSymbol) currentScope).getVariablesNames();

            parentVariablesList.addAll(variablesList);
            variablesList = parentVariablesList;

            currentScope = currentScope.getParent();
        }

        for (ASTVariable variable : let.getDeclarations()) {
            int variableIndex = variablesList.indexOf(variable.getName().getToken().getText());

            var type = variable.getName().getSymbol().getType();
            ST init = Optional.ofNullable(variable.getInitialization())
                    .map(d -> d.accept(this))
                    .orElse(templates.getInstanceOf("returnAddres").add("addr", helper.getObjectDefaultAddress(type)));

            variablesInit.add("e", templates.getInstanceOf("variableInit")
                    .add("expr", init)
                    .add("offset", -4 * (variableIndex + 1)));
        }

        return templates.getInstanceOf("localVarBlock")
                .add("varSizes", 4 * let.getDeclarations().size())
                .add("initializations", variablesInit)
                .add("body", let.getBody().accept(this));
    }

    @Override
    public ST visit(ASTMethod method) {
        var symbol = method.getName().getSymbol();
        var className = ((TypeSymbol) symbol.getParent()).getName();

        helper.addMethod(
                className + "." + symbol.getName(),
                method.getBody().accept(this),
                method.getParameters().size());

        return null;
    }

    @Override
    public ST visit(ASTMethodCall methodCall) {
        var arguments = templates.getInstanceOf("sequence");
        // copy methodCall arguments
        var args = new LinkedList<>(methodCall.getArguments());
        // reverse arguments
        Collections.reverse(args);
        args.forEach(a -> arguments.add("e", templates.getInstanceOf("putWordOnStack")
                .add("address", a.accept(this))));

        ST callerAddress = Optional.ofNullable(methodCall.getCaller()).map(c -> c.accept(this)).orElse(null);
        String actualCaller = Optional.ofNullable(methodCall.getActualCaller()).map(c -> c.getToken().getText())
                .orElse(null);

        return helper.getMethodCall(methodCall, filename, callerAddress, arguments, actualCaller);
    }

    @Override
    public ST visit(ASTMethodId methodId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTNegative negative) {
        return templates.getInstanceOf("negative")
                .add("expression", negative.getExpression().accept(this));
    }

    @Override
    public ST visit(ASTNew neww) {
        var typeName = neww.getType().getToken().getText();

        if (typeName.equals("SELF_TYPE")) {
            return templates.getInstanceOf("newSELF_TYPE");
        }
        return templates.getInstanceOf("newObject")
                .add("name", typeName);
    }

    @Override
    public ST visit(ASTNode node) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTNot not) {
        return helper.getNot(not.getExpression().accept(this));
    }

    @Override
    public ST visit(ASTObjectId objectId) {
        if (objectId.getToken().getText().equals("self")) {
            return templates.getInstanceOf("returnSelf");
        }

        if (objectId.getSymbol().getScope() instanceof TypeSymbol) {
            var className = ((TypeSymbol) objectId.getSymbol().getReferencedScope().getParent()).getName();
            var classAttributes = SymbolTable.getFieldTables().get(className);

            var attributeIndex = classAttributes.indexOf(objectId.getToken().getText());

            if (Optional.ofNullable(objectId.getIsOnLhs()).orElse(false)) {
                return templates.getInstanceOf("saveWordInClass")
                        .add("offset", 4 * (attributeIndex + 3));
            }
            return templates.getInstanceOf("loadWordFromClass")
                    .add("offset", 4 * (attributeIndex + 3));
        } else if (objectId.getSymbol().getScope() instanceof MethodSymbol) {
            var definedInMethod = ((MethodSymbol) objectId.getSymbol().getScope());

            var attributeIndex = definedInMethod
                    .getParametersNames()
                    .indexOf(objectId.getToken().getText());

            if (Optional.ofNullable(objectId.getIsOnLhs()).orElse(false)) {
                return templates.getInstanceOf("saveWordInArguments")
                        .add("offset", 4 * (attributeIndex + 3));
            }

            return templates.getInstanceOf("loadWordFromArguments")
                    .add("offset", 4 * (attributeIndex + 3));
        } else if (objectId.getSymbol().getScope() instanceof LetSymbol) {
            List<String> variablesList = new LinkedList<>();

            Scope currentScope = objectId.getSymbol().getScope();
            while (currentScope instanceof LetSymbol) {
                var parentVariablesList = ((LetSymbol) currentScope).getVariablesNames();

                parentVariablesList.addAll(variablesList);
                variablesList = parentVariablesList;

                currentScope = currentScope.getParent();
            }

            var variableIndex = variablesList.indexOf(objectId.getToken().getText());

            if (Optional.ofNullable(objectId.getIsOnLhs()).orElse(false)) {
                return templates.getInstanceOf("saveWordInArguments")
                        .add("offset", -4 * (variableIndex + 1));
            }

            return templates.getInstanceOf("loadWordFromArguments")
                    .add("offset", -4 * (variableIndex + 1));
        }

        throw new UnsupportedOperationException("Not yet implemented");
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
