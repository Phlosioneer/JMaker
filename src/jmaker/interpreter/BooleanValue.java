package jmaker.interpreter;

import jmaker.parser.Expression;

public class BooleanValue implements ExpressionValue, Expression {
	public final boolean value;

	public BooleanValue(boolean value) {
		this.value = value;
	}

	@Override
	public DataType getType() {
		return DataType.Boolean;
	}

	@Override
	public String toString() {
		return "{" + value + "}";
	}

	@Override
	public String castToString() {
		if (value) {
			return "true";
		} else {
			return "false";
		}
	}
}
