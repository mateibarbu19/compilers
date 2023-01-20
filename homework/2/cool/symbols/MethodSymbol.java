package cool.symbols;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cool.scopes.Scope;

public class MethodSymbol extends Symbol implements Scope {
    Map<String, IdSymbol> parameters;
    TypeSymbol parent;
    private TypeSymbol returnType;

    public MethodSymbol(String name, TypeSymbol parent) {
        super(name);
        this.parent = parent;

        parameters = new LinkedHashMap<>();
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

        if (parameters.containsKey(sym.getName()))
            return false;

        parameters.put(sym.getName(), (IdSymbol) sym);

        return true;
    }

    @Override
    public Symbol lookup(String str) {
        Symbol symbol = parameters.get(str);

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

    /**
     * @return the parametersNames
     */
    public List<String> getParametersNames() {
        return parameters.keySet().stream().collect(Collectors.toList());
    }

    /**
     * @return the parameters
     */
    public List<IdSymbol> getParameters() {
        return parameters.values().stream().collect(Collectors.toList());
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