package jmaker.runtime;

import jmaker.interpreter.DataType;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Memory;
import jmaker.main.UnreachableCodeBlockException;
import jmaker.runtime.NativeFunction.SigType;

public class MathFunctions {

	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("round", MathFunctions::round, new SigType[]{
			SigType.Number
		}),
		new NativeFunction("floor", MathFunctions::floor, new SigType[]{
			SigType.Number
		}),
		new NativeFunction("ceil", MathFunctions::ceil, new SigType[]{
			SigType.Number
		}),
		new NativeFunction("abs", MathFunctions::abs, new SigType[]{
			SigType.Number
		}),
		new NativeFunction("max", MathFunctions::max, new SigType[]{
			SigType.Number, SigType.Number
		}),
		new NativeFunction("min", MathFunctions::min, new SigType[]{
			SigType.Number, SigType.Number
		}),
	};

	public static void register(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue round(ExpressionValue[] args) {
		var original = args[0];
		switch (original.getType()) {
			case Number_Int:
				return original;
			case Number_Double:
				return new IntegerValue((int) Math.round(original.asDouble()));
			default:
				throw new UnreachableCodeBlockException();
		}
	}

	public static ExpressionValue floor(ExpressionValue[] args) {
		var original = args[0];
		switch (original.getType()) {
			case Number_Int:
				return original;
			case Number_Double:
				return new IntegerValue((int) Math.floor(original.asDouble()));
			default:
				throw new UnreachableCodeBlockException();
		}
	}

	public static ExpressionValue ceil(ExpressionValue[] args) {
		var original = args[0];
		switch (original.getType()) {
			case Number_Int:
				return original;
			case Number_Double:
				return new IntegerValue((int) Math.ceil(original.asDouble()));
			default:
				throw new UnreachableCodeBlockException();
		}
	}

	public static ExpressionValue abs(ExpressionValue[] args) {
		var original = args[0];
		switch (original.getType()) {
			case Number_Int:
				return new IntegerValue(Math.abs(original.asInteger()));
			case Number_Double:
				return new DoubleValue(Math.abs(original.asDouble()));
			default:
				throw new UnreachableCodeBlockException();
		}
	}

	public static ExpressionValue max(ExpressionValue[] args) {
		var num1 = args[0];
		var num2 = args[1];
		if (num1.getType() == DataType.Number_Int && num2.getType() == DataType.Number_Int) {
			return new IntegerValue(Math.max(num1.asInteger(), num2.asInteger()));
		} else {
			return new DoubleValue(Math.max(num1.asDouble(), num2.asDouble()));
		}
	}

	public static ExpressionValue min(ExpressionValue[] args) {
		var num1 = args[0];
		var num2 = args[1];
		if (num1.getType() == DataType.Number_Int && num2.getType() == DataType.Number_Int) {
			return new IntegerValue(Math.min(num1.asInteger(), num2.asInteger()));
		} else {
			return new DoubleValue(Math.min(num1.asDouble(), num2.asDouble()));
		}
	}
}
