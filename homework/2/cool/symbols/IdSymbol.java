package cool.symbols;

import cool.scopes.Scope;

public class IdSymbol extends Symbol {
	TypeSymbol type;
	Scope scope;
	Scope referencedScope;

	public IdSymbol(String name) {
		super(name);
	}

	public IdSymbol(String name, TypeSymbol type) {
		this(name);

		this.type = type;
	}

	public IdSymbol(String name, Scope scope) {
		this(name);

		this.scope = scope;
	}

	public IdSymbol(String name, TypeSymbol type, Scope scope) {
		this(name);

		this.type = type;
		this.scope = scope;
	}

	public TypeSymbol getType() {
		return type;
	}

	public void setType(TypeSymbol type) {
		this.type = type;
	}

	/**
	 * @return the scope
	 */
	public Scope getScope() {
		return scope;
	}

	/**
	 * @param scope the scope to set
	 */
	public void setScope(Scope scope) {
		this.scope = scope;
	}

	/**
	 * @return the referencedScope
	 */
	public Scope getReferencedScope() {
		return referencedScope;
	}

	/**
	 * @param referencedScope the referencedScope to set
	 */
	public void setReferencedScope(Scope referencedScope) {
		this.referencedScope = referencedScope;
	}
}
