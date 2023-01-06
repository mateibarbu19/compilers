package cool.visitor;

import cool.AST.*;

public interface ASTVisitor<T> {
	T visit(ASTAlternative alternative);

	T visit(ASTArithmetical arithmetical);

	T visit(ASTAssignment assignment);

	T visit(ASTAttribute attribute);

	T visit(ASTBlock block);

	T visit(ASTBool bool);

	T visit(ASTCase casee);

	T visit(ASTClassDefine classDefine);

	T visit(ASTExpression expression);

	T visit(ASTFeature feature);

	T visit(ASTFormal formal);

	T visit(ASTIf iff);

	T visit(ASTInt intt);

	T visit(ASTIsvoid isvoid);

	T visit(ASTLet let);

	T visit(ASTMethod method);

	T visit(ASTMethodCall methodCall);

	T visit(ASTMethodId methodId);

	T visit(ASTNegative negative);

	T visit(ASTNew neww);

	T visit(ASTNode node);

	T visit(ASTNot not);

	T visit(ASTObjectId objectId);

	T visit(ASTProgram program);

	T visit(ASTRelational relational);

	T visit(ASTString string);

	T visit(ASTTypeId typeId);

	T visit(ASTVariable variable);

	T visit(ASTWhile whilee);
}
