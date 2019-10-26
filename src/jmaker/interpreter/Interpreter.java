package jmaker.interpreter;

import java.util.ArrayList;
import java.util.HashMap;
import jmaker.parser.BinaryOperator;
import jmaker.parser.Block;
import jmaker.parser.Expression;
import jmaker.parser.Expression.Binary;
import jmaker.parser.Expression.FunctionCall;
import jmaker.parser.Expression.Index;
import jmaker.parser.Expression.Symbol;
import jmaker.parser.Expression.Unary;
import jmaker.parser.Statement.Assignment;
import jmaker.parser.Statement.BlockStatement;
import jmaker.parser.Statement.Empty;
import jmaker.parser.Statement.ExpressionStatement;
import jmaker.parser.Statement.If;
import jmaker.parser.Statement.Rule;
import jmaker.parser.Statement.WhileLoop;

public class Interpreter {
	Block script;
	Memory memory;
	ArrayList<String> commandQueue;
	ArrayList<RuleValue> allRules;

	public Interpreter(Block script) {
		this.script = script;
		memory = new Memory();
	}

	public void run() {
		runBlock(script);
	}

	public void runBlock(Block block) {
		memory.pushScope();
		for (var statement : block.statements) {
			if (statement instanceof Assignment) {
				runAssignment((Assignment) statement);
			} else if (statement instanceof ExpressionStatement) {
				var expression = (ExpressionStatement) statement;
				if (expression.isCommand) {
					var command = runExpression(expression.expression);
					if (command.getType() != DataType.String) {
						throw new RuntimeException("Command expressions must return a string.");
					}
					pushCommand(command.asString());
				} else {
					runExpression(expression.expression);
				}
			} else if (statement instanceof BlockStatement) {
				runBlock(((BlockStatement) statement).block);
			} else if (statement instanceof If) {
				runIf((If) statement);
			} else if (statement instanceof WhileLoop) {
				runWhile((WhileLoop) statement);
			} else if (statement instanceof Rule) {
				createRule((Rule) statement);
			} else if (statement instanceof Empty) {
				// Do nothing.
			} else {
				throw new RuntimeException("Unrecognized statement type: " + statement);
			}
		}
		memory.popScope();
	}

	private void createRule(Rule statement) {
		String[] resolvedTargets = new String[statement.targets.length];
		String[] resolvedDependencies = new String[statement.dependencies.length];

		for (int i = 0; i < statement.targets.length; i++) {
			var targetExpression = statement.targets[i];
			var targetValue = runExpression(targetExpression);
			if (targetValue.getType() != DataType.String) {
				throw new RuntimeException("Rule targets must be strings.");
			}
			resolvedTargets[i] = targetValue.asString();
		}

		for (int i = 0; i < statement.dependencies.length; i++) {
			var depExpression = statement.dependencies[i];
			var depValue = runExpression(depExpression);
			if (depValue.getType() != DataType.String) {
				throw new RuntimeException("Rule dependencies must be strings.");
			}
			resolvedDependencies[i] = depValue.asString();
		}

		if (commandQueue != null) {
			throw new RuntimeException("Cannot nest Rules.");
		}
		commandQueue = new ArrayList<>();

		runBlock(statement.block);

		var commandsAsRawArray = commandQueue.toArray(size->new String[size]);
		allRules.add(new RuleValue(resolvedTargets, resolvedDependencies, commandsAsRawArray));
		commandQueue = null;
	}

	private void pushCommand(String command) {
		if (commandQueue == null) {
			throw new RuntimeException("Cannot use command statement outside of a Rule body.");
		}
		commandQueue.add(command);
	}

	private void runIf(If statement) {
		for (int i = 0; i < statement.conditionals.length; i++) {
			var result = runExpression(statement.conditionals[i]);
			if (result.getType() != DataType.Boolean) {
				throw new RuntimeException("Conditionals must return a boolean.");
			}
			if (result.asBoolean()) {
				runBlock(statement.blocks[i]);
				return;
			}
		}

		if (statement.elseBlock != null) {
			runBlock(statement.elseBlock);
		}
	}

