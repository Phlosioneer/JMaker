package jmaker.parser;

public enum BinaryOperator {
	OR, AND, EQUAL, NOT_EQUAL, LESS, LESS_EQUAL, GREATER, GREATER_EQUAL,
	//
	MULT, DIV, ADD, SUB, PIPE;

	public boolean isComparisonOperator() {
		switch (this) {
			case EQUAL:
			case NOT_EQUAL:
			case LESS:
			case LESS_EQUAL:
			case GREATER:
			case GREATER_EQUAL:
				return true;
			default:
				return false;
		}
	}
}
