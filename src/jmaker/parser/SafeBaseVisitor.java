package jmaker.parser;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.RuleNode;
import org.antlr.v4.runtime.tree.TerminalNode;
import codegen.JMakerParser.ArrayLiteralContext;
import codegen.JMakerParser.AssignOpContext;
import codegen.JMakerParser.AssignmentContext;
import codegen.JMakerParser.BlockContext;
import codegen.JMakerParser.DictLiteralContext;
import codegen.JMakerParser.ElseIfStatementContext;
import codegen.JMakerParser.ExpressionContext;
import codegen.JMakerParser.ExpressionListContext;
import codegen.JMakerParser.FileContext;
import codegen.JMakerParser.ForEachContext;
import codegen.JMakerParser.ForManualContext;
import codegen.JMakerParser.ForStatementContext;
import codegen.JMakerParser.FuncDefArgContext;
import codegen.JMakerParser.FunctionCallContext;
import codegen.JMakerParser.FunctionDefContext;
import codegen.JMakerParser.IfStatementContext;
import codegen.JMakerParser.IndexBracketsContext;
import codegen.JMakerParser.IndexContext;
import codegen.JMakerParser.KeyValuePairContext;
import codegen.JMakerParser.LambdaArgsContext;
import codegen.JMakerParser.LambdaContext;
import codegen.JMakerParser.LiteralContext;
import codegen.JMakerParser.PrimaryContext;
import codegen.JMakerParser.RuleStatementContext;
import codegen.JMakerParser.SimpleAssignmentContext;
import codegen.JMakerParser.StatementContext;
import codegen.JMakerParser.UnambiguousVarContext;
import codegen.JMakerParser.WhileStatementContext;
import codegen.JMakerParserVisitor;

public class SafeBaseVisitor<T> implements JMakerParserVisitor<T> {
	@Override
	public T visitStatement(StatementContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitAssignment(AssignmentContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitForEach(ForEachContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitForManual(ForManualContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visit(ParseTree arg0) {
		throw new RuntimeException();
	}

	@Override
	public T visitChildren(RuleNode arg0) {
		throw new RuntimeException();
	}

	@Override
	public T visitErrorNode(ErrorNode arg0) {
		throw new RuntimeException();
	}

	@Override
	public T visitTerminal(TerminalNode arg0) {
		throw new RuntimeException();
	}

	@Override
	public T visitAssignOp(AssignOpContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitArrayLiteral(ArrayLiteralContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitIndexBrackets(IndexBracketsContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitLiteral(LiteralContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitFile(FileContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitDictLiteral(DictLiteralContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitBlock(BlockContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitExpression(ExpressionContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitForStatement(ForStatementContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitFuncDefArg(FuncDefArgContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitIndex(IndexContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitIfStatement(IfStatementContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitSimpleAssignment(SimpleAssignmentContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitWhileStatement(WhileStatementContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitKeyValuePair(KeyValuePairContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitExpressionList(ExpressionListContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitRuleStatement(RuleStatementContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitFunctionDef(FunctionDefContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitFunctionCall(FunctionCallContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitUnambiguousVar(UnambiguousVarContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitPrimary(PrimaryContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitLambdaArgs(LambdaArgsContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitLambda(LambdaContext context) {
		throw new RuntimeException();
	}

	@Override
	public T visitElseIfStatement(ElseIfStatementContext context) {
		throw new RuntimeException();
	}
}
