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
    public static final TypeSymbol SELF_TYPE = new TypeSymbol("SELF_TYPE", OBJECT);

    Map<String, IdSymbol> attributes = new LinkedHashMap<>();
    Map<String, MethodSymbol> methods = new LinkedHashMap<>();

    TypeSymbol parent;
    String parentName; // because of forward references

    public TypeSymbol(String name, String parentName) {
        super(name);
        this.parentName = parentName;

        IdSymbol self = new IdSymbol("self", SELF_TYPE);

        attributes = new LinkedHashMap<>() {
            {
                put(self.getName(), self);
            }
        };
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

    /**
     * @param type
     * @return
     */
    public boolean inherits(TypeSymbol type) {
        if (this == type) {
            return true;
        }

        if (parent != null) {
            return parent.inherits(type);
        }

        return false;
    }

    public boolean comparesWithItself() {
        return this == TypeSymbol.BOOL || this == TypeSymbol.INT || this == TypeSymbol.STRING;
    }
}
