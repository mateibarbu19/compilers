package cool.symbols;

import java.util.LinkedHashMap;
import java.util.Map;

import cool.scopes.Scope;

public class MethodSymbol extends Symbol implements Scope {
    Map<String, IdSymbol> knownObjects;
    TypeSymbol parent;
    private TypeSymbol returnType;

    public MethodSymbol(String name, TypeSymbol parent) {
        super(name);
        this.parent = parent;

        knownObjects = new LinkedHashMap<>();
    }

    public MethodSymbol(String name, TypeSymbol parent, TypeSymbol returnType) {
        this(name, parent);

        this.returnType = returnType;
    }

    @Override
    public boolean add(Symbol sym) {
        if (!(sym instanceof IdSymbol)) {
            return false;
        }

        if (knownObjects.containsKey(sym.getName()))
            return false;

        knownObjects.put(sym.getName(), (IdSymbol) sym);

        return true;
    }

    @Override
    public Symbol lookup(String s) {
        var sym = knownObjects.get(s);

        if (sym != null)
            return sym;

        if (parent != null)
            return parent.lookup(s);

        return null;
    }

    @Override
    public Scope getParent() {
        return parent;
    }

    /**
     * @return the knownObjects
     */
    public Map<String, IdSymbol> getKnownObjects() {
        return knownObjects;
    }

    /**
     * @return the returnType
     */
    public TypeSymbol getReturnType() {
        return returnType;
    }

    /**
     * @param returnType the returnType to set
     */
    public void setReturnType(TypeSymbol returnType) {
        this.returnType = returnType;
    }
}