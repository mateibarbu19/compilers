package cool.visitor;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

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
import cool.scopes.Scope;
import cool.symbols.IdSymbol;
import cool.symbols.MethodSymbol;
import cool.symbols.Symbol;
import cool.symbols.SymbolTable;
import cool.symbols.TypeSymbol;

public class ASTResolution implements ASTVisitor<Optional<TypeSymbol>> {
    // can't part from it as that would mean saving the scope of each expression
    Scope currentScope;

    private TypeSymbol getActualType(String typeName, Scope scope) {
        if (!typeName.equals("SELF_TYPE")) {
            return (TypeSymbol) SymbolTable.globals.lookup(typeName);
        }

        Scope currentScope = scope;
        while (!(currentScope instanceof TypeSymbol)) {
            currentScope = currentScope.getParent();
        }

        return (TypeSymbol) currentScope;
    }

    private TypeSymbol getLCA(TypeSymbol ts1, TypeSymbol ts2) {
        var ancestors = new HashSet<TypeSymbol>();

        while (ts1 != null) {
            ancestors.add(ts1);
            ts1 = (TypeSymbol) ts1.getParent();
        }

        while (ts2 != null) {
            if (ancestors.contains(ts2)) {
                return ts2;
            }

            ts2 = (TypeSymbol) ts2.getParent();
        }

        return TypeSymbol.OBJECT;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTAlternative alternative) {
        if (alternative.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        ParserRuleContext ctx = alternative.getContext();
        Token type = alternative.getType().getToken();
        String typeName = type.getText();
        String name = alternative.getName().getToken().getText();

        TypeSymbol caseType = getActualType(typeName, currentScope);
        if (caseType == null) {
            SymbolTable.error(ctx, type,
                    "Case variable " + name + " has undefined type " + typeName);
            return null;
        }

        alternative.getName().getSymbol().setType(caseType);

        currentScope = alternative.getName().getSymbol().getScope();
        var ret = alternative.getBody().accept(this);
        currentScope = currentScope.getParent();

        return ret;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTArithmetical arithmetical) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTAssignment assignment) {
        if (assignment.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        ParserRuleContext ctx = assignment.getContext();
        Token id = assignment.getName().getToken();
        String name = id.getText();

        Optional<TypeSymbol> type = assignment.getName().accept(this);

        if (type.isEmpty()) {
            return Optional.empty();
        }

        TypeSymbol idType = getActualType(type.get().getName(), currentScope);

        Optional<TypeSymbol> retType = Optional.ofNullable(assignment.getExpression())
                .flatMap(e -> Optional.ofNullable(e.accept(this)).orElse(Optional.empty()));

        if (idType == null || retType.isEmpty()) {
            return Optional.empty();
        }

        TypeSymbol exprType = getActualType(retType.get().getName(), currentScope);
        if (!exprType.inherits(idType)) {
            SymbolTable.error(ctx, assignment.getExpression().getContext().start,
                    "Type " + retType.get().getName() + " of assigned expression is incompatible with declared type "
                            + type.get().getName() + " of identifier " + name);
        }

        return retType;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTAttribute attribute) {
        if (attribute.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        ParserRuleContext ctx = attribute.getContext();
        IdSymbol symbol = attribute.getName().getSymbol();
        TypeSymbol classScope = (TypeSymbol) symbol.getScope();
        TypeSymbol type = getActualType(symbol.getType().getName(), classScope);

        currentScope = classScope;

        Optional<TypeSymbol> init = Optional.ofNullable(attribute.getInitialization())
                .flatMap(e -> Optional.ofNullable(e.accept(this)).orElse(Optional.empty()));

        if (init.isPresent()) {
            TypeSymbol initType = getActualType(init.get().getName(), classScope);

            if (!initType.inherits(type)) {
                SymbolTable.error(ctx, attribute.getInitialization().getContext().start,
                        "Type " + initType.getName() + " of initialization expression of attribute " + symbol
                                + " is incompatible with declared type " + type.getName());
                attribute.setError(ASTError.SemnaticError);
                return Optional.empty();
            }
        }

        return Optional.of(type);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTBlock block) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTBool bool) {
        return Optional.of(TypeSymbol.BOOL);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTCase casee) {
        return casee
                .getAlternatives()
                .stream()
                .map(a -> a.accept(this))
                .filter(r -> r != null && r.isPresent())
                .map(Optional::get)
                .reduce(this::getLCA);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTClassDefine classDefine) {
        if (classDefine.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        classDefine.getFeatures().forEach(f -> f.accept(this));

        return Optional.empty();
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
        return Optional.empty();
    }

    @Override
    public Optional<TypeSymbol> visit(ASTIf iff) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTInt intt) {
        return Optional.of(TypeSymbol.INT);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTIsvoid isvoid) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTLet let) {
        currentScope = let.getSymbol();

        for (var variable : let.getDeclarations()) {
            variable.accept(this);

            if (variable.getError() != ASTError.SemnaticError) {
                currentScope.add(variable.getName().getSymbol());
            }
        }

        var ret = let.getBody().accept(this);

        currentScope = currentScope.getParent();

        return ret;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTMethod method) {
        if (method.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        ParserRuleContext ctx = method.getContext();
        String name = method.getName().getToken().getText();
        MethodSymbol symbol = method.getName().getSymbol();
        TypeSymbol classScope = (TypeSymbol) symbol.getParent();
        var returnType = symbol.getReturnType();
        Symbol lookup = ((TypeSymbol) classScope.getParent()).lookup(name);
        if (lookup instanceof MethodSymbol) {
            MethodSymbol overriddenMethod = (MethodSymbol) lookup;
            TypeSymbol overriddenType = overriddenMethod.getReturnType();

            if (returnType != overriddenType) {
                SymbolTable.error(ctx, method.getType().getToken(),
                        "Class " + classScope.getName() + " overrides method " + name + " but changes return type from "
                                + overriddenType + " to " + returnType);
                method.setError(ASTError.SemnaticError);
                return Optional.empty();
            }

            List<IdSymbol> parameters = symbol.getParameters();
            List<IdSymbol> overridenParameters = overriddenMethod.getParameters();
            if (parameters.size() != overridenParameters.size()) {
                SymbolTable.error(ctx, method.getName().getToken(), "Class " + classScope.getName()
                        + " overrides method " + name + " with different number of formal parameters");
                method.setError(ASTError.SemnaticError);
                return null;
            }

            for (int i = 0; i != parameters.size(); i++) {
                var paramType = parameters.get(i).getType();
                var overriddenParamType = overridenParameters.get(i).getType();

                // cannot check otherwise for semantic erros
                if (paramType == null || overriddenParamType == null) {
                    continue;
                }

                if (paramType != overriddenParamType) {
                    SymbolTable.error(ctx, method.getParameters().get(i).getType().getToken(),
                            "Class " + classScope.getName() + " overrides method " + name
                                    + " but changes type of formal parameter " + parameters.get(i).getName() + " from "
                                    + overriddenParamType.getName() + " to " + paramType.getName());
                    method.setError(ASTError.SemnaticError);
                    return Optional.empty();
                }
            }
        }

        currentScope = symbol;
        var ret = method.getBody().accept(this);
        if (ret != null && ret.isPresent()) {
            TypeSymbol bodyType = ret.get();
            if (!bodyType.getName().equals(returnType.getName())) {
                bodyType = getActualType(bodyType.getName(), symbol);
                if (!bodyType.inherits(returnType)) {
                    SymbolTable.error(ctx, method.getBody().getContext().start,
                            "Type " + bodyType + " of the body of method " + name
                                    + " is incompatible with declared return type " + returnType);
                }
            }
        }

        return Optional.empty();
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
        ParserRuleContext ctx = neww.getContext();
        Token type = neww.getType().getToken();
        String typeName = neww.getType().getToken().getText();
        TypeSymbol classType = getActualType(typeName, currentScope);

        if (classType == null) {
            SymbolTable.error(ctx, type, "new is used with undefined type " + typeName);
            neww.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        return Optional.of((TypeSymbol) SymbolTable.globals.lookup(typeName));
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
        ParserRuleContext ctx = objectId.getContext();
        Token id = objectId.getToken();
        String name = id.getText();

        var ret = (IdSymbol) currentScope.lookup(name);
        if (ret == null) {
            SymbolTable.error(ctx, id, "Undefined identifier " + name);
            objectId.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        return Optional.of(ret.getType());
    }

    @Override
    public Optional<TypeSymbol> visit(ASTProgram program) {
        program.getClasses().forEach(this::visit);

        return Optional.empty();
    }

    @Override
    public Optional<TypeSymbol> visit(ASTRelational relational) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTString string) {
        return Optional.of(TypeSymbol.STRING);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTTypeId typeId) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTVariable variable) {
        if (variable.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        ParserRuleContext ctx = variable.getContext();
        Token id = variable.getName().getToken();
        String name = id.getText();
        Token type = variable.getType().getToken();
        String typeName = type.getText();

        TypeSymbol actualType = getActualType(typeName, currentScope);
        if (actualType == null) {
            SymbolTable.error(ctx, type,
                    "Let variable " + name + " has undefined type " + typeName);
            variable.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        Optional<TypeSymbol> init = Optional.ofNullable(variable.getInitialization())
                .flatMap(e -> Optional.ofNullable(e.accept(this)).orElse(Optional.empty()));

        if (init.isPresent()) {
            TypeSymbol initType = getActualType(init.get().getName(), currentScope);

            if (!initType.inherits(actualType)) {
                SymbolTable.error(ctx, variable.getInitialization().getContext().start,
                        "Type " + initType.getName() + " of initialization expression of identifier " + name
                                + " is incompatible with declared type " + typeName);
            }
        }

        variable.getName().getSymbol().setType((TypeSymbol) SymbolTable.globals.lookup(typeName));

        return Optional.of(variable.getName().getSymbol().getType());
    }

    @Override
    public Optional<TypeSymbol> visit(ASTWhile whilee) {
        ParserRuleContext ctx = whilee.getContext();
        Optional<TypeSymbol> condType = whilee.getCondition().accept(this);
        if (condType == null || condType.isEmpty() || condType.get() != TypeSymbol.BOOL) {
            SymbolTable.error(ctx, whilee.getCondition().getContext().start,
                    "While condition has type " + condType + " instead of Bool");
        }

        return Optional.of(TypeSymbol.OBJECT);
    }
};