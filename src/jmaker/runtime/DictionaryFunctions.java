package jmaker.runtime;

import java.util.HashMap;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DictionaryValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.Memory;
import jmaker.runtime.NativeFunction.SigType;

public class DictionaryFunctions {

	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("keys", DictionaryFunctions::keys, new SigType[]{
			SigType.Dictionary
		}),
		new NativeFunction("values", DictionaryFunctions::values, new SigType[]{
			SigType.Dictionary
		}),
		new NativeFunction("pairs", DictionaryFunctions::pairs, new SigType[]{
			SigType.Dictionary
		}),
		new NativeFunction("subDict", DictionaryFunctions::subDict, new SigType[]{
			SigType.Dictionary, SigType.Array
		}),
		new NativeFunction("contains", DictionaryFunctions::contains, new SigType[]{
			SigType.Dictionary, SigType.Any
		})
	};

	public static void registerAll(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue keys(ExpressionValue[] args) {
		var original = ((DictionaryValue) args[0]).elements;

		var keys = new ExpressionValue[original.size()];
		var currentIndex = 0;
		for (var key : original.keySet()) {
			keys[currentIndex] = key;
			currentIndex += 1;
		}

		return new ArrayValue(keys);
	}

	public static ExpressionValue values(ExpressionValue[] args) {
		var original = ((DictionaryValue) args[0]).elements;

		var values = new ExpressionValue[original.size()];
		var currentIndex = 0;
		for (var value : original.values()) {
			values[currentIndex] = value;
			currentIndex += 1;
		}

		return new ArrayValue(values);
	}

	public static ExpressionValue pairs(ExpressionValue[] args) {
		var original = ((DictionaryValue) args[0]).elements;

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
		var original = ((DictionaryValue) args[0]).elements;

		var keys = ((ArrayValue) args[1]).elements;
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
		var original = ((DictionaryValue) args[0]).elements;
		var key = args[1];

		return new BooleanValue(original.containsKey(key));
	}
}
