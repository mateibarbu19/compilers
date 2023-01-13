package cool.symbols;

import cool.scopes.Scope;

import java.util.LinkedHashMap;
import java.util.Map;

public class LetSymbol implements Scope {
    Map<String, IdSymbol> variables;
    Scope parent;

    public LetSymbol(Scope parent) {
        this.parent = parent;

        this.variables = new LinkedHashMap<>();
    }

    @Override
    public boolean add(Symbol symbol) {
        if (!(symbol instanceof IdSymbol)) {
            return false;
        }

        variables.putIfAbsent(symbol.getName(), (IdSymbol) symbol);
        return true;
    }

    @Override
    public Symbol lookup(String str) {
        Symbol symbol = variables.get(str);

        if (symbol != null) {
            return symbol;
        }

        if (parent != null) {
            return parent.lookup(str);
        }

        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }
}
