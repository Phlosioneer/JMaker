package jmaker.runtime;

import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.FunctionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Memory;

public class TypeFunctions {
	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("isBool", TypeFunctions::isBool),
		new NativeFunction("isInteger", TypeFunctions::isInteger),
		new NativeFunction("isDouble", TypeFunctions::isDouble),
		new NativeFunction("isString", TypeFunctions::isString),
		new NativeFunction("isArray", TypeFunctions::isArray),
		new NativeFunction("isDict", TypeFunctions::isDict),
		new NativeFunction("isFunction", TypeFunctions::isFunction),
		new NativeFunction("parseInt", TypeFunctions::parseInt),
		new NativeFunction("parseDouble", TypeFunctions::parseDouble),
		new NativeFunction("call", TypeFunctions::callFunction)
	};

	public static void registerAll(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue isBool(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		return new BooleanValue(args[0].getType() == DataType.Boolean);
	}

	public static ExpressionValue isInteger(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		return new BooleanValue(args[0].getType() == DataType.Number_Int);
	}

	public static ExpressionValue isDouble(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		return new BooleanValue(args[0].getType() == DataType.Number_Double);
	}

	public static ExpressionValue isString(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		return new BooleanValue(args[0].getType() == DataType.String);
	}

	public static ExpressionValue isArray(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		return new BooleanValue(args[0].getType() == DataType.Array);
	}

	public static ExpressionValue isDict(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		return new BooleanValue(args[0].getType() == DataType.Dictionary);
	}

	public static ExpressionValue isFunction(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		return new BooleanValue(args[0].getType() == DataType.Function);
	}

	public static ExpressionValue parseInt(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var arg = args[0];

		if (arg.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		return new IntegerValue(Integer.parseInt(arg.toString()));
	}

	public static ExpressionValue parseDouble(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var arg = args[0];

		if (arg.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		return new DoubleValue(Double.parseDouble(arg.toString()));
	}

	public static ExpressionValue callFunction(ExpressionValue[] args) {
		if (args.length != 2) {
			throw new ArgCountException(2, args.length);
		}

		var functionExpr = args[0];
		var functionArgsExpr = args[1];

		if (functionExpr.getType() != DataType.Function || functionArgsExpr.getType() != DataType.Array) {
			throw new ArgTypeException(args);
		}
		var function = (FunctionValue) functionExpr;
		var functionArgs = ((ArrayValue) functionArgsExpr).elements;

		return function.call(functionArgs);
	}
}
