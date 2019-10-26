package jmaker.runtime;

import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.FunctionValue;

public class NativeFunction extends FunctionValue {
	public NativeFunctionImpl function;

	public NativeFunction(String symbolName, NativeFunctionImpl function) {
		super(symbolName);
		this.function = function;
	}

	@Override
	public ExpressionValue call(ExpressionValue[] args) {
		try {
			return this.function.call(args);
		} catch (Exception e) {
			throw new RuntimeException("Error in function '" + symbolName + "'", e);
		}
	}

	public static interface NativeFunctionImpl {
		ExpressionValue call(ExpressionValue[] args);
	}
}
