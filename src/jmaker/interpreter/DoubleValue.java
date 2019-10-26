package jmaker.interpreter;

import jmaker.parser.Expression;

public class DoubleValue implements Expression, ExpressionValue {
	public final double value;

	public DoubleValue(double value) {
		this.value = value;
	}

	@Override
	public DataType getType() {
		return DataType.Number_Double;
	}

	@Override
	public double asDouble() {
		return value;
	}

	@Override
	public String toString() {
		return "{Double, " + value + "}";
	}

	@Override
	public String castToString() {
		return Double.toString(value);
	}

	@Override
	public int hashCode() {
		return Double.hashCode(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof DoubleValue)) {
			return false;
		}
		DoubleValue other = (DoubleValue) obj;

		return value == other.value;
	}
}
