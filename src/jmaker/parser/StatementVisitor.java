package jmaker.parser;

import java.util.Arrays;
import org.antlr.v4.runtime.tree.TerminalNode;
import codegen.JMakerLexer;
import codegen.JMakerParser.AssignmentContext;
import codegen.JMakerParser.ForEachContext;
import codegen.JMakerParser.ForManualContext;
import codegen.JMakerParser.ForStatementContext;
import codegen.JMakerParser.FunctionDefContext;
import codegen.JMakerParser.IfStatementContext;
import codegen.JMakerParser.RuleStatementContext;
import codegen.JMakerParser.SimpleAssignmentContext;
import codegen.JMakerParser.StatementContext;
import codegen.JMakerParser.WhileStatementContext;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.IntegerValue;

public class StatementVisitor extends SafeBaseVisitor<Statement> {
	private final VisitorManager parent;

	public StatementVisitor(VisitorManager parent) {
		this.parent = parent;
	}

	@Override
	public Statement visitStatement(StatementContext context) {
		if (context.assignment() != null) {
			return visitAssignment(context.assignment());
		}
		if (context.ifStatement() != null) {
			return visitIfStatement(context.ifStatement());
		}
		if (context.whileStatement() != null) {
			return visitWhileStatement(context.whileStatement());
		}
		if (context.forStatement() != null) {
			return visitForStatement(context.forStatement());
		}
		if (context.ruleStatement() != null) {
			return visitRuleStatement(context.ruleStatement());
		}
		if (context.block() != null) {
			var block = parent.blockVisitor.visit(context.block());
			return new Statement.BlockStatement(block);
		}
		if (context.functionDef() != null) {
			return visitFunctionDef(context.functionDef());
		}
		if (context.RETURN() != null) {
			Expression inner;
			if (context.expression() != null) {
				inner = parent.expressionVisitor.visit(context.expression());
			} else {
				inner = null;
			}
			return new Statement.ExpressionStatement(inner, ExpressionStatementKind.RETURN);
		}
		if (context.expression() != null) {
			ExpressionStatementKind kind;
			if (context.ANGLE_RIGHT() != null) {
				kind = ExpressionStatementKind.COMMAND;
			} else {
				kind = ExpressionStatementKind.NORMAL;
			}
			var inner = parent.expressionVisitor.visitExpression(context.expression());
			return new Statement.ExpressionStatement(inner, kind);
		} else {
			return new Statement.Empty();
		}
	}

	@Override
	public Statement visitAssignment(AssignmentContext context) {
		var symbol = new Expression.Symbol(context.NAME().getText());
		var assignToken = (TerminalNode) context.assignOp().getChild(0);
		var expression = parent.expressionVisitor.visitExpression(context.expression());

		BinaryOperator rewriteOp;
		switch (assignToken.getSymbol().getType()) {
			case JMakerLexer.EQUALS:
				return new Statement.Assignment(symbol, expression);

			// Rewrite "Symbol <op>= Expression" to "Symbol = Symbol <op> Expression"
			case JMakerLexer.PLUS_EQUAL:
				rewriteOp = BinaryOperator.ADD;
				break;
			case JMakerLexer.MINUS_EQUAL:
				rewriteOp = BinaryOperator.SUB;
				break;
			case JMakerLexer.STAR_EQUAL:
				rewriteOp = BinaryOperator.MULT;
				break;
			case JMakerLexer.SLASH_EQUAL:
				rewriteOp = BinaryOperator.DIV;
				break;
			case JMakerLexer.AMP_EQUAL:
				rewriteOp = BinaryOperator.AND;
				break;
			case JMakerLexer.PIPE_EQUAL:
				rewriteOp = BinaryOperator.OR;
				break;
			default:
				var current = assignToken.getSymbol().getType();
				throw new RuntimeException("Unrecognized assignment operator: " + current);
		}

		var newExpression = new Expression.Binary(symbol, rewriteOp, expression);
		return new Statement.Assignment(symbol, newExpression);
	}

	@Override
	public Statement visitIfStatement(IfStatementContext context) {
		Expression[] conditions;
		Block[] elseBlocks;
		var firstCondition = parent.expressionVisitor.visitExpression(context.ifCondition);
		var firstBlock = parent.blockVisitor.visitBlock(context.ifBlock);

		var elseIfStatements = context.elseIfStatement();
		if (elseIfStatements != null && elseIfStatements.size() > 0) {
			conditions = new Expression[elseIfStatements.size() + 1];
			elseBlocks = new Block[elseIfStatements.size() + 1];

			conditions[0] = firstCondition;
			elseBlocks[0] = firstBlock;
			for (int i = 0; i < elseIfStatements.size(); i++) {
				var current = elseIfStatements.get(i);
				var currentCondition = parent.expressionVisitor.visitExpression(current.expression());
				var currentBlock = parent.blockVisitor.visitBlock(current.block());
				conditions[i + 1] = currentCondition;
				elseBlocks[i + 1] = currentBlock;
			}
		} else {
			conditions = new Expression[]{
				firstCondition
			};
			elseBlocks = new Block[]{
				firstBlock
			};
		}

		if (context.elseBlock == null) {
			return new Statement.If(conditions, elseBlocks);
		} else {
			var finalBlock = parent.blockVisitor.visitBlock(context.elseBlock);
			return new Statement.If(conditions, elseBlocks, finalBlock);
		}
	}

