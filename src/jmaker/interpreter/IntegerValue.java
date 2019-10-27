package jmaker.interpreter;

import java.util.Objects;
import jmaker.parser.Expression;

public class IntegerValue implements ExpressionValue, Expression {
	public final int value;

	public IntegerValue(int value) {
		this.value = value;
	}

	@Override
	public DataType getType() {
		return DataType.Number_Int;
	}

	@Override
	public int asInteger() {
		return this.value;
	}

	@Override
	public double asDouble() {
		return this.value;
	}

	@Override
	public String toString() {
		return "{Integer, " + value + "}";
	}

	@Override
	public String castToString() {
		return Integer.toString(value);
	}

	@Override
	public int hashCode() {
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof IntegerValue)) {
			return false;
		}
		IntegerValue other = (IntegerValue) obj;
		return value == other.value;
	}

}
