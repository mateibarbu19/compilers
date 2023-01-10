package cool.scopes;

import java.util.*;

import cool.symbols.Symbol;

public class GlobalScope implements Scope {
    Map<String, Symbol> symbols = new LinkedHashMap<>();

    @Override
    public boolean add(Symbol sym) {
        // Reject duplicates in the same scope.
        if (symbols.containsKey(sym.getName()))
            return false;

        symbols.put(sym.getName(), sym);

        return true;
    }

    @Override
    public Symbol lookup(String name) {
        return symbols.get(name);
    }

    @Override
    public Scope getParent() {
        return null;
    }

    @Override
    public String toString() {
        return symbols.values().toString();
    }

}
