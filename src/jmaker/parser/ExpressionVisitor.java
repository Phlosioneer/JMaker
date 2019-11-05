package jmaker.parser;

import org.antlr.v4.runtime.Token;
import codegen.JMakerLexer;
import codegen.JMakerParser.ArrayLiteralContext;
import codegen.JMakerParser.DictLiteralContext;
import codegen.JMakerParser.ExpressionContext;
import codegen.JMakerParser.ExpressionListContext;
import codegen.JMakerParser.Expression_otherContext;
import codegen.JMakerParser.FunctionCallContext;
import codegen.JMakerParser.IndexContext;
import codegen.JMakerParser.LambdaContext;
import codegen.JMakerParser.LiteralContext;
import codegen.JMakerParser.PrimaryContext;
import codegen.JMakerParser.UnambiguousVarContext;
import codegen.JMakerParser.UnaryContext;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.PathValue;
import jmaker.interpreter.StringValue;
import jmaker.parser.Expression.Symbol;

public class ExpressionVisitor extends SafeBaseVisitor<Expression> {
	private final VisitorManager parent;

	public ExpressionVisitor(VisitorManager parent) {
		this.parent = parent;
	}

	@Override
	public Expression visitExpression(ExpressionContext context) {
		if (context.expression_other() != null) {
			return visitExpression_other(context.expression_other());
		}
		var left = visitExpression(context.left);
		var right = visitExpression(context.right);
		var op = context.binop;

		return finishVisitingExpression(left, op, right);
	}

	@Override
	public Expression visitExpression_other(Expression_otherContext context) {
		if (context.binop == null) {
			return visitNonbinaryExpression(context);
		}
		var left = visitExpression_other(context.left);
		var right = visitExpression_other(context.right);
		var op = context.binop;
		return finishVisitingExpression(left, op, right);
	}

	private static Expression finishVisitingExpression(Expression left, Token op, Expression right) {
		BinaryOperator parsedOp;
		switch (op.getType()) {
			case JMakerLexer.PIPE:
				parsedOp = BinaryOperator.PIPE;
				break;
			case JMakerLexer.DOUBLE_AMP:
				parsedOp = BinaryOperator.AND;
				break;
			case JMakerLexer.DOUBLE_PIPE:
				parsedOp = BinaryOperator.OR;
				break;
			case JMakerLexer.DOUBLE_EQUAL:
				parsedOp = BinaryOperator.EQUAL;
				break;
			case JMakerLexer.BANG_EQUAL:
				parsedOp = BinaryOperator.NOT_EQUAL;
				break;
			case JMakerLexer.ANGLE_LEFT:
				parsedOp = BinaryOperator.LESS;
				break;
			case JMakerLexer.ANGLE_RIGHT:
				parsedOp = BinaryOperator.GREATER;
				break;
			case JMakerLexer.LESS_EQUAL:
				parsedOp = BinaryOperator.LESS_EQUAL;
				break;
			case JMakerLexer.GREATER_EQUAL:
				parsedOp = BinaryOperator.GREATER_EQUAL;
				break;
			case JMakerLexer.PLUS:
				parsedOp = BinaryOperator.ADD;
				break;
			case JMakerLexer.MINUS:
				parsedOp = BinaryOperator.SUB;
				break;
			case JMakerLexer.STAR:
				parsedOp = BinaryOperator.MULT;
				break;
			case JMakerLexer.FORWARD_SLASH:
				parsedOp = BinaryOperator.DIV;
				break;
			default:
				throw new RuntimeException("Unrecognized binary operator: " + op.getType());
		}

		return new Expression.Binary(left, parsedOp, right);
	}

	private Expression visitNonbinaryExpression(Expression_otherContext context) {
		if (context.unary() != null) {
			return visitUnary(context.unary());
		}
		if (context.primary() != null) {
			return visitPrimary(context.primary());
		}
		if (context.unambiguousVar() != null) {
			return visitUnambiguousVar(context.unambiguousVar());
		}
		if (context.lambda() != null) {
			return visitLambda(context.lambda());
		}
		throw new RuntimeException();
	}

