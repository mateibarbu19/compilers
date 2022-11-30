import org.antlr.v4.runtime.Token;

public class ResolutionPassVisitor implements ASTVisitor<TypeSymbol> {
    @Override
    public TypeSymbol visit(Program prog) {
        for (var stmt : prog.stmts) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Id id) {
        // Verificăm dacă într-adevăr avem de-a face cu o variabilă
        // sau cu o funcție, al doilea caz constituind eroare.
        // Puteți folosi instanceof.
        var symbol = id.getScope().lookup(id.getToken().getText());

        if (symbol instanceof FunctionSymbol) {
            ASTVisitor.error(id.getToken(),
                    id.getToken().getText() + " is not a variable");
            return null;
        }

        // DONE 2: Întoarcem informația de tip salvată deja în simbol încă de la
        // definirea variabilei.
        return id.getSymbol().getType();
    }

    @Override
    public TypeSymbol visit(VarDef varDef) {
        if (varDef.initValue != null) {
            var varType = varDef.id.getSymbol().getType();
            var initType = varDef.initValue.accept(this);

            // DONE 5: Verificăm dacă expresia de inițializare are tipul potrivit
            // cu cel declarat pentru variabilă.
            if (varType == null || initType == null || varType != initType) {
                ASTVisitor.error(varDef.initValue.getToken(),
                        "Type of initilization expression does not match variable type");
                return null;
            }

            return varType;
        }
        return null;
    }

    @Override
    public TypeSymbol visit(FuncDef funcDef) {
        var returnType = funcDef.id.getSymbol().getType();
        var bodyType = funcDef.body.accept(this);

        // DONE 5: Verificăm dacă tipul de retur declarat este compatibil
        // cu cel al corpului.
        if (returnType == null || bodyType == null || returnType != bodyType) {
            ASTVisitor.error(funcDef.getToken(),
                    "Return type does not match body type");
            return null;
        }

        return returnType;
    }

    @Override
    public TypeSymbol visit(Call call) {
        // Verificăm dacă funcția există în scope. Nu am putut face
        // asta în prima trecere din cauza a forward references.
        //
        // De asemenea, verificăm că Id-ul pe care se face apelul de funcție
        // este, într-adevăr, o funcție și nu o variabilă.
        //
        // Hint: pentru a obține scope-ul, putem folosi call.id.getScope(),
        // setat la trecerea anterioară.
        var id = call.id;
        var symbol = id.getScope().lookup(id.getToken().getText());

        if (symbol == null) {
            ASTVisitor.error(id.getToken(),
                    id.getToken().getText() + " function undefined");
            return null;
        }

        if (!(symbol instanceof FunctionSymbol)) {
            ASTVisitor.error(id.getToken(),
                    id.getToken().getText() + " is not a function");
            return null;
        }

        var functionSymbol = (FunctionSymbol) symbol;
        id.setSymbol(functionSymbol);

        // DONE 6: Verificați dacă numărul parametrilor actuali coincide
        // cu cel al parametrilor formali, și că tipurile sunt compatibile.
        var formals = functionSymbol.getFormals();
        if (formals.size() != call.args.size()) {
            ASTVisitor.error(call.getToken(), call.id.getToken().getText() + " applied to wrong number of arguments");
            return null;
        } else {
            var index = 0;
            var formalIterator = formals.entrySet().iterator();
            var argsIterator = call.args.iterator();

            while (formalIterator.hasNext()) {
                var formal = formalIterator.next();
                var arg = argsIterator.next();
                index++;

                if (((IdSymbol) formal.getValue()).getType() != arg.accept(this)) {
                    ASTVisitor.error(arg.getToken(),
                            "Argument " + index +
                                    " of " + call.id.getToken().getText() +
                                    " has wrong type");
                    return null;
                }
            }
        }

        return functionSymbol.getType();
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        var idType = assign.id.accept(this);
        var exprType = assign.expr.accept(this);

        // DONE 5: Verificăm dacă expresia cu care se realizează atribuirea
        // are tipul potrivit cu cel declarat pentru variabilă.
        if (idType == null || exprType == null || idType != exprType) {
            ASTVisitor.error(assign.expr.getToken(),
                    "Assignment with incompatible types");
            return null;
        }

        return idType;
    }

