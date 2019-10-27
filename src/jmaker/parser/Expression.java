package jmaker.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
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

		@Override
		public int hashCode() {
			return Objects.hash(left, operator, right);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Binary)) {
				return false;
			}
			Binary other = (Binary) obj;
			return Objects.equals(left, other.left) && operator == other.operator && Objects.equals(right, other.right);
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

		@Override
		public int hashCode() {
			return Objects.hash(inner, operator);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Unary)) {
				return false;
			}
			Unary other = (Unary) obj;
			return Objects.equals(inner, other.inner) && operator == other.operator;
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

		@Override
		public int hashCode() {
			return Objects.hash(indexExpression, variable);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Index)) {
				return false;
			}
			Index other = (Index) obj;
			return Objects.equals(indexExpression, other.indexExpression) && Objects.equals(variable, other.variable);
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

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(args);
			result = prime * result + Objects.hash(functionName);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof FunctionCall)) {
				return false;
			}
			FunctionCall other = (FunctionCall) obj;
			return Arrays.equals(args, other.args) && Objects.equals(functionName, other.functionName);
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

		@Override
		public int hashCode() {
			return Objects.hash(name);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Symbol)) {
				return false;
			}
			Symbol other = (Symbol) obj;
			return Objects.equals(name, other.name);
		}
	}
}
