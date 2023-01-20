package cool.visitor;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import cool.AST.ASTAlternative;
import cool.AST.ASTArithmetical;
import cool.AST.ASTAssignment;
import cool.AST.ASTAttribute;
import cool.AST.ASTBlock;
import cool.AST.ASTBool;
import cool.AST.ASTCase;
import cool.AST.ASTClassDefine;
import cool.AST.ASTError;
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
import cool.symbols.MethodSymbol;
import cool.symbols.SymbolTable;
import cool.symbols.TypeSymbol;

public class ASTHierarchy implements ASTVisitor<Optional<TypeSymbol>> {
    @Override
    public Optional<TypeSymbol> visit(ASTAlternative alternative) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTArithmetical arithmetical) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTAssignment assignment) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTAttribute attribute) {
        ParserRuleContext ctx = attribute.getContext();
        Token tokenName = attribute.getName().getToken();
        String name = tokenName.getText();
        Token tokenType = attribute.getType().getToken();
        String type = attribute.getType().getToken().getText();

        if (attribute.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        TypeSymbol classScope = (TypeSymbol) attribute.getName().getSymbol().getScope();

        if (classScope.getParent().lookup(name) != null) {
            SymbolTable.error(ctx, tokenName,
                    "Class " + classScope.getName() + " redefines inherited attribute " + name);
            attribute.setError(ASTError.SemnaticError);

            return Optional.empty();
        }

        TypeSymbol typeSymbol = (TypeSymbol) SymbolTable.globals.lookup(type);
        if (typeSymbol == null) {
            SymbolTable.error(ctx, tokenType,
                    "Class " + classScope.getName() + " has attribute " + name + " with undefined type " + type);
            attribute.setError(ASTError.SemnaticError);

            return Optional.empty();
        }
        attribute.getName().getSymbol().setType(typeSymbol);

        return Optional.of(typeSymbol);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTBlock block) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTBool bool) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTCase casee) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTClassDefine classDefine) {
        ParserRuleContext ctx = classDefine.getContext();
        Token name = classDefine.getName().getToken();

        // failed at definition
        if (classDefine.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        TypeSymbol definedType = classDefine.getType();
        String parent = definedType.getParentName();

        if (SymbolTable.globals.lookup(parent) == null) {
            SymbolTable.error(ctx, classDefine.getParent().getToken(),
                    "Class " + name.getText() + " has undefined parent " + parent);
        }

        TypeSymbol parentType = (TypeSymbol) SymbolTable.globals.lookup(parent);
        if (parentType != null) {
            definedType.setParent(parentType);
        }

        Set<TypeSymbol> seen = new HashSet<>() {
            {
                add(definedType);
            }
        };

        while (parentType != null && !seen.contains(parentType)) {
            seen.add(parentType);
            parentType = (TypeSymbol) SymbolTable.globals.lookup(parentType.getParentName());
        }

        if (parentType == definedType) {
            SymbolTable.error(ctx, name, "Inheritance cycle for class " + name.getText());
            classDefine.setError(ASTError.SemnaticError);
        }

        classDefine.getFeatures().forEach(f -> f.accept(this));

        return Optional.of(definedType);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTExpression expression) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTFeature feature) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTFormal formal) {
        ParserRuleContext ctx = formal.getContext();
        Token tokenName = formal.getName().getToken();
        String name = tokenName.getText();
        Token tokenType = formal.getType().getToken();
        String type = formal.getType().getToken().getText();

        if (formal.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        MethodSymbol methodScope = (MethodSymbol) formal.getName().getSymbol().getScope();
        TypeSymbol classScope = (TypeSymbol) methodScope.getParent();

        TypeSymbol typeSymbol = (TypeSymbol) SymbolTable.globals.lookup(type);
        if (typeSymbol == null) {
            SymbolTable.error(ctx, tokenType,
                    "Method " + methodScope.getName() + " of class " + classScope.getName() + " has formal parameter "
                            + name + " with undefined type " + type);
            formal.setError(ASTError.SemnaticError);

            return Optional.empty();
        }

        formal.getName().getSymbol().setType(typeSymbol);

        return Optional.of(typeSymbol);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTIf iff) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTInt intt) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTIsvoid isvoid) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTLet let) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTMethod method) {
        ParserRuleContext ctx = method.getContext();
        Token tokenName = method.getName().getToken();
        String name = tokenName.getText();
        Token tokenType = method.getType().getToken();
        String type = method.getType().getToken().getText();

        if (method.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        TypeSymbol classScope = (TypeSymbol) method.getName().getSymbol().getParent();

        TypeSymbol typeSymbol = (TypeSymbol) SymbolTable.globals.lookup(type);
        if (typeSymbol == null) {
            SymbolTable.error(ctx, tokenType,
                    "Class " + classScope.getName() + " has method " + name + " with undefined return type " + type);
            method.setError(ASTError.SemnaticError);

            return Optional.empty();
        }

        method.getName().getSymbol().setReturnType(typeSymbol);
        method.getParameters().forEach(p -> p.accept(this));

        return Optional.of(typeSymbol);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTMethodCall methodCall) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTMethodId methodId) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTNegative negative) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTNew neww) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTNode node) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTNot not) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTObjectId objectId) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTProgram program) {
        program.getClasses().forEach(this::visit);

        return Optional.of(TypeSymbol.OBJECT);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTRelational relational) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTString string) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTTypeId typeId) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTVariable variable) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTWhile whilee) {
        return null;
    }
};