	@Override
	public Expression visitUnary(UnaryContext context) {
		UnaryOperator op;
		var expression = visitExpression(context.expression());
		switch (context.unop.getType()) {
			case JMakerLexer.BANG:
				op = UnaryOperator.NOT;
				break;
			case JMakerLexer.MINUS:
				op = UnaryOperator.NEGATE;
				break;
			default:
				throw new RuntimeException();
		}
		return new Expression.Unary(expression, op);
	}

	@Override
	public Expression visitPrimary(PrimaryContext context) {
		if (context.literal() != null) {
			return visitLiteral(context.literal());
		}
		if (context.functionCall() != null) {
			return visitFunctionCall(context.functionCall());
		}
		if (context.expression() != null) {
			return visitExpression(context.expression());
		}
		throw new RuntimeException();
	}

	@Override
	public Expression visitUnambiguousVar(UnambiguousVarContext context) {
		if (context.expression() != null) {
			return visitExpression(context.expression());
		}
		if (context.NAME() != null) {
			return new Expression.Symbol(context.NAME().getText());
		}
		if (context.index() != null) {
			return visitIndex(context.index());
		}
		throw new RuntimeException();
	}

	@Override
	public Expression visitLambda(LambdaContext context) {
		var argNames = context.lambdaArgs().NAME();
		Expression.Symbol[] argSymbols;
		if (argNames == null) {
			argSymbols = new Symbol[0];
		} else {
			argSymbols = new Expression.Symbol[argNames.size()];
			for (int i = 0; i < argNames.size(); i++) {
				argSymbols[i] = new Expression.Symbol(argNames.get(i).getText());
			}
		}

		Block innerBlock;
		if (context.block() != null) {
			innerBlock = parent.blockVisitor.visit(context.block());
		} else {
			var expression = visitExpression(context.expression());
			innerBlock = new Block(new Statement[]{
				new Statement.ExpressionStatement(expression, ExpressionStatementKind.RETURN)
			});
		}

		var functionName = parent.generateAnonymousName("function");

		var definition = new Statement.FunctionDefinition(functionName, argSymbols, 0, innerBlock);
		return new Expression.Lambda(definition);
	}

	@Override
	public Expression visitIndex(IndexContext context) {
		Expression current;
		if (context.NAME() != null) {
			current = new Expression.Symbol(context.NAME().getText());
		} else {
			current = visitExpression(context.expression());
		}
		var bracketList = context.indexBrackets();
		for (int i = bracketList.size() - 1; i >= 0; i -= 1) {
			var bracket = bracketList.get(i);
			if (bracket.only != null) {
				var only = visitExpression(bracket.only);
				current = new Expression.Index(current, only);
			} else {
				Expression start;
				Expression end;
				if (bracket.start != null) {
					start = visitExpression(bracket.start);
				} else {
					start = null;
				}
				if (bracket.end != null) {
					end = visitExpression(bracket.end);
				} else {
					end = null;
				}

				current = new Expression.IndexRange(current, start, end);
			}
		}

		return current;
	}

	public Expression[] tryParseExpressionList(ExpressionListContext context) {
		if (context != null) {
			var rawDeps = context.expression();
			var ret = new Expression[rawDeps.size()];
			for (int i = 0; i < rawDeps.size(); i++) {
				var current = parent.expressionVisitor.visitExpression(rawDeps.get(i));
				ret[i] = current;
			}

			return ret;
		} else {
			return new Expression[0];
		}
	}

	@Override
	public Expression visitFunctionCall(FunctionCallContext context) {
		var functionRef = visitUnambiguousVar(context.unambiguousVar());
		var args = tryParseExpressionList(context.expressionList());
		return new Expression.FunctionCall(functionRef, args);
	}

