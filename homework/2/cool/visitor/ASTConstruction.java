package cool.visitor;

import java.util.stream.Collectors;

import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;

import cool.AST.*;

import cool.parser.CoolParser.AdditionContext;
import cool.parser.CoolParser.AlternativeContext;
import cool.parser.CoolParser.AssignmentContext;
import cool.parser.CoolParser.AttributeContext;
import cool.parser.CoolParser.BlockContext;
import cool.parser.CoolParser.BoolContext;
import cool.parser.CoolParser.CaseContext;
import cool.parser.CoolParser.ClassDefineContext;
import cool.parser.CoolParser.EnclosedContext;
import cool.parser.CoolParser.FormalContext;
import cool.parser.CoolParser.IdContext;
import cool.parser.CoolParser.IfContext;
import cool.parser.CoolParser.IntContext;
import cool.parser.CoolParser.IsvoidContext;
import cool.parser.CoolParser.LetContext;
import cool.parser.CoolParser.MethodCallContext;
import cool.parser.CoolParser.MethodContext;
import cool.parser.CoolParser.MultiplicationContext;
import cool.parser.CoolParser.NegativeContext;
import cool.parser.CoolParser.NewContext;
import cool.parser.CoolParser.NotContext;
import cool.parser.CoolParser.OwnMethodCallContext;
import cool.parser.CoolParser.ProgramContext;
import cool.parser.CoolParser.RelationalContext;
import cool.parser.CoolParser.StringContext;
import cool.parser.CoolParser.VariableContext;
import cool.parser.CoolParser.WhileContext;
import cool.parser.CoolParserBaseVisitor;

public class ASTConstruction extends CoolParserBaseVisitor<ASTNode> {
	public ASTObjectId visitObjectId(TerminalNode node) {
		if (node != null)
			return new ASTObjectId(node.getSymbol());
		return null;
	}

	public ASTObjectId visitObjectId(Token token) {
		if (token != null)
			return new ASTObjectId(token);
		return null;
	}

	public ASTMethodId visitMethodId(TerminalNode node) {
		if (node != null)
			return new ASTMethodId(node.getSymbol());
		return null;
	}

	public ASTMethodId visitMethodId(Token token) {
		if (token != null)
			return new ASTMethodId(token);
		return null;
	}

	public ASTTypeId visitTypeId(TerminalNode node) {
		if (node != null)
			return new ASTTypeId(node.getSymbol());
		return null;
	}

	public ASTTypeId visitTypeId(Token token) {
		if (token != null)
			return new ASTTypeId(token);
		return null;
	}

	@Override
	public ASTNode visitAddition(AdditionContext ctx) {
		return new ASTArithmetical(ctx.start, (ASTExpression) visit(ctx.lhs), ctx.op,
				(ASTExpression) visit(ctx.rhs));
	}

