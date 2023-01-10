package cool.symbols;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import cool.scopes.Scope;

public class TypeSymbol extends Symbol implements Scope {
    public static final TypeSymbol OBJECT = new TypeSymbol("Object", (TypeSymbol) null);
    public static final TypeSymbol BOOL = new TypeSymbol("Bool", OBJECT);
    public static final TypeSymbol INT = new TypeSymbol("Int", OBJECT);
    public static final TypeSymbol STRING = new TypeSymbol("String", OBJECT);
    public static final TypeSymbol IO = new TypeSymbol("IO", OBJECT);
    // TODO SELF_TYPE

    Map<String, IdSymbol> attributes = new LinkedHashMap<>();
    Map<String, MethodSymbol> methods = new LinkedHashMap<>();

    TypeSymbol parent;
    String parentName; // because of forward references

    public TypeSymbol(String name, String parentName) {
        super(name);
        this.parentName = parentName;

        attributes = new LinkedHashMap<>();
        methods = new LinkedHashMap<>();
    }

    public TypeSymbol(String name, TypeSymbol parent) {
        this(name, Optional.ofNullable(parent).map(p -> p.getName()).orElse(null));

        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        if (sym instanceof IdSymbol) {
            if (attributes.containsKey(sym.getName()))
                return false;

            attributes.put(sym.getName(), (IdSymbol) sym);

            return true;
        } else if (sym instanceof MethodSymbol) {
            if (methods.containsKey(sym.getName()))
                return false;

            methods.put(sym.getName(), (MethodSymbol) sym);

            return true;
        }

        return false;
    }

    @Override
    public Symbol lookup(String str) {
        var attributeSym = attributes.get(str);
        var methodSym = methods.get(str);

        // both a object and a method
        if (attributeSym != null && methodSym != null) {
            System.exit(-1);
        }

        if (attributeSym != null) {
            return attributeSym;
        }
        if (methodSym != null) {
            return methodSym;
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
     * @param parent the parent to set
     */
    public void setParent(TypeSymbol parent) {
        this.parent = parent;
    }

    /**
     * @return the parentName
     */
    public String getParentName() {
        return parentName;
    }
}
