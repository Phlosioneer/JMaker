package jmaker.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public interface Expression {
	public static class Array implements Expression {
		public final Expression[] elements;

		public Array(ArrayList<Expression> elements) {
			this(elements.toArray(size->new Expression[size]));
		}

		public Array(Expression[] elements) {
			this.elements = elements;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(elements);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Array)) {
				return false;
			}
			Array other = (Array) obj;
			return Arrays.equals(elements, other.elements);
		}
	}

	public static class Dictionary implements Expression {
		public final Expression[] keys;
		public final Expression[] values;

		public Dictionary(ArrayList<Expression> keys, ArrayList<Expression> values) {
			this(keys.toArray(size->new Expression[size]), values.toArray(size->new Expression[size]));
		}

		public Dictionary(Expression[] keys, Expression[] values) {
			this.keys = keys;
			this.values = values;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(keys);
			result = prime * result + Arrays.hashCode(values);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Dictionary)) {
				return false;
			}
			Dictionary other = (Dictionary) obj;
			return Arrays.equals(keys, other.keys) && Arrays.equals(values, other.values);
		}
	}

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
