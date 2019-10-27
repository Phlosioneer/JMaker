package jmaker.interpreter;

import java.util.Objects;
import jmaker.parser.Expression;

public class StringValue implements ExpressionValue, Expression {
	public final String value;

	public StringValue(String value) {
		this.value = value;
	}

	@Override
	public DataType getType() {
		return DataType.String;
	}

	@Override
	public boolean isIndexable() {
		return true;
	}

	@Override
	public ExpressionValue indexBy(ExpressionValue index) {
		if (index.getType() != DataType.Number_Int) {
			String article = "a";
			if (index.getType() == DataType.Array) {
				article = "an";
			}
			throw new RuntimeException("Cannot index a string by " + article + " " + index.getClass().getName());
		}

		int indexValue = ((IntegerValue) index).value;
		assert (indexValue >= 0);
		assert (indexValue < value.length());
		char inner = value.charAt(indexValue);
		return new StringValue(Character.toString(inner));
	}

	@Override
	public String asString() {
		return this.value;
	}

	@Override
	public String toString() {
		return "{\"" + value + "\"}";
	}

	@Override
	public String castToString() {
		return value;
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
		if (!(obj instanceof StringValue)) {
			return false;
		}
		StringValue other = (StringValue) obj;
		return Objects.equals(value, other.value);
	}
}
