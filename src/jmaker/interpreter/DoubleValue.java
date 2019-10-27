package jmaker.interpreter;

import java.util.Objects;
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
		return Objects.hash(value);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof DoubleValue)) {
			return false;
		}
		DoubleValue other = (DoubleValue) obj;
		return Double.doubleToLongBits(value) == Double.doubleToLongBits(other.value);
	}

}
