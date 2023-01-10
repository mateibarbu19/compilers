package cool.visitor;

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

public class ASTResolution implements ASTVisitor<TypeSymbol> {

    @Override
    public TypeSymbol visit(ASTAlternative alternative) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTArithmetical arithmetical) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTAssignment assignment) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTAttribute attribute) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeSymbol visit(ASTBlock block) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTBool bool) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTCase casee) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTClassDefine classDefine) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeSymbol visit(ASTExpression expression) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTFeature feature) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTFormal formal) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeSymbol visit(ASTIf iff) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTInt intt) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTIsvoid isvoid) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTLet let) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTMethod method) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeSymbol visit(ASTMethodCall methodCall) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeSymbol visit(ASTMethodId methodId) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNegative negative) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNew neww) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNode node) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTNot not) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTObjectId objectId) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTProgram program) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public TypeSymbol visit(ASTRelational relational) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTString string) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTTypeId typeId) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTVariable variable) {
        return null;
    }

    @Override
    public TypeSymbol visit(ASTWhile whilee) {
        return null;
    }
};