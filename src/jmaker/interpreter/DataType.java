package jmaker.interpreter;

public enum DataType {
	String, Boolean, Array, Dictionary, Function,
	// These two are actually the same type, but they are stored differently.
	Number_Int, Number_Double;

	public boolean isNumber() {
		return this == Number_Int || this == Number_Double;
	}
}
