package cool.visitor;

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
import cool.scopes.CaseScope;
import cool.scopes.Scope;
import cool.symbols.IdSymbol;
import cool.symbols.LetSymbol;
import cool.symbols.MethodSymbol;
import cool.symbols.SymbolTable;
import cool.symbols.TypeSymbol;

public class ASTDefinition implements ASTVisitor<Void> {
    Scope currentScope;
    Set<String> prohibitedParents = Set.of(TypeSymbol.INT.getName(), TypeSymbol.BOOL.getName(),
            TypeSymbol.STRING.getName(),
            "SELF_TYPE");

    @Override
    public Void visit(ASTAlternative alternative) {
        ParserRuleContext ctx = alternative.getContext();
        Token id = alternative.getName().getToken();
        String name = id.getText();
        Token type = alternative.getType().getToken();
        String typeName = type.getText();

        if (name.equals("self")) {
            SymbolTable.error(ctx, id, "Case variable has illegal name " + name);
            alternative.setError(ASTError.SemnaticError);
            return null;
        }

        if (typeName.equals("SELF_TYPE")) {
            SymbolTable.error(ctx, type, "Case variable " + name + " has illegal type " + typeName);
            alternative.setError(ASTError.SemnaticError);
            return null;
        }

        CaseScope caseScope = new CaseScope(currentScope);
        IdSymbol symbol = new IdSymbol(name, caseScope);

        alternative.getName().setSymbol(symbol);
        caseScope.add(symbol);

        currentScope = caseScope;
        alternative.getBody().accept(this);
        currentScope = caseScope.getParent();

        return null;
    }

    @Override
    public Void visit(ASTArithmetical arithmetical) {
        arithmetical.getLhs().accept(this);
        arithmetical.getRhs().accept(this);

        return null;
    }

    @Override
    public Void visit(ASTAssignment assignment) {
        ParserRuleContext ctx = assignment.getContext();
        Token id = assignment.getName().getToken();
        String name = id.getText();

        if (name.equals("self")) {
            SymbolTable.error(ctx, id, "Cannot assign to " + name);
            assignment.setError(ASTError.SemnaticError);
            return null;
        }

        assignment.getExpression().accept(this);

        return null;
    }

    @Override
    public Void visit(ASTAttribute attribute) {
        ParserRuleContext ctx = attribute.getContext();
        Token token = attribute.getName().getToken();
        String name = token.getText();

        if (name.equals("self")) {
            SymbolTable.error(ctx, token,
                    "Class " + ((TypeSymbol) currentScope).getName() + " has attribute with illegal name " + name);
            attribute.setError(ASTError.SemnaticError);
            return null;
        }

        IdSymbol symbol = new IdSymbol(name, currentScope);
        if (!currentScope.add(symbol)) {
            SymbolTable.error(ctx, token,
                    "Class " + ((TypeSymbol) currentScope).getName() + " redefines attribute " + name);
            attribute.setError(ASTError.SemnaticError);
            return null;
        }

        attribute.getName().setSymbol(symbol);

        Optional.ofNullable(attribute.getInitialization()).ifPresent(e -> e.accept(this));

        return null;
    }

    @Override
    public Void visit(ASTBlock block) {
        block.getStatements().forEach(s -> s.accept(this));

        return null;
    }

    @Override
    public Void visit(ASTBool bool) {
        return null;
    }

    @Override
    public Void visit(ASTCase casee) {
        casee.getAlternatives().forEach(a -> a.accept(this));

        return null;
    }

    @Override
    public Void visit(ASTClassDefine classDefine) {
        ParserRuleContext ctx = classDefine.getContext();
        Token token = classDefine.getName().getToken();
        String name = token.getText();

        if (name.equals("SELF_TYPE")) {
            SymbolTable.error(ctx, token, "Class has illegal name " + name);
            classDefine.setError(ASTError.SemnaticError);
            return null;
        }

        Optional<Token> parent = Optional.ofNullable(classDefine.getParent()).map(p -> p.getToken());
        String parentName = parent.map(p -> p.getText()).orElse("Object");

        // by all means ignore the parent name for now as it may not be defined
        TypeSymbol type = new TypeSymbol(name, parentName);
        if (!SymbolTable.globals.add(type)) {
            SymbolTable.error(ctx, token, "Class " + name + " is redefined");
            classDefine.setError(ASTError.SemnaticError);
            return null;
        }

        if (prohibitedParents.contains(parentName)) {
            SymbolTable.error(ctx, classDefine.getParent().getToken(),
                    "Class " + name + " has illegal parent " + parentName);
            classDefine.setError(ASTError.SemnaticError);
            return null;
        }

        classDefine.setType(type);
        currentScope = type;

        classDefine.getFeatures().forEach(f -> f.accept(this));

        return null;
    }

