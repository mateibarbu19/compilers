package cool.visitor;

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
import cool.codegen.CodeGenHelper;

public class ASTCodeGen implements ASTVisitor<ST> {
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTBlock block) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTBool bool) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTCase casee) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTClassDefine classDefine) {
        // Define the class
        if (classDefine.getName().getToken().getText().equals("Main"))
            // TODO : handle Main class
            return null;

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTMethodCall methodCall) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTProgram program) {
        ST strConstants = templates.getInstanceOf("sequence")
                .add("e", helper.getStringConst("Hello World!"));

        ST intConstants = templates.getInstanceOf("sequence")
                .add("e", helper.getIntConst(123));

        ST classNameTabs = templates.getInstanceOf("sequence")
                .add("e", templates.getInstanceOf("word")
                        .add("val", "DEMO_CLASS_NAME ###"));

        ST classObjTabs = templates.getInstanceOf("sequence")
                .add("e", helper.getWordConst("DEMO_CLASS_OBJ ###"));

        ST classDispatchTabs = templates.getInstanceOf("classDisptachTab")
                .add("name", "DEMO_CLASS")
                .add("methods", helper.getWordConst("DEMO_CLASS.METHOD_1"))
                .add("methods", helper.getWordConst("DEMO_CLASS.METHOD_2"));

        // TODO here
        // for (var c : program.getClasses())
        // mainSection.add("e", c.accept(this));

        // assembly-ing it all together. HA! get it?
        var programST = templates.getInstanceOf("program");
        programST.add("strConstants", strConstants);
        programST.add("intConstants", intConstants);
        programST.add("classNameTabs", classNameTabs);
        programST.add("classObjTabs", classObjTabs);
        programST.add("classDispatchTabs", classDispatchTabs);

        return programST;
    }

    @Override
    public ST visit(ASTRelational relational) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public ST visit(ASTString string) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Not yet implemented");
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
