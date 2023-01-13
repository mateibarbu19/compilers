package cool.scopes;

import cool.symbols.IdSymbol;
import cool.symbols.Symbol;

public class CaseScope implements Scope {
    Scope parent;
    IdSymbol var;

    public CaseScope(Scope parent) {
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol symbol) {
        if (!(symbol instanceof IdSymbol)) {
            return false;
        }

        // TODO
        var = (IdSymbol) symbol;

        return true;
    }

    @Override
    public Symbol lookup(String str) {
        if (var.getName().equals(str)) {
            return var;
        }

        return parent.lookup(str);
    }

    @Override
    public Scope getParent() {
        return parent;
    }
}
