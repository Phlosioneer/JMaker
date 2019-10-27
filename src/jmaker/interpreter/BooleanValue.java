package jmaker.interpreter;

import java.util.Objects;
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
		if (value) {
			return "true";
		} else {
			return "false";
		}
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
		if (!(obj instanceof BooleanValue)) {
			return false;
		}
		BooleanValue other = (BooleanValue) obj;
		return value == other.value;
	}

}
