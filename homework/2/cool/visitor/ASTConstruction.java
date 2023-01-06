package cool.visitor;

import java.util.stream.Collectors;

import org.antlr.v4.runtime.ParserRuleContext;
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
	public ASTObjectId visitObjectId(final ParserRuleContext context, final TerminalNode node) {
		if (node != null)
			return new ASTObjectId(context, node.getSymbol());
		return null;
	}

	public ASTObjectId visitObjectId(final ParserRuleContext context, final Token token) {
		if (token != null)
			return new ASTObjectId(context, token);
		return null;
	}

	public ASTMethodId visitMethodId(final ParserRuleContext context, final TerminalNode node) {
		if (node != null)
			return new ASTMethodId(context, node.getSymbol());
		return null;
	}

	public ASTMethodId visitMethodId(final ParserRuleContext context, final Token token) {
		if (token != null)
			return new ASTMethodId(context, token);
		return null;
	}

	public ASTTypeId visitTypeId(final ParserRuleContext context, final TerminalNode node) {
		if (node != null)
			return new ASTTypeId(context, node.getSymbol());
		return null;
	}

	public ASTTypeId visitTypeId(final ParserRuleContext context, final Token token) {
		if (token != null)
			return new ASTTypeId(context, token);
		return null;
	}

	@Override
	public ASTNode visitAlternative(final AlternativeContext ctx) {
		return new ASTAlternative(ctx, visitObjectId(ctx, ctx.OBJECTID()), visitTypeId(ctx, ctx.TYPEID()),
				(ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitAddition(final AdditionContext ctx) {
		return new ASTArithmetical(ctx, (ASTExpression) visit(ctx.lhs), ctx.op,
				(ASTExpression) visit(ctx.rhs));
	}

	@Override
	public ASTNode visitAssignment(final AssignmentContext ctx) {
		return new ASTAssignment(ctx, visitObjectId(ctx, ctx.OBJECTID()),
				(ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitAttribute(final AttributeContext ctx) {
		final var expression = ctx.expression() == null ? null : (ASTExpression) visit(ctx.expression());

		return new ASTAttribute(ctx, visitObjectId(ctx, ctx.OBJECTID()), visitTypeId(ctx, ctx.TYPEID()),
				expression);
	}

	@Override
	public ASTNode visitBlock(final BlockContext ctx) {
		return new ASTBlock(ctx, ctx.expression()
				.stream()
				.map(node -> (ASTExpression) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitBool(final BoolContext ctx) {
		return new ASTBool(ctx, ctx.BOOL().getSymbol());
	}

	@Override
	public ASTNode visitCase(final CaseContext ctx) {
		return new ASTCase(ctx, (ASTExpression) visit(ctx.expression()), ctx.alternative()
				.stream()
				.map(node -> (ASTAlternative) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitClassDefine(final ClassDefineContext ctx) {
		return new ASTClassDefine(ctx, visitTypeId(ctx, ctx.name), visitTypeId(ctx, ctx.parent), ctx.feature()
				.stream()
				.map(node -> (ASTFeature) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitEnclosed(final EnclosedContext ctx) {
		return visit(ctx.expression());
	}

	@Override
	public ASTNode visitFormal(final FormalContext ctx) {
		return new ASTFormal(ctx, visitObjectId(ctx, ctx.OBJECTID()), visitTypeId(ctx, ctx.TYPEID()));
	}

	@Override
	public ASTNode visitId(final IdContext ctx) {
		return new ASTObjectId(ctx, ctx.OBJECTID().getSymbol());
	}

	@Override
	public ASTNode visitIf(final IfContext ctx) {
		return new ASTIf(ctx, (ASTExpression) visit(ctx.condition), (ASTExpression) visit(ctx.consequent),
				(ASTExpression) visit(ctx.ifAlternative));
	}

	@Override
	public ASTNode visitInt(final IntContext ctx) {
		return new ASTInt(ctx, ctx.INT().getSymbol());
	}

	@Override
	public ASTNode visitIsvoid(final IsvoidContext ctx) {
		return new ASTIsvoid(ctx, (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitLet(final LetContext ctx) {
		return new ASTLet(ctx, ctx.variable()
				.stream()
				.map(node -> (ASTVariable) visit(node))
				.collect(Collectors.toList()), (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitMethod(final MethodContext ctx) {
		final var paramters = ctx.formal() == null ? null
				: ctx.formal()
						.stream()
						.map(node -> (ASTFormal) visit(node))
						.collect(Collectors.toList());

		return new ASTMethod(ctx, visitMethodId(ctx, ctx.OBJECTID()), paramters, visitTypeId(ctx, ctx.TYPEID()),
				(ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitMethodCall(final MethodCallContext ctx) {
		final var actualCaller = ctx.TYPEID() == null ? null : visitTypeId(ctx, ctx.TYPEID());
		final var arguments = ctx.arguments == null ? null
				: ctx.arguments
						.stream()
						.map(node -> (ASTExpression) visit(node))
						.collect(Collectors.toList());

		return new ASTMethodCall(ctx, (ASTExpression) visit(ctx.caller), actualCaller,
				visitMethodId(ctx, ctx.OBJECTID()), arguments);
	}

	@Override
	public ASTNode visitMultiplication(final MultiplicationContext ctx) {
		return new ASTArithmetical(ctx, (ASTExpression) visit(ctx.lhs), ctx.op,
				(ASTExpression) visit(ctx.rhs));
	}

	@Override
	public ASTNode visitNegative(final NegativeContext ctx) {
		return new ASTNegative(ctx, (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitNew(final NewContext ctx) {
		return new ASTNew(ctx, visitTypeId(ctx, ctx.TYPEID()));
	}

	@Override
	public ASTNode visitNot(final NotContext ctx) {
		return new ASTNot(ctx, (ASTExpression) visit(ctx.expression()));
	}

	@Override
	public ASTNode visitOwnMethodCall(final OwnMethodCallContext ctx) {
		final var arguments = ctx.arguments == null ? null
				: ctx.arguments
						.stream()
						.map(node -> (ASTExpression) visit(node))
						.collect(Collectors.toList());

		return new ASTMethodCall(ctx, null, null, visitMethodId(ctx, ctx.OBJECTID()), arguments);
	}

	@Override
	public ASTNode visitProgram(final ProgramContext ctx) {
		return new ASTProgram(ctx, ctx.classDefine()
				.stream()
				.map(node -> (ASTClassDefine) visit(node))
				.collect(Collectors.toList()));
	}

	@Override
	public ASTNode visitRelational(final RelationalContext ctx) {
		return new ASTRelational(ctx, (ASTExpression) visit(ctx.lhs), ctx.op,
				(ASTExpression) visit(ctx.rhs));
	}

	@Override
	public ASTNode visitString(final StringContext ctx) {
		return new ASTString(ctx, ctx.STRING().getSymbol());
	}

	@Override
	public ASTNode visitVariable(final VariableContext ctx) {
		final var expression = ctx.expression() == null ? null : (ASTExpression) visit(ctx.expression());

		return new ASTVariable(ctx, visitObjectId(ctx, ctx.OBJECTID()), visitTypeId(ctx, ctx.TYPEID()),
				expression);
	}

	@Override
	public ASTNode visitWhile(final WhileContext ctx) {
		return new ASTWhile(ctx, (ASTExpression) visit(ctx.condition), (ASTExpression) visit(ctx.body));
	}
}