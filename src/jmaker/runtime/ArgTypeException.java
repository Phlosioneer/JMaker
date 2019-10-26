package jmaker.runtime;

import jmaker.interpreter.ExpressionValue;

public class ArgTypeException extends RuntimeException {

	public ArgTypeException(ExpressionValue[] args) {
		super(formatError(args));
	}

	private static String formatError(ExpressionValue[] actualTypes) {
		assert (actualTypes != null);
		assert (actualTypes.length > 0);
		StringBuilder ret = new StringBuilder();
		ret.append("not applicable for types (");
		ret.append(actualTypes[0].getType());
		for (int i = 1; i < actualTypes.length; i++) {
			ret.append(", ");
			ret.append(actualTypes[i].getType());
		}
		ret.append(")");
		return ret.toString();
	}
}
