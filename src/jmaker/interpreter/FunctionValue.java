package jmaker.interpreter;

public abstract class FunctionValue implements ExpressionValue {
	public final String symbolName;

	public FunctionValue(String symbolName) {
		this.symbolName = symbolName;
	}

	public abstract ExpressionValue call(ExpressionValue[] args);

	@Override
	public DataType getType() {
		return DataType.Function;
	}

	@Override
	public String toString() {
		return "<Function " + symbolName + ">";
	}
}
