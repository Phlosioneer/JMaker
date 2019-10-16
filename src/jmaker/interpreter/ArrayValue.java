package jmaker.interpreter;

import java.util.ArrayList;

public class ArrayValue implements ExpressionValue {
	public final ExpressionValue[] elements;

	public ArrayValue(ArrayList<ExpressionValue> elements) {
		this(elements.toArray(size->new ExpressionValue[size]));
	}

	public ArrayValue(ExpressionValue[] elements) {
		this.elements = elements;
	}

	@Override
	public ExpressionValue indexBy(ExpressionValue index) {
		if (index.getType() != DataType.Number_Int) {
			throw new RuntimeException("Arrays cannot be indexed by " + index.getType());
		}
		int i = index.asInteger();
		if (i < 0) {
			throw new RuntimeException("Negative index: " + i);
		}
		if (i >= elements.length) {
			throw new RuntimeException("Index out of bounds: " + i + " (array size: " + elements.length + ")");
		}
		return elements[i];
	}

	@Override
	public DataType getType() {
		return DataType.Array;
	}

	@Override
	public String castToString() {
		var builder = new StringBuilder();
		builder.append('[');

		if (elements.length > 0) {
			builder.append(elements[0].castToString());
		}

		for (int i = 1; i < elements.length; i++) {
			builder.append(",");
			var cast = elements[i].castToString();
			if (elements[i].getType() == DataType.String) {
				builder.append('"');
				builder.append(cast);
				builder.append('"');
			} else {
				builder.append(cast);
			}
		}

		builder.append(']');
		return builder.toString();
	}
}
