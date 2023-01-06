package cool.visitor;

import cool.AST.*;

import org.antlr.v4.runtime.Token;

public class ASTStringify implements ASTVisitor<String> {
	private static final String PAD_STR = " ";

	private static final String ATTRIBUTE_STR = "attribute";
	private static final String ASSIGNAMENT_STR = "<-";
	private static final String BLOCK_STR = "block";
	private static final String CASE_STR = "case";
	private static final String CLASS_STR = "class";
	private static final String FORMAL_STR = "formal";
	private static final String IMPLICIT_DISPATCH_STR = "implicit dispatch";
	private static final String EXPLICIT_DISPATCH_STR = ".";
	private static final String LET_STR = "let";
	private static final String METHOD_STR = "method";
	private static final String PROGRAM_STR = "program";
	private static final String VARIABLE_STR = "local";
	private static final String WHILE_STR = "while";
	private static final int NR_PAD_STR = 2;

	private int level;

	public ASTStringify() {
		level = 0;
	}

	@Override
	public String visit(final ASTAlternative alternative) {
		String str = pad("case branch");

		level++;
		str += alternative.getName().accept(this);
		str += alternative.getType().accept(this);
		str += alternative.getBody().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTArithmetical arithmetical) {
		String str = pad(arithmetical.getOperator());

		level++;
		str += arithmetical.getLhs().accept(this);
		str += arithmetical.getRhs().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTAssignment assignment) {
		String str = pad(ASSIGNAMENT_STR);

		level++;
		str += assignment.getName().accept(this);
		str += assignment.getExpression().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTAttribute attribute) {
		String str = pad(ATTRIBUTE_STR);

		level++;
		str += attribute.getName().accept(this);
		str += attribute.getType().accept(this);
		str += attribute.getInitialization() == null ? "" : attribute.getInitialization().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTBlock block) {
		String str = pad(BLOCK_STR);

		level++;
		str += block.getStatements().stream().map(
				expr -> expr.accept(this)).reduce("", String::concat);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTBool bool) {
		return pad(bool.getToken());
	}

	@Override
	public String visit(final ASTCase casee) {
		String str = pad(CASE_STR);

		level++;
		str += casee.getCondition().accept(this);
		str += casee.getAlternatives().stream().map(
				expr -> expr.accept(this)).reduce("", String::concat);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTClassDefine classDefine) {
		String str = pad(CLASS_STR);

		level++;
		str += classDefine.getName().accept(this);
		str += classDefine.getParent() == null ? "" : classDefine.getParent().accept(this);
		str += classDefine.getFeatures().stream().map(
				expr -> expr.accept(this)).reduce("", String::concat);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTExpression expression) {
		return visit((ASTNode) expression);
	}

	@Override
	public String visit(final ASTFeature feature) {
		return visit((ASTNode) feature);
	}

	@Override
	public String visit(final ASTFormal formal) {
		String str = pad(FORMAL_STR);

		level++;
		str += formal.getName().accept(this);
		str += formal.getType().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTIf iff) {
		String str = pad(iff.getContext().start);

		level++;
		str += iff.getCondition().accept(this);
		str += iff.getConsequent().accept(this);
		str += iff.getAlternative().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTInt intt) {
		return pad(intt.getToken());
	}

	@Override
	public String visit(final ASTIsvoid isvoid) {
		String str = pad(isvoid.getContext().start);

		level++;
		str += isvoid.getExpression().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTLet let) {
		String str = pad(LET_STR);

		level++;
		str += let.getDeclarations().stream().map(
				expr -> expr.accept(this)).reduce("", String::concat);

		str += let.getBody().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTMethod method) {
		String str = pad(METHOD_STR);

		level++;
		str += method.getName().accept(this);
		str += method.getParameters().stream().map(
				expr -> expr.accept(this)).reduce("", String::concat);
		str += method.getType().accept(this);
		str += method.getBody().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTMethodCall methodCall) {
		String str = pad(methodCall.getCaller() == null ? IMPLICIT_DISPATCH_STR : EXPLICIT_DISPATCH_STR);

		level++;
		if (methodCall.getCaller() != null) {
			str += methodCall.getCaller().accept(this);
			if (methodCall.getActualCaller() != null) {
				str += methodCall.getActualCaller().accept(this);
			}
		}
		str += methodCall.getMethod().accept(this);
		str += methodCall.getArguments().stream().map(
				expr -> expr.accept(this)).reduce("", String::concat);
		level--;
		return str;
	}

	@Override
	public String visit(ASTMethodId methodId) {
		return pad(methodId.getToken());
	}

	@Override
	public String visit(final ASTNegative negative) {
		String str = pad(negative.getContext().start);

		level++;
		str += negative.getExpression().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTNew neww) {
		String str = pad(neww.getContext().start);

		level++;
		str += neww.getType().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTNode node) {
		return pad(node.getContext().start);
	}

	@Override
	public String visit(final ASTNot not) {
		String str = pad(not.getContext().start);

		level++;
		str += not.getExpression().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(ASTObjectId objectId) {
		return pad(objectId.getToken());
	}

	@Override
	public String visit(final ASTProgram program) {
		String str = pad(PROGRAM_STR);

		level++;
		str += program.getClasses().stream().map(
				expr -> expr.accept(this)).reduce("", String::concat);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTRelational relational) {
		String str = pad(relational.getOperator());

		level++;
		str += relational.getLhs().accept(this);
		str += relational.getRhs().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTString string) {
		return pad(string.getToken());
	}

	@Override
	public String visit(final ASTTypeId typeId) {
		return pad(typeId.getToken());
	}

	@Override
	public String visit(final ASTVariable variable) {
		String str = pad(VARIABLE_STR);

		level++;
		str += variable.getName().accept(this);
		str += variable.getType().accept(this);
		str += variable.getInitialization() == null ? "" : variable.getInitialization().accept(this);
		level--;

		return str;
	}

	@Override
	public String visit(final ASTWhile whilee) {
		String str = pad(WHILE_STR);

		level++;
		str += whilee.getCondition().accept(this);
		str += whilee.getBody().accept(this);
		level--;

		return str;
	}

	private String padding() {
		return PAD_STR.repeat(NR_PAD_STR * level);
	}

	private String pad(final String str) {
		return padding() + str + "\n";
	}

	private String pad(final Token token) {
		return pad(token.getText());
	}

}
