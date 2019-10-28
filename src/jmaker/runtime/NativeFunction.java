package jmaker.runtime;

import jmaker.interpreter.DataType;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.FunctionValue;

public class NativeFunction extends FunctionValue {
	public final NativeFunctionImpl function;
	public final SigType[][] signatures;

	public NativeFunction(String symbolName, NativeFunctionImpl function) {
		super(symbolName);
		this.function = function;
		signatures = null;
	}

	public NativeFunction(String symbolName, NativeFunctionImpl function, SigType[] signature) {
		super(symbolName);
		this.function = function;
		assert (signature != null);
		this.signatures = new SigType[][]{
			signature
		};
	}

	public NativeFunction(String symbolName, NativeFunctionImpl function, SigType[][] signatures) {
		super(symbolName);
		this.function = function;
		this.signatures = signatures;
		assert (signatures != null);
		assert (signatures.length != 0);
	}

	@Override
	public ExpressionValue call(ExpressionValue[] args) {
		if (signatures != null) {
			boolean foundMatchingSig = false;
			for (var signature : signatures) {
				if (signature.length != args.length) {
					continue;
				}
				boolean matchesCurrentSig = true;
				for (int i = 0; i < args.length; i++) {
					if (!signature[i].matches(args[i].getType())) {
						matchesCurrentSig = false;
						break;
					}
				}
				if (matchesCurrentSig) {
					foundMatchingSig = true;
					break;
				}
			}
			if (!foundMatchingSig) {
				throw new RuntimeException("Unrecognized signature for '" + symbolName + "'");
			}
		}
		try {
			return this.function.call(args);
		} catch (Exception e) {
			throw new RuntimeException("Error in function '" + symbolName + "'", e);
		}
	}

	public static interface NativeFunctionImpl {
		ExpressionValue call(ExpressionValue[] args);
	}

	public static enum SigType {
		Integer, Double, String, Array, Dictionary, Function, Number, Any;

		public boolean matches(DataType other) {
			switch (this) {
				case Integer:
					return other == DataType.Number_Int;
				case Double:
					return other == DataType.Number_Double;
				case String:
					return other == DataType.String;
				case Array:
					return other == DataType.Array;
				case Dictionary:
					return other == DataType.Dictionary;
				case Function:
					return other == DataType.Function;
				case Number:
					return (other == DataType.Number_Int || other == DataType.Number_Double);
				case Any:
					return true;
				default:
					throw new RuntimeException("Unrecognized signature type: " + this);
			}
		}
	}
}