	@Override
	public ASTNode visitAlternative(AlternativeContext ctx) {
		return new ASTAlternative(ctx.start, visitObjectId(ctx.OBJECTID()), visitTypeId(ctx.TYPEID()),
				(ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitAssignment(AssignmentContext ctx) {
		return new ASTAssignment(ctx.start, visitObjectId(ctx.OBJECTID()),
				(ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitAttribute(AttributeContext ctx) {
		var expression = ctx.expression() == null ? null : (ASTExpression) visit(ctx.expression());

		return new ASTAttribute(ctx.start, visitObjectId(ctx.OBJECTID()), visitTypeId(ctx.TYPEID()), expression);
	}

	@Override
	public ASTNode visitBlock(BlockContext ctx) {
		return new ASTBlock(ctx.start, ctx.expression()
				.stream()
				.map(node -> (ASTExpression) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitBool(BoolContext ctx) {
		return new ASTBool(ctx.BOOL().getSymbol());
	}

	@Override
	public ASTNode visitCase(CaseContext ctx) {
		return new ASTCase(ctx.start, (ASTExpression) visit(ctx.expression()), ctx.alternative()
				.stream()
				.map(node -> (ASTAlternative) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitClassDefine(ClassDefineContext ctx) {
		return new ASTClassDefine(ctx.start, visitTypeId(ctx.name), visitTypeId(ctx.parent), ctx.feature()
				.stream()
				.map(node -> (ASTFeature) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitEnclosed(EnclosedContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public ASTNode visitFormal(FormalContext ctx) {
		return new ASTFormal(ctx.start, visitObjectId(ctx.OBJECTID()), visitTypeId(ctx.TYPEID()));
	}

	@Override
	public ASTNode visitId(IdContext ctx) {
		return new ASTObjectId(ctx.OBJECTID().getSymbol());
	}

	@Override
	public ASTNode visitIf(IfContext ctx) {
		return new ASTIf(ctx.start, (ASTExpression) visit(ctx.condition), (ASTExpression) visit(ctx.consequent),
				(ASTExpression) visit(ctx.ifAlternative));
	}

	@Override
	public ASTNode visitInt(IntContext ctx) {
		return new ASTInt(ctx.INT().getSymbol());
	}

	@Override
	public ASTNode visitIsvoid(IsvoidContext ctx) {
		return new ASTIsvoid(ctx.start, (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitLet(LetContext ctx) {
		return new ASTLet(ctx.start, ctx.variable()
				.stream()
				.map(node -> (ASTVariable) visit(node))
				.collect(Collectors.toList()), (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitMethod(MethodContext ctx) {
		var paramters = ctx.formal() == null ? null
				: ctx.formal()
						.stream()
						.map(node -> (ASTFormal) visit(node))
						.collect(Collectors.toList());

		return new ASTMethod(ctx.start, visitMethodId(ctx.OBJECTID()), paramters, visitTypeId(ctx.TYPEID()),
				(ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitMethodCall(MethodCallContext ctx) {
		var actualCaller = ctx.TYPEID() == null ? null : visitTypeId(ctx.TYPEID());
		var arguments = ctx.arguments == null ? null
				: ctx.arguments
						.stream()
						.map(node -> (ASTExpression) visit(node))
						.collect(Collectors.toList());

		return new ASTMethodCall(ctx.start, (ASTExpression) visit(ctx.caller), actualCaller,
				visitMethodId(ctx.OBJECTID()), arguments);
	}

	@Override
	public ASTNode visitMultiplication(MultiplicationContext ctx) {
		return new ASTArithmetical(ctx.start, (ASTExpression) visit(ctx.lhs), ctx.op,
				(ASTExpression) visit(ctx.rhs));
	}

	@Override
	public ASTNode visitNegative(NegativeContext ctx) {
		return new ASTNegative(ctx.start, (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitNew(NewContext ctx) {
		return new ASTNew(ctx.start, visitTypeId(ctx.TYPEID()));
	}

	@Override
	public ASTNode visitNot(NotContext ctx) {
		return new ASTNot(ctx.start, (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitOwnMethodCall(OwnMethodCallContext ctx) {
		var arguments = ctx.arguments == null ? null
				: ctx.arguments
						.stream()
						.map(node -> (ASTExpression) visit(node))
						.collect(Collectors.toList());

		return new ASTMethodCall(ctx.start, null, null, visitMethodId(ctx.OBJECTID()), arguments);
	}

	@Override
	public ASTNode visitProgram(ProgramContext ctx) {
		return new ASTProgram(ctx.start, ctx.classDefine()
				.stream()
				.map(node -> (ASTClassDefine) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitRelational(RelationalContext ctx) {
		return new ASTRelational(ctx.start, (ASTExpression) visit(ctx.lhs), ctx.op,
				(ASTExpression) visit(ctx.rhs));
	}

	@Override
	public ASTNode visitString(StringContext ctx) {
		return new ASTString(ctx.STRING().getSymbol());
	}

	@Override
	public ASTNode visitVariable(VariableContext ctx) {
		var expression = ctx.expression() == null ? null : (ASTExpression) visit(ctx.expression());

		return new ASTVariable(ctx.start, visitObjectId(ctx.OBJECTID()), visitTypeId(ctx.TYPEID()), expression);
	}

	@Override
	public ASTNode visitWhile(WhileContext ctx) {
		return new ASTWhile(ctx.start, (ASTExpression) visit(ctx.condition), (ASTExpression) visit(ctx.body));
	}
}