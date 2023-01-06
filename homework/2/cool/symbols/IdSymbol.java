package cool.symbols;

public class IdSymbol extends Symbol {
	TypeSymbol type;

	public IdSymbol(String name) {
		super(name);
	}

	public IdSymbol(String name, TypeSymbol type) {
		this(name);

		this.type = type;
	}

	public TypeSymbol getType() {
		return type;
	}

	public void setType(TypeSymbol type) {
		this.type = type;
	}
}