	private void runAssignment(Assignment statement) {
		var value = runExpression(statement.rightSide);
		memory.set(statement.leftSide.name, value);
	}

	private void runWhile(WhileLoop statement) {
		while (true) {
			var value = runExpression(statement.condition);
			if (value.getType() != DataType.Boolean) {
				throw new RuntimeException("Conditionals must return a boolean.");
			}
			if (!value.asBoolean()) {
				break;
			}
			runBlock(statement.block);
		}
	}

	public ExpressionValue runExpression(Expression expression) {
		// Literal values.
		if (expression instanceof ExpressionValue) {
			return (ExpressionValue) expression;
		}

		// Variables and arrays.
		if (expression instanceof Symbol) {
			var symbol = (Symbol) expression;
			var ret = memory.get(symbol.name);
			if (ret == null) {
				throw new RuntimeException("No function or variable named " + ret + " was found in the current scope.");
			}
			return ret;
		}
		if (expression instanceof Index) {
			var castExpression = (Index) expression;
			var inner = runExpression(castExpression.variable);
			var index = runExpression(castExpression.indexExpression);
			return inner.indexBy(index);
		}

		// Unary and binary expressions.
		if (expression instanceof Unary) {
			var castExpression = (Unary) expression;
			var innerValue = runExpression(castExpression.inner);
			switch (castExpression.operator) {
				case NEGATE:
					if (innerValue.getType() == DataType.Number_Double) {
						return new DoubleValue(-1 * innerValue.asDouble());
					} else if (innerValue.getType() == DataType.Number_Int) {
						return new IntegerValue(-1 * innerValue.asInteger());
					} else {
						throw new RuntimeException(innerValue.getType() + " can't be negated");
					}
				case NOT:
					if (innerValue.getType() != DataType.Boolean) {
						throw new RuntimeException("Logical not can only be applied to booleans");
					}
				default:
					return new BooleanValue(!innerValue.asBoolean());
			}
		}
		if (expression instanceof Binary) {
			var castExpression = (Binary) expression;
			var left = runExpression(castExpression.left);
			var right = runExpression(castExpression.right);
			return runBinaryOp(left, right, castExpression.operator);
		}
		if (expression instanceof FunctionCall) {
			var functionCall = (FunctionCall) expression;
			var function = runExpression(functionCall.functionName);
			if (function.getType() != DataType.Function) {
				throw new RuntimeException("Expected function, found " + function.getType());
			}
			var args = new ExpressionValue[functionCall.args.length];
			for (int i = 0; i < args.length; i++) {
				var arg = functionCall.args[i];
				args[i] = runExpression(arg);
			}
			return ((FunctionValue) function).call(args);
		}
		throw new RuntimeException("Unrecognized expression type: " + expression.getClass().getName());
	}

