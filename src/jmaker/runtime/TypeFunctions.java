package jmaker.runtime;

import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.FunctionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Memory;
import jmaker.runtime.NativeFunction.SigType;

public class TypeFunctions {
	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("isBool", TypeFunctions::isBool, new SigType[]{
			SigType.Any
		}),
		new NativeFunction("isInteger", TypeFunctions::isInteger, new SigType[]{
			SigType.Any
		}),
		new NativeFunction("isDouble", TypeFunctions::isDouble, new SigType[]{
			SigType.Any
		}),
		new NativeFunction("isString", TypeFunctions::isString, new SigType[]{
			SigType.Any
		}),
		new NativeFunction("isArray", TypeFunctions::isArray, new SigType[]{
			SigType.Any
		}),
		new NativeFunction("isDict", TypeFunctions::isDict, new SigType[]{
			SigType.Any
		}),
		new NativeFunction("isFunction", TypeFunctions::isFunction, new SigType[]{
			SigType.Any
		}),
		new NativeFunction("parseInt", TypeFunctions::parseInt, new SigType[]{
			SigType.String
		}),
		new NativeFunction("parseDouble", TypeFunctions::parseDouble, new SigType[]{
			SigType.String
		}),
		new NativeFunction("call", TypeFunctions::callFunction, new SigType[]{
			SigType.Function, SigType.Array
		})
	};

	public static void registerAll(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue isBool(ExpressionValue[] args) {
		return new BooleanValue(args[0].getType() == DataType.Boolean);
	}

	public static ExpressionValue isInteger(ExpressionValue[] args) {
		return new BooleanValue(args[0].getType() == DataType.Number_Int);
	}

	public static ExpressionValue isDouble(ExpressionValue[] args) {
		return new BooleanValue(args[0].getType() == DataType.Number_Double);
	}

	public static ExpressionValue isString(ExpressionValue[] args) {
		return new BooleanValue(args[0].getType() == DataType.String);
	}

	public static ExpressionValue isArray(ExpressionValue[] args) {
		return new BooleanValue(args[0].getType() == DataType.Array);
	}

	public static ExpressionValue isDict(ExpressionValue[] args) {
		return new BooleanValue(args[0].getType() == DataType.Dictionary);
	}

	public static ExpressionValue isFunction(ExpressionValue[] args) {
		return new BooleanValue(args[0].getType() == DataType.Function);
	}

	public static ExpressionValue parseInt(ExpressionValue[] args) {
		var arg = args[0];

		return new IntegerValue(Integer.parseInt(arg.toString()));
	}

	public static ExpressionValue parseDouble(ExpressionValue[] args) {
		var arg = args[0];

		return new DoubleValue(Double.parseDouble(arg.toString()));
	}

	public static ExpressionValue callFunction(ExpressionValue[] args) {
		var function = (FunctionValue) args[0];
		var functionArgs = ((ArrayValue) args[1]).elements;

		return function.call(functionArgs);
	}
}