	@Override
	public Statement visitWhileStatement(WhileStatementContext context) {
		var expression = parent.expressionVisitor.visitExpression(context.expression());
		var block = parent.blockVisitor.visitBlock(context.block());
		return new Statement.WhileLoop(expression, block);
	}

	@Override
	public Statement visitForStatement(ForStatementContext context) {
		if (context.forManual() != null) {
			return visitForManual(context.forManual());
		} else {
			assert (context.forEach() != null);
			return visitForEach(context.forEach());
		}
	}

	@Override
	public Statement visitForEach(ForEachContext context) {
		// Rewrite "for (Name = Expression) Block" as:
		// {
		//   <anon-int-0> = 0;
		//   <anon-array-0> = Expression;
		//   <anon-int-1> = length(<anon-array-0>);
		//   while (<anon-int-0> < <anon-int-1>) {
		//      Name = <anon-array-0>[<anon-int-0>];
		//      Block
		//      <anon-int-0> = <anon-int-0> + 1;
		//   }
		// }

		var incrementVar = parent.generateAnonymousName("int");
		var cachedArray = parent.generateAnonymousName("array");
		var cachedArrayLength = parent.generateAnonymousName("int");

		var assignment = (Statement.Assignment) visitSimpleAssignment(context.simpleAssignment());
		var userVarName = assignment.leftSide;
		var arrayExpression = assignment.rightSide;
		var userBlock = parent.blockVisitor.visitBlock(context.block());

		var innerBlockStatements = new Statement[userBlock.statements.length + 2];
		innerBlockStatements[0] = new Statement.Assignment(userVarName, new Expression.Index(cachedArray, incrementVar));
		for (int i = 1; i < innerBlockStatements.length - 1; i++) {
			innerBlockStatements[i] = userBlock.statements[i - 1];
		}
		innerBlockStatements[innerBlockStatements.length - 1] = new Statement.Assignment(
				//
				incrementVar,
				//
				new Expression.Binary(
						//
						incrementVar,
						//
						BinaryOperator.ADD,
						//
						new IntegerValue(1)));
		var innerBlock = new Block(innerBlockStatements);

		var lengthFunctionCall = new Expression.FunctionCall(new Expression.Symbol("length"), new Expression[]{
			cachedArray
		});
		var condition = new Expression.Binary(incrementVar, BinaryOperator.LESS, cachedArrayLength);
		var outerBlock = new Block(new Statement[]{
			new Statement.Assignment(incrementVar, new IntegerValue(0)),
			new Statement.Assignment(cachedArray, arrayExpression),
			new Statement.Assignment(cachedArrayLength, lengthFunctionCall),
			new Statement.WhileLoop(condition, innerBlock)
		});

		return new Statement.BlockStatement(outerBlock);
	}

	@Override
	public Statement visitForManual(ForManualContext context) {
		// Rewrite "or (Assign1; Conditional; Assign2) Block" as:
		// {
		//   Assign1;
		//   while (Conditional) {
		//     Block
		//     Assign2;
		//   }
		// }

		// Conditional statement
		Expression condition;
		if (context.condition == null) {
			condition = new BooleanValue(true);
		} else {
			condition = parent.expressionVisitor.visitExpression(context.expression());
		}

		// Inner block and Update statement
		Block innerBlock = parent.blockVisitor.visitBlock(context.block());
		if (context.update != null) {
			var newLength = innerBlock.statements.length + 1;
			var newStatements = Arrays.copyOf(innerBlock.statements, newLength);
			newStatements[newLength - 1] = visitAssignment(context.update);
			innerBlock = new Block(newStatements);
		}
		var whileStatement = new Statement.WhileLoop(condition, innerBlock);

		// Outer block, if needed.
		if (context.init != null) {
			return new Statement.BlockStatement(new Block(new Statement[]{
				visitSimpleAssignment(context.init),
				whileStatement
			}));
		} else {
			return whileStatement;
		}
	}

	@Override
	public Statement visitRuleStatement(RuleStatementContext context) {
		var targets = parent.expressionVisitor.tryParseExpressionList(context.targets);
		var deps = parent.expressionVisitor.tryParseExpressionList(context.deps);
		var block = parent.blockVisitor.visitBlock(context.block());
		return new Statement.Rule(targets, deps, block);
	}

	@Override
	public Statement visitFunctionDef(FunctionDefContext context) {
		var functionName = new Expression.Symbol(context.NAME().getText());
		var rawArgs = context.funcDefArg();
		var block = parent.blockVisitor.visitBlock(context.block());
		boolean pipeArgSet = false;
		int pipeArg = 0;

		if (rawArgs == null || rawArgs.size() == 0) {
			return new Statement.FunctionDefinition(functionName, null, 0, block);
		} else {
			var args = new Expression.Symbol[rawArgs.size()];

			for (int i = 0; i < rawArgs.size(); i++) {
				var current = rawArgs.get(i);
				args[i] = new Expression.Symbol(current.NAME().getText());
				if (current.STAR() != null) {
					if (pipeArgSet) {
						throw new RuntimeException("Can't mark more than one argument as pipeable");
					} else {
						pipeArgSet = true;
						pipeArg = i;
					}
				}
			}

			return new Statement.FunctionDefinition(functionName, args, pipeArg, block);
		}
	}

	@Override
	public Statement visitSimpleAssignment(SimpleAssignmentContext context) {
		var name = new Expression.Symbol(context.NAME().getText());
		var expression = parent.expressionVisitor.visitExpression(context.expression());
		return new Statement.Assignment(name, expression);
	}
}
