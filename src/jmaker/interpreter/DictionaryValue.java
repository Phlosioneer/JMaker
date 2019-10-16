package jmaker.interpreter;

import java.util.Map;

public class DictionaryValue implements ExpressionValue {

	public final Map<ExpressionValue, ExpressionValue> elements;

	public DictionaryValue(Map<ExpressionValue, ExpressionValue> elements) {
		this.elements = elements;
		for (var key : elements.keySet()) {
			if (key.getType() == DataType.Array) {
				throw new RuntimeException("Can't use an array as a key.");
			}
			if (key.getType() == DataType.Dictionary) {
				throw new RuntimeException("Can't use a dictionary as a key.");
			}
			if (key.getType() == DataType.Function) {
				throw new RuntimeException("Can't use a function as a key.");
			}
		}
	}

	@Override
	public DataType getType() {
		return DataType.Dictionary;
	}

	@Override
	public String castToString() {
		var builder = new StringBuilder();
		builder.append('{');
		boolean shouldAddComma = false;
		for (var entry : elements.entrySet()) {
			if (shouldAddComma) {
				builder.append(", ");
			} else {
				shouldAddComma = true;
			}

			var key = entry.getKey();
			var value = entry.getValue();
			if (key.getType() == DataType.String) {
				builder.append('"');
				builder.append(key.castToString());
				builder.append('"');
			} else {
				builder.append(key.castToString());
			}

			builder.append(": ");
			builder.append(value.castToString());
		}
		builder.append('}');
		return builder.toString();
	}
}
