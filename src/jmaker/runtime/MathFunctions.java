package jmaker.runtime;

import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Memory;

public class MathFunctions {

	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("round", MathFunctions::round),
		new NativeFunction("floor", MathFunctions::floor),
		new NativeFunction("ceil", MathFunctions::ceil),
		new NativeFunction("abs", MathFunctions::abs),
		new NativeFunction("max", MathFunctions::max),
		new NativeFunction("min", MathFunctions::min),
		new NativeFunction("numberCanBeInt", MathFunctions::numberCanBeInt)
	};

	public static void register(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue round(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() == DataType.Number_Int) {
			return original;
		} else if (original.getType() == DataType.Number_Double) {
			return new IntegerValue((int) Math.round(original.asDouble()));
		} else {
			throw new ArgTypeException(args);
		}
	}

	public static ExpressionValue floor(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() == DataType.Number_Int) {
			return original;
		} else if (original.getType() == DataType.Number_Double) {
			return new IntegerValue((int) Math.floor(original.asDouble()));
		} else {
			throw new ArgTypeException(args);
		}
	}

	public static ExpressionValue ceil(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() == DataType.Number_Int) {
			return original;
		} else if (original.getType() == DataType.Number_Double) {
			return new IntegerValue((int) Math.ceil(original.asDouble()));
		} else {
			throw new ArgTypeException(args);
		}
	}

	public static ExpressionValue abs(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() == DataType.Number_Int) {
			return new IntegerValue(Math.abs(original.asInteger()));
		} else if (original.getType() == DataType.Number_Double) {
			return new DoubleValue(Math.abs(original.asDouble()));
		} else {
			throw new ArgTypeException(args);
		}
	}

	public static ExpressionValue max(ExpressionValue[] args) {
		if (args.length != 2) {
			throw new ArgCountException(2, args.length);
		}

		var num1 = args[0];
		var num2 = args[1];
		if (!(num1.getType().isNumber() && num2.getType().isNumber())) {
			throw new ArgTypeException(args);
		}
		if (num1.getType() == DataType.Number_Int && num2.getType() == DataType.Number_Int) {
			return new IntegerValue(Math.max(num1.asInteger(), num2.asInteger()));
		} else {
			return new DoubleValue(Math.max(num1.asDouble(), num2.asDouble()));
		}
	}

	public static ExpressionValue min(ExpressionValue[] args) {
		if (args.length != 2) {
			throw new ArgCountException(2, args.length);
		}

		var num1 = args[0];
		var num2 = args[1];
		if (!(num1.getType().isNumber() && num2.getType().isNumber())) {
			throw new ArgTypeException(args);
		}
		if (num1.getType() == DataType.Number_Int && num2.getType() == DataType.Number_Int) {
			return new IntegerValue(Math.min(num1.asInteger(), num2.asInteger()));
		} else {
			return new DoubleValue(Math.min(num1.asDouble(), num2.asDouble()));
		}
	}

	public static ExpressionValue numberCanBeInt(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() == DataType.Number_Int) {
			return new BooleanValue(true);
		} else {
			return new BooleanValue(Math.round(original.asDouble()) == original.asDouble());
		}
	}
}
