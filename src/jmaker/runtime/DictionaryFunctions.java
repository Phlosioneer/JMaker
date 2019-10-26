package jmaker.runtime;

import java.util.HashMap;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.DictionaryValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.Memory;

public class DictionaryFunctions {

	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("keys", DictionaryFunctions::keys),
		new NativeFunction("values", DictionaryFunctions::values),
		new NativeFunction("pairs", DictionaryFunctions::pairs),
		new NativeFunction("subDict", DictionaryFunctions::subDict),
		new NativeFunction("contains", DictionaryFunctions::contains)
	};

	public void registerAll(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue keys(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var originalExpr = args[0];
		if (originalExpr.getType() != DataType.Dictionary) {
			throw new ArgTypeException(args);
		}

		var original = ((DictionaryValue) originalExpr).elements;
		var keys = new ExpressionValue[original.size()];
		var currentIndex = 0;
		for (var key : original.keySet()) {
			keys[currentIndex] = key;
			currentIndex += 1;
		}

		return new ArrayValue(keys);
	}

	public static ExpressionValue values(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var originalExpr = args[0];
		if (originalExpr.getType() != DataType.Dictionary) {
			throw new ArgTypeException(args);
		}

		var original = ((DictionaryValue) originalExpr).elements;
		var values = new ExpressionValue[original.size()];
		var currentIndex = 0;
		for (var value : original.values()) {
			values[currentIndex] = value;
			currentIndex += 1;
		}

		return new ArrayValue(values);
	}

	public static ExpressionValue pairs(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var originalExpr = args[0];
		if (originalExpr.getType() != DataType.Dictionary) {
			throw new ArgTypeException(args);
		}

		var original = ((DictionaryValue) originalExpr).elements;
		var pairs = new ExpressionValue[original.size()];
		var currentIndex = 0;
		for (var pair : original.entrySet()) {
			pairs[currentIndex] = new ArrayValue(new ExpressionValue[]{
				pair.getKey(),
				pair.getValue()
			});
			currentIndex += 1;
		}

		return new ArrayValue(pairs);
	}

	public static ExpressionValue subDict(ExpressionValue[] args) {
		if (args.length != 2) {
			throw new ArgCountException(2, args.length);
		}

		var originalExpr = args[0];
		var keysExpr = args[1];
		if (originalExpr.getType() != DataType.Dictionary || keysExpr.getType() != DataType.Array) {
			throw new ArgTypeException(args);
		}

		var original = ((DictionaryValue) originalExpr).elements;
		var keys = ((ArrayValue) keysExpr).elements;
		var ret = new HashMap<ExpressionValue, ExpressionValue>(keys.length);
		for (var key : keys) {
			var value = original.get(key);
			if (value != null) {
				ret.put(key, value);
			}
		}

		return new DictionaryValue(ret);
	}

	public static ExpressionValue contains(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var originalExpr = args[0];
		if (originalExpr.getType() != DataType.Dictionary) {
			throw new ArgTypeException(args);
		}

		var original = ((DictionaryValue) originalExpr).elements;
		return new BooleanValue(original != null);
	}
}