	@Override
	public Expression visitLiteral(LiteralContext context) {
		if (context.arrayLiteral() != null) {
			return visitArrayLiteral(context.arrayLiteral());
		}
		if (context.dictLiteral() != null) {
			return visitDictLiteral(context.dictLiteral());
		}
		if (context.TRUE() != null) {
			return new BooleanValue(true);
		}
		if (context.FALSE() != null) {
			return new BooleanValue(false);
		}
		if (context.STRING() != null) {
			var unparsedString = context.STRING().getText();
			return parseString(unparsedString);
		}
		if (context.INTEGER() != null) {
			var separatorsRemoved = context.INTEGER().getText().replaceAll("_", "");
			return new IntegerValue(Integer.parseInt(separatorsRemoved));
		}
		var separatorsRemoved = context.FLOAT().getText().replaceAll("_", "");
		return new DoubleValue(Double.parseDouble(separatorsRemoved));
	}

	@Override
	public Expression visitArrayLiteral(ArrayLiteralContext context) {
		var elements = tryParseExpressionList(context.expressionList());
		return new Expression.Array(elements);
	}

	@Override
	public Expression visitDictLiteral(DictLiteralContext context) {
		var pairs = context.keyValuePair();
		var keys = new Expression[pairs.size()];
		var values = new Expression[pairs.size()];
		for (int i = 0; i < pairs.size(); i++) {
			var currentPair = pairs.get(i);
			var key = visitExpression(currentPair.key);
			var value = visitExpression(currentPair.value);
			keys[i] = key;
			values[i] = value;
		}

		return new Expression.Dictionary(keys, values);
	}

	private static Expression parseString(String string) {
		StringBuilder ret = new StringBuilder(string.length());

		char firstChar = string.charAt(0);
		char quoteChar;
		boolean isPath = false;
		boolean isRaw = false;
		int startIndex;
		if (firstChar == 'p' || firstChar == 'P') {
			isPath = true;
			quoteChar = string.charAt(1);
			startIndex = 2;
		} else if (firstChar == 'r' || firstChar == 'R') {
			isRaw = true;
			quoteChar = string.charAt(1);
			startIndex = 2;
		} else {
			quoteChar = firstChar;
			startIndex = 1;
		}

		int endIndex = string.length() - 2;

		for (int i = startIndex; i <= endIndex; i++) {
			char current = string.charAt(i);
			char next = string.charAt(i + 1);
			// Quotes can be escaped in raw and normal strings.
			if (current == '\\' && next == quoteChar && i != endIndex) {
				ret.append(quoteChar);
				i += 1;
			} else if (!isRaw && current == '\\') {
				if (i == endIndex) {
					// This should be impossible.
					throw new RuntimeException();
				}
				switch (next) {
					case 'n':
						ret.append('\n');
						break;
					case 't':
						ret.append('\t');
						break;
					case 'r':
						ret.append('\r');
						break;
					case '\\':
						ret.append('\\');
						break;
					case 'x': {
						if (i + 3 > endIndex) {
							throw new RuntimeException("Raw byte escape code without two hex digits");
						}
						var digits = string.substring(i + 2, i + 4);
						int value = Integer.parseInt(digits, 16);
						char valueAsChar = (char) value;
						ret.append(valueAsChar);

						i += 2;
						break;
					}
					case 'u': {
						if (i + 5 > endIndex) {
							throw new RuntimeException("Unicode escape code without four hex digits");
						}
						var digits = string.substring(i + 2, i + 6);
						int value = Integer.parseInt(digits, 16);
						char valueAsChar = (char) value;
						ret.append(valueAsChar);

						i += 4;
						break;
					}
					default:
						throw new RuntimeException("unrecognized escape code: \\" + next);
				}
				i += 1;
			} else {
				ret.append(current);
			}
		}

		var parsedString = ret.toString();
		if (isPath) {
			return new PathValue(parsedString);
		} else {
			return new StringValue(parsedString);
		}
	}
}