    @Override
    public Void visit(ASTExpression expression) {
        return null;
    }

    @Override
    public Void visit(ASTFeature feature) {
        return null;
    }

    @Override
    public Void visit(ASTFormal formal) {
        ParserRuleContext ctx = formal.getContext();
        Token id = formal.getName().getToken();
        String name = id.getText();
        Token type = formal.getType().getToken();

        MethodSymbol method = (MethodSymbol) currentScope;
        TypeSymbol classDefined = (TypeSymbol) method.getParent();

        if (name.equals("self")) {
            SymbolTable.error(ctx, id, "Method " + method.getName() + " of class " + classDefined.getName()
                    + " has formal parameter with illegal name " + name);
            formal.setError(ASTError.SemnaticError);
            return null;
        }

        String typeName = type.getText();
        if (typeName.equals("SELF_TYPE")) {
            SymbolTable.error(ctx, type, "Method " + method.getName() + " of class " + classDefined.getName()
                    + " has formal parameter " + name + " with illegal type " + typeName);
            formal.setError(ASTError.SemnaticError);
            return null;
        }

        IdSymbol symbol = new IdSymbol(name, currentScope);
        if (!currentScope.add(symbol)) {
            SymbolTable.error(ctx, id, "Method " + method.getName() + " of class " + classDefined.getName()
                    + " redefines formal parameter " + name);
            formal.setError(ASTError.SemnaticError);
            return null;
        }

        formal.getName().setSymbol(symbol);

        return null;
    }

    @Override
    public Void visit(ASTIf iff) {
        iff.getConsequent().accept(this);
        iff.getAlternative().accept(this);

        return null;
    }

    @Override
    public Void visit(ASTInt intt) {
        return null;
    }

    @Override
    public Void visit(ASTIsvoid isvoid) {
        isvoid.getExpression().accept(this);

        return null;
    }

    @Override
    public Void visit(ASTLet let) {
        LetSymbol symbol = new LetSymbol(currentScope);
        let.setSymbol(symbol);

        currentScope = symbol;
        let.getDeclarations().forEach(d -> d.accept(this));
        let.getBody().accept(this);
        currentScope = currentScope.getParent();

        return null;
    }

    @Override
    public Void visit(ASTMethod method) {
        ParserRuleContext ctx = method.getContext();
        Token id = method.getName().getToken();
        String name = id.getText();

        TypeSymbol parent = (TypeSymbol) currentScope;
        MethodSymbol symbol = new MethodSymbol(name, parent);

        if (!currentScope.add(symbol)) {
            SymbolTable.error(ctx, id,
                    "Class " + parent.getName() + " redefines method " + name);
            method.setError(ASTError.SemnaticError);
            return null;
        }

        method.getName().setSymbol(symbol);

        currentScope = symbol;
        method.getParameters().forEach(p -> p.accept(this));
        method.getBody().accept(this);
        currentScope = currentScope.getParent();

        return null;
    }

    @Override
    public Void visit(ASTMethodCall methodCall) {
        return null;
    }

    @Override
    public Void visit(ASTMethodId methodId) {
        return null;
    }

    @Override
    public Void visit(ASTNegative negative) {
        negative.getExpression().accept(this);

        return null;
    }

    @Override
    public Void visit(ASTNew neww) {
        return null;
    }

    @Override
    public Void visit(ASTNode node) {
        return null;
    }

    @Override
    public Void visit(ASTNot not) {
        not.getExpression().accept(this);

        return null;
    }

    @Override
    public Void visit(ASTObjectId objectId) {
        return null;
    }

    @Override
    public Void visit(ASTProgram program) {
        program.getClasses().forEach(this::visit);

        return null;
    }

    @Override
    public Void visit(ASTRelational relational) {
        relational.getLhs().accept(this);
        relational.getRhs().accept(this);

        return null;
    }

    @Override
    public Void visit(ASTString string) {
        return null;
    }

    @Override
    public Void visit(ASTTypeId typeId) {
        return null;
    }

    @Override
    public Void visit(ASTVariable variable) {
        ParserRuleContext ctx = variable.getContext();
        Token id = variable.getName().getToken();
        String name = id.getText();

        if (name.equals("self")) {
            SymbolTable.error(ctx, id, "Let variable has illegal name " + name);
            variable.setError(ASTError.SemnaticError);
            return null;
        }

        IdSymbol symbol = new IdSymbol(name, currentScope);
        variable.getName().setSymbol(symbol);

        Optional.ofNullable(variable.getInitialization()).ifPresent(e -> e.accept(this));

        return null;
    }

    @Override
    public Void visit(ASTWhile whilee) {
        whilee.getBody().accept(this);

        return null;
    }
}