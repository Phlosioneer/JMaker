package jmaker.interpreter;

public class FunctionValue implements ExpressionValue {
	public final String symbolName;

	public FunctionValue(String symbolName) {
		this.symbolName = symbolName;
	}

	@Override
	public DataType getType() {
		return DataType.Function;
	}

	@Override
	public String castToString() {
		return "<Function " + symbolName + ">";
	}
}
