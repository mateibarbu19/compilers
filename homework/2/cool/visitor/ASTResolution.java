package cool.visitor;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        if (typeName == null) {
            return null;
        }

        if (!typeName.equals("SELF_TYPE")) {
            return (TypeSymbol) SymbolTable.globals.lookup(typeName);
        }

        Scope currentScope = scope;
        while (!(currentScope instanceof TypeSymbol)) {
            currentScope = currentScope.getParent();
        }

        return (TypeSymbol) currentScope;
    }

    private TypeSymbol getLUB(TypeSymbol a, TypeSymbol b) {
        var ancestors = new HashSet<TypeSymbol>();

        while (a != null) {
            ancestors.add(a);
            a = (TypeSymbol) a.getParent();
        }

        while (b != null) {
            if (ancestors.contains(b)) {
                return b;
            }

            b = (TypeSymbol) b.getParent();
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
            alternative.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        alternative.getName().getSymbol().setType(caseType);

        currentScope = alternative.getName().getSymbol().getScope();
        var ret = alternative.getBody().accept(this);
        currentScope = currentScope.getParent();

        return ret;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTArithmetical arithmetical) {
        ParserRuleContext ctx = arithmetical.getContext();
        Token operator = arithmetical.getOperator();
        Optional<TypeSymbol> lhsType = Optional.ofNullable(arithmetical.getLhs().accept(this)).orElse(Optional.empty());
        Optional<TypeSymbol> rhsType = Optional.ofNullable(arithmetical.getRhs().accept(this)).orElse(Optional.empty());

        if (lhsType.isPresent() && lhsType.get() != TypeSymbol.INT) {
            SymbolTable.error(ctx, arithmetical.getLhs().getContext().start,
                    "Operand of " + operator.getText() + " has type " + lhsType.get().getName()
                            + " instead of Int");
        }

        if (rhsType.isPresent() && rhsType.get() != TypeSymbol.INT) {
            SymbolTable.error(ctx, arithmetical.getRhs().getContext().start,
                    "Operand of " + operator.getText() + " has type " + rhsType.get().getName()
                            + " instead of Int");
        }

        return Optional.of(TypeSymbol.INT);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTAssignment assignment) {
        if (assignment.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        ParserRuleContext ctx = assignment.getContext();
        Token id = assignment.getName().getToken();
        String name = id.getText();

        assignment.getName().accept(this);
        Optional<TypeSymbol> type = Optional.ofNullable(assignment.getName().accept(this)).orElse(Optional.empty());

        if (type.isEmpty()) {
            return Optional.empty();
        }

        Optional<TypeSymbol> retType = Optional.ofNullable(assignment.getExpression())
                .flatMap(e -> Optional.ofNullable(e.accept(this)).orElse(Optional.empty()));

        if (retType.isPresent() && !retType.get().inherits(type.get())) {
            SymbolTable.error(ctx, assignment.getExpression().getContext().start,
                    "Type " + retType.get().getName() + " of assigned expression is incompatible with declared type "
                            + type.get().getName() + " of identifier " + name);
            assignment.setError(ASTError.SemnaticError);
        }

        if (retType.isPresent()) {
            return retType;
        }
        return type;
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
            TypeSymbol initType = getActualType(init.get().getName(), currentScope);

            if (!initType.inherits(type)) {
                SymbolTable.error(ctx, attribute.getInitialization().getContext().start,
                        "Type " + init.get().getName() + " of initialization expression of attribute " + symbol
                                + " is incompatible with declared type " + type.getName());
            }
        }

        return Optional.of(type);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTBlock block) {
        return block
                .getStatements()
                .stream()
                .map(e -> e.accept(this))
                .filter(r -> r != null && r.isPresent())
                .map(Optional::get)
                .reduce((first, second) -> second);
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
                .reduce(this::getLUB);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTClassDefine classDefine) {
        if (classDefine.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        var type = classDefine.getType();
        List<String> classAttributes = new LinkedList<>();
        List<String> classMethods = new LinkedList<>();

        while (type != null) {
            final var typeName = type.getName();

            final var tmp = classMethods.stream().map(n -> n.split("\\.", 2)[1]).collect(Collectors.toList());
            var typeMethods = type.getMethodsNames()
                    .stream()
                    .filter(methodName -> !tmp.contains(methodName))
                    .map(methodName -> typeName + "." + methodName)
                    .collect(Collectors.toList());

            var typeAttributes = type.getAttributesNames()
                    .stream()
                    .filter(attributeName -> !attributeName.equals("self"))
                    .collect(Collectors.toList());

            typeAttributes.addAll(classAttributes);
            typeMethods.addAll(classMethods);

            classMethods = typeMethods;
            classAttributes = typeAttributes;

            type = (TypeSymbol) type.getParent();
        }

        var name = classDefine.getName().getToken().getText();
        SymbolTable.putInDispatchTables(name, classMethods);
        SymbolTable.putInFieldTables(name, classAttributes);

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
        ParserRuleContext ctx = iff.getContext();
        Optional<TypeSymbol> conditionType = Optional.ofNullable(iff.getCondition().accept(this))
                .orElse(Optional.empty());

        if (conditionType.isPresent() && conditionType.get() != TypeSymbol.BOOL) {
            SymbolTable.error(ctx, iff.getCondition().getContext().start,
                    "If condition has type " + conditionType.get().getName() + " instead of Bool");
        }

        return Optional.of(getLUB(
                iff.getConsequent().accept(this).orElse(null),
                iff.getAlternative().accept(this).orElse(null)));
    }

    @Override
    public Optional<TypeSymbol> visit(ASTInt intt) {
        return Optional.of(TypeSymbol.INT);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTIsvoid isvoid) {
        if (isvoid.getError() != ASTError.SemnaticError) {
            isvoid.getExpression().accept(this);
        }

        return Optional.of(TypeSymbol.BOOL);
    }

    @Override
    public Optional<TypeSymbol> visit(ASTLet let) {
        currentScope = let.getSymbol();

        for (var variable : let.getDeclarations()) {
            variable.accept(this);

            // cannot check for semantic erros
            var symbol = variable.getName().getSymbol();
            if (symbol != null && symbol.getType() != null) {
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
        TypeSymbol returnType = symbol.getReturnType();
        Symbol lookup = ((TypeSymbol) classScope.getParent()).lookup(name);

        // if it overrides a method
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
                return Optional.empty();
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

            if (bodyType != returnType) {
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
        if (methodCall.getError() == ASTError.SemnaticError) {
            return Optional.empty();
        }

        ParserRuleContext ctx = methodCall.getContext();
        Optional<ASTExpression> caller = Optional.ofNullable(methodCall.getCaller());
        TypeSymbol callerType = getActualType(
                caller.flatMap(c -> c.accept(this))
                        .orElse(TypeSymbol.SELF_TYPE).getName(),
                currentScope);

        Optional<String> actualCaller = Optional.ofNullable(methodCall.getActualCaller())
                .map(aC -> aC.getToken().getText());
        if (actualCaller.isPresent()) {
            var actualCallerType = (TypeSymbol) SymbolTable.globals.lookup(actualCaller.get());
            if (actualCallerType == null) {
                SymbolTable.error(ctx, methodCall.getActualCaller().getToken(),
                        "Type " + actualCaller.get() + " of static dispatch is undefined");
                methodCall.setError(ASTError.SemnaticError);
                return Optional.empty();
            }

            if (!callerType.inherits(actualCallerType)) {
                SymbolTable.error(ctx, methodCall.getActualCaller().getToken(),
                        "Type " + actualCallerType + " of static dispatch is not a superclass of type " + callerType);
                methodCall.setError(ASTError.SemnaticError);
                return Optional.empty();
            }

            callerType = actualCallerType;
        }

        methodCall.setRuntimeCallerType(callerType);

        String method = methodCall.getMethod().getToken().getText();
        Symbol symbol = callerType.lookup(method);
        if (!(symbol instanceof MethodSymbol)) {
            SymbolTable.error(ctx, methodCall.getMethod().getToken(),
                    "Undefined method " + method + " in class " + callerType.getName());
            methodCall.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        var argumentsTypes = methodCall
                .getArguments()
                .stream()
                .map(a -> a.accept(this))
                .filter(r -> r != null && r.isPresent())
                .map(Optional::get)
                .collect(Collectors.toList());

        MethodSymbol methodSymbol = (MethodSymbol) symbol;
        var parameters = methodSymbol.getParameters();
        if (argumentsTypes.size() != parameters.size()) {
            SymbolTable.error(ctx, methodCall.getMethod().getToken(),
                    "Method " + method + " of class " + callerType.getName()
                            + " is applied to wrong number of arguments");
            methodCall.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        for (int i = 0; i != argumentsTypes.size(); i++) {
            if (!argumentsTypes.get(i).inherits(parameters.get(i).getType())) {
                SymbolTable.error(ctx, methodCall.getArguments().get(i).getContext().start,
                        "In call to method " + method + " of class " + callerType.getName() + ", actual type "
                                + argumentsTypes.get(i).getName() + " of formal parameter "
                                + parameters.get(i).getName()
                                + " is incompatible with declared type " + parameters.get(i).getType().getName());
            }
        }

        if (methodSymbol.getReturnType() == TypeSymbol.SELF_TYPE) {
            return caller.map(c -> c.accept(this).orElse(getActualType("SELF_TYPE", currentScope)));
        }

        return Optional.of(methodSymbol.getReturnType());
    }

    @Override
    public Optional<TypeSymbol> visit(ASTMethodId methodId) {
        return null;
    }

    @Override
    public Optional<TypeSymbol> visit(ASTNegative negative) {
        ParserRuleContext ctx = negative.getContext();
        Optional<TypeSymbol> exprType = Optional.ofNullable(negative.getExpression().accept(this))
                .orElse(Optional.empty());

        if (exprType.isEmpty()) {
            negative.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        if (exprType.get() != TypeSymbol.INT) {
            SymbolTable.error(ctx, negative.getExpression().getContext().start,
                    "Operand of ~ has type " + exprType.get() + " instead of Int");
            negative.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        return exprType;
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
        ParserRuleContext ctx = not.getContext();
        Optional<TypeSymbol> exprType = Optional.ofNullable(not.getExpression().accept(this)).orElse(Optional.empty());

        if (exprType.isEmpty()) {
            not.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        if (exprType.get() != TypeSymbol.BOOL) {
            SymbolTable.error(ctx, not.getExpression().getContext().start,
                    "Operand of not has type " + exprType.get() + " instead of Bool");
            not.setError(ASTError.SemnaticError);
            return Optional.empty();
        }

        return exprType;
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

        // TODO cand are matei timp si chef sa faca asta mai frumos
        if (objectId.getSymbol() == null) {
            ret.setReferencedScope(currentScope);
            objectId.setSymbol(ret);
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
        ParserRuleContext ctx = relational.getContext();
        Token operator = relational.getOperator();
        Optional<TypeSymbol> lhsType = Optional.ofNullable(relational.getLhs().accept(this)).orElse(Optional.empty());
        Optional<TypeSymbol> rhsType = Optional.ofNullable(relational.getRhs().accept(this)).orElse(Optional.empty());

        if (!operator.getText().equals("=")) {
            if (lhsType.isPresent() && lhsType.get() != TypeSymbol.INT) {
                SymbolTable.error(ctx, relational.getLhs().getContext().start,
                        "Operand of " + operator.getText() + " has type " + lhsType.get().getName()
                                + " instead of Int");
            }

            if (rhsType.isPresent() && rhsType.get() != TypeSymbol.INT) {
                SymbolTable.error(ctx, relational.getRhs().getContext().start,
                        "Operand of " + operator.getText() + " has type " + rhsType.get().getName()
                                + " instead of Int");
            }
        } else {
            var lhsComparesWithItself = lhsType.map(t -> t.comparesWithItself()).orElse(false);
            var rhsComparesWithItself = lhsType.map(t -> t.comparesWithItself()).orElse(false);

            if (lhsComparesWithItself && rhsComparesWithItself && lhsType.get() != rhsType.get()) {
                SymbolTable.error(ctx, operator,
                        "Cannot compare " + lhsType.get().getName() + " with " +
                                rhsType.get().getName());
            }
        }

        return Optional.of(TypeSymbol.BOOL);
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

        variable.getName().getSymbol().setType((TypeSymbol) SymbolTable.globals.lookup(typeName));

        Optional<TypeSymbol> init = Optional.ofNullable(variable.getInitialization())
                .flatMap(e -> Optional.ofNullable(e.accept(this)).orElse(Optional.empty()));

        if (init.isPresent()) {
            TypeSymbol initType = init.get();

            if (!initType.inherits(actualType)) {
                SymbolTable.error(ctx, variable.getInitialization().getContext().start,
                        "Type " + initType.getName() + " of initialization expression of identifier " + name
                                + " is incompatible with declared type " + typeName);
                variable.setError(ASTError.SemnaticError);
            }
        }

        return Optional.of(variable.getName().getSymbol().getType());
    }

    @Override
    public Optional<TypeSymbol> visit(ASTWhile whilee) {
        ParserRuleContext ctx = whilee.getContext();
        Optional<TypeSymbol> condType = Optional.ofNullable(whilee.getCondition().accept(this))
                .orElse(Optional.empty());

        whilee.getBody().accept(this);

        if (condType == null || condType.isEmpty() || condType.get() != TypeSymbol.BOOL) {
            SymbolTable.error(ctx, whilee.getCondition().getContext().start,
                    "While condition has type " + condType.get() + " instead of Bool");
        }

        return Optional.of(TypeSymbol.OBJECT);
    }
};