    @Override
    public TypeSymbol visit(If iff) {
        TypeSymbol condType = iff.cond.accept(this);
        TypeSymbol thenType = iff.thenBranch.accept(this);
        TypeSymbol elseType = iff.elseBranch.accept(this);
        TypeSymbol res = thenType;

        // DONE 4: Verificați tipurile celor 3 componente, afișați eroare
        // dacă este cazul, și precizați tipul expresiei.
        if (condType == null || condType != TypeSymbol.BOOL) {
            ASTVisitor.error(iff.cond.getToken(), "Condition of if expression has type other than Bool");
            res = null;
        }

        if (thenType == null || elseType == null || thenType != elseType) {
            ASTVisitor.error(iff.getToken(), "Branches of if expression have incompatible types");
            res = null;
        }

        return thenType;
    }

    @Override
    public TypeSymbol visit(Type type) {
        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        return formal.id.getSymbol().getType();
    }

    // Operații aritmetice.
    @Override
    public TypeSymbol visit(UnaryMinus uMinus) {
        var exprType = uMinus.expr.accept(this);

        // DONE 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        if (exprType == TypeSymbol.BOOL) {
            ASTVisitor.error(uMinus.getToken(), "Unary minus applied to Bool");
            return null;
        }

        return exprType;
    }

    @Override
    public TypeSymbol visit(Div div) {
        // DONE 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.

        return checkBinaryNumericOpTypes(div.getToken(), div.left, div.right);
    }

    @Override
    public TypeSymbol visit(Mult mult) {
        // DONE 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.

        return checkBinaryNumericOpTypes(mult.getToken(), mult.left, mult.right);
    }

    @Override
    public TypeSymbol visit(Plus plus) {
        // DONE 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.

        return checkBinaryNumericOpTypes(plus.getToken(), plus.left, plus.right);
    }

    @Override
    public TypeSymbol visit(Minus minus) {
        // DONE 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.

        return checkBinaryNumericOpTypes(minus.getToken(), minus.left, minus.right);
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        // DONE 3: Verificăm tipurile operanzilor, afișăm eroare dacă e cazul,
        // și întoarcem tipul expresiei.
        // Puteți afla felul operației din relational.getToken().getType(),
        // pe care îl puteți compara cu CPLangLexer.EQUAL etc.
        var leftType = relational.left.accept(this);
        var rightType = relational.right.accept(this);

        if (leftType == rightType) {
            return TypeSymbol.BOOL;
        }

        ASTVisitor.error(relational.getToken(), "Operation with incompabile types");
        return null;
    }

    // Tipurile de bază
    @Override
    public TypeSymbol visit(Int intt) {
        // DONE 2: Întoarcem tipul corect.
        return TypeSymbol.INT;
    }

    @Override
    public TypeSymbol visit(Bool bool) {
        // DONE 2: Întoarcem tipul corect.
        return TypeSymbol.BOOL;
    }

    @Override
    public TypeSymbol visit(FloatNum floatNum) {
        // DONE 2: Întoarcem tipul corect.
        return TypeSymbol.FLOAT;
    }

    TypeSymbol checkBinaryNumericOpTypes(Token token, Expression e1, Expression e2) {
        var e1Type = e1.accept(this);
        var e2Type = e2.accept(this);

        if (e1Type == TypeSymbol.INT && (e2Type == TypeSymbol.INT || e2Type == TypeSymbol.FLOAT)) {
            return e2Type;
        }

        if (e1Type == TypeSymbol.FLOAT && (e2Type == TypeSymbol.INT || e2Type == TypeSymbol.FLOAT)) {
            return TypeSymbol.FLOAT;
        }

        ASTVisitor.error(token, "Operation with incompabile types");
        return null;
    }
};