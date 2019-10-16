package jmaker.parser;

import java.util.ArrayList;
import tests.TestUtil;

public interface Expression {
	public static class Binary implements Expression {
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

	public static class Unary implements Expression {
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

	public static class Index implements Expression {
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

	public static class FunctionCall implements Expression {
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

	public static class Symbol implements Expression {
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