	public ExpressionValue runBinaryOp(ExpressionValue left, ExpressionValue right, BinaryOperator op) {
		// Adding arrays to arrays merges them.
		if (left.getType() == DataType.Array && right.getType() == DataType.Array && op == BinaryOperator.ADD) {
			ArrayValue leftAsArray = (ArrayValue) left;
			ArrayValue rightAsArray = (ArrayValue) right;

			int total = leftAsArray.elements.length + rightAsArray.elements.length;
			ArrayList<ExpressionValue> combinedArrays = new ArrayList<>(total);
			for (var item : leftAsArray.elements) {
				combinedArrays.add(item);
			}
			for (var item : rightAsArray.elements) {
				combinedArrays.add(item);
			}
			return new ArrayValue(combinedArrays);
		}

		// Adding items to arrays.
		if (left.getType() == DataType.Array && op == BinaryOperator.ADD) {
			ArrayValue leftAsArray = (ArrayValue) left;

			int total = leftAsArray.elements.length + 1;
			ExpressionValue[] newArray = new ExpressionValue[total];
			for (int i = 0; i < leftAsArray.elements.length; i++) {
				newArray[i] = leftAsArray.elements[i];
			}
			newArray[total - 1] = right;
			return new ArrayValue(newArray);
		}

		// Adding two dictionaries.
		if (left.getType() == DataType.Dictionary && right.getType() == DataType.Dictionary && op == BinaryOperator.ADD) {
			DictionaryValue leftAsDict = (DictionaryValue) left;
			DictionaryValue rightAsDict = (DictionaryValue) right;

			HashMap<ExpressionValue, ExpressionValue> newDict = new HashMap<>();
			newDict.putAll(leftAsDict.elements);
			newDict.putAll(rightAsDict.elements);
			return new DictionaryValue(newDict);
		}

		// Adding strings converts the other value to a string.
		if (left.getType() == DataType.String) {
			return new StringValue(left.asString() + right.castToString());
		}
		if (right.getType() == DataType.String) {
			return new StringValue(left.castToString() + right.asString());
		}

		// Numbers
		if (left.getType().isNumber() && right.getType().isNumber()) {
			boolean useDoubleValue = (left.getType() == DataType.Number_Double || right.getType() == DataType.Number_Double);

			switch (op) {
				case ADD:
					if (useDoubleValue) {
						return new DoubleValue(left.asDouble() + right.asDouble());
					}
					return new IntegerValue(left.asInteger() + right.asInteger());
				case SUB:
					if (useDoubleValue) {
						return new DoubleValue(left.asDouble() - right.asDouble());
					}
					return new IntegerValue(left.asInteger() - right.asInteger());
				case MULT:
					if (useDoubleValue) {
						return new DoubleValue(left.asDouble() * right.asDouble());
					}
					return new IntegerValue(left.asInteger() * right.asInteger());
				case DIV:
					double result = left.asDouble() / right.asDouble();
					if (useDoubleValue) {
						return new DoubleValue(result);
					}
					return new IntegerValue((int) Math.floor(result));
				case LESS:
					return new BooleanValue(left.asDouble() < right.asDouble());
				case LESS_EQUAL:
					return new BooleanValue(left.asDouble() <= right.asDouble());
				case GREATER:
					return new BooleanValue(left.asDouble() > right.asDouble());
				case GREATER_EQUAL:
					return new BooleanValue(left.asDouble() >= right.asDouble());
				// Handle equal and not equal here, to be able to say "2.0 == 2".
				case EQUAL:
					if (useDoubleValue) {
						return new BooleanValue(left.asDouble() == right.asDouble());
					}
					return new BooleanValue(left.asInteger() == right.asInteger());
				case NOT_EQUAL:
					if (useDoubleValue) {
						return new BooleanValue(left.asDouble() != right.asDouble());
					}
					return new BooleanValue(left.asInteger() != right.asInteger());
				default:
					// fallthrough
			}
		}

		// Boolean logic
		if (left.getType() == DataType.Boolean && right.getType() == DataType.Boolean) {
			if (op == BinaryOperator.AND) {
				return new BooleanValue(left.asBoolean() && right.asBoolean());
			}
			if (op == BinaryOperator.OR) {
				return new BooleanValue(left.asBoolean() || right.asBoolean());
			}
		}

		// Equality
		if (op == BinaryOperator.EQUAL) {
			if (left.getType() != right.getType()) {
				return new BooleanValue(false);
			}
			if (left.hashCode() != right.hashCode()) {
				return new BooleanValue(false);
			}
			return new BooleanValue(left.equals(right));
		}
		if (op == BinaryOperator.NOT_EQUAL) {
			if (left.getType() != right.getType()) {
				return new BooleanValue(true);
			}
			if (left.hashCode() != right.hashCode()) {
				return new BooleanValue(true);
			}
			return new BooleanValue(!left.equals(right));
		}

		throw new RuntimeException("Cannot apply " + op + " to types " + left.getType() + " and " + right.getType());
	}
}
