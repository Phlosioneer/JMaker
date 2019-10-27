package jmaker.interpreter;

public interface ExpressionValue {
	DataType getType();

	default boolean isIndexable() {
		return false;
	}

	default ExpressionValue indexBy(ExpressionValue index) {
		if (isIndexable()) {
			throw new RuntimeException(getClass().getName() + " hasn't implemented required method indexBy()");
		} else {
			throw new RuntimeException(getClass().getName() + " is not indexable");
		}
	}

	default boolean asBoolean() {
		throw new RuntimeException(this + " isn't a boolean.");
	}

	default int asInteger() {
		throw new RuntimeException(this + " isn't an int.");
	}

	default double asDouble() {
		throw new RuntimeException(this + " isn't a double.");
	}
}
