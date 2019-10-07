package jmaker.parser;

import java.util.ArrayList;
import tests.TestUtil;

public class Expression {

	protected Expression() {}

	@Override
	public String toString() {
		throw new UnsupportedOperationException(getClass().getName() + " MUST implement toString");
	}

	public static class Binary extends Expression {
		public final Expression left;
		public final BinaryOperator operator;
		public final Expression right;

		public Binary(Expression left, BinaryOperator operator, Expression right) {
			this.left = left;
			this.operator = operator;
			this.right = right;
		}

		@Override
		public String toString() {
			return "{Binary, op: " + operator + ", left: " + left + ", right: " + right + "}";
		}
	}

	public static class Unary extends Expression {
		public final Expression inner;
		public final UnaryOperator operator;

		public Unary(Expression inner, UnaryOperator operator) {
			this.inner = inner;
			this.operator = operator;
		}

		@Override
		public String toString() {
			return "{Unary, op: " + operator + ", inner: " + inner + "}";
		}
	}

	public static class Index extends Expression {
		public final Expression variable;
		public final Expression indexExpression;

		public Index(Expression variable, Expression indexExpression) {
			this.variable = variable;
			this.indexExpression = indexExpression;
		}

		@Override
		public String toString() {
			return "{Index, inner: " + variable + ", index: " + indexExpression + "}";
		}
	}

	public static class FunctionCall extends Expression {
		public final Expression functionName;
		public final Expression[] args;

		public FunctionCall(Expression functionName, ArrayList<Expression> args) {
			this(functionName, args.toArray(size->new Expression[size]));
		}

		public FunctionCall(Expression functionName, Expression[] args) {
			this.functionName = functionName;
			this.args = args;
		}

		@Override
		public String toString() {
			var ret = new StringBuilder();
			ret.append("{FunctionCall, function: ");
			ret.append(functionName);
			ret.append(", args: ");
			ret.append(TestUtil.arrayToString(args));
			ret.append("}");
			return ret.toString();
		}
	}

	public static class Number extends Expression {
		public final boolean isInteger;
		public final int intValue;
		public final double doubleValue;

		public Number(int value) {
			isInteger = true;
			intValue = value;
			doubleValue = 0;
		}

		public Number(double value) {
			isInteger = false;
			intValue = 0;
			doubleValue = value;
		}

		@Override
		public String toString() {
			if (isInteger) {
				return "{Int, " + intValue + "}";
			} else {
				return "{Double, " + doubleValue + "}";
			}
		}
	}

	public static class StringLiteral extends Expression {
		public final String value;

		public StringLiteral(String value) {
			// TODO: Escape character processing
			this.value = value;
		}

		@Override
		public String toString() {
			return "{\"" + value + "\"}";
		}
	}

	public static class BooleanLiteral extends Expression {
		public final boolean value;

		public BooleanLiteral(boolean value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return "{" + value + "}";
		}
	}

	public static class Symbol extends Expression {
		public final String name;

		public Symbol(String name) {
			this.name = name;
		}

		@Override
		public String toString() {
			return "{" + name + "}";
		}
	}
}
