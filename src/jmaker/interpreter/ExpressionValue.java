package jmaker.interpreter;

public interface ExpressionValue {
	DataType getType();

	String castToString();

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

	default String asString() {
		throw new RuntimeException(this + " isn't a string.");
	}

	default int asInteger() {
		throw new RuntimeException(this + " isn't an int.");
	}

	default double asDouble() {
		throw new RuntimeException(this + " isn't a double.");
	}
}
