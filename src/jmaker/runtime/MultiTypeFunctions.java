package jmaker.runtime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.DictionaryValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Memory;
import jmaker.interpreter.StringValue;
import jmaker.runtime.NativeFunction.SigType;

public class MultiTypeFunctions {

	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("length", MultiTypeFunctions::length, new SigType[][]{
			new SigType[]{
				SigType.String
			},
			new SigType[]{
				SigType.Array
			},
			new SigType[]{
				SigType.Dictionary
			}
		}),
		new NativeFunction("set", MultiTypeFunctions::set, new SigType[][]{
			new SigType[]{
				SigType.Array, SigType.Integer, SigType.Any
			},
			new SigType[]{
				SigType.Dictionary, SigType.Any, SigType.Any
			}
		}),
		new NativeFunction("remove", MultiTypeFunctions::remove, new SigType[][]{
			new SigType[]{
				SigType.Array, SigType.Integer
			},
			new SigType[]{
				SigType.Dictionary, SigType.Any
			}
		}),
		new NativeFunction("range", MultiTypeFunctions::range, new SigType[][]{
			new SigType[]{
				SigType.String, SigType.Integer
			},
			new SigType[]{
				SigType.String, SigType.Integer, SigType.Integer
			},
			new SigType[]{
				SigType.Array, SigType.Integer
			},
			new SigType[]{
				SigType.Array, SigType.Integer, SigType.Integer
			}
		}),
		new NativeFunction("find", MultiTypeFunctions::find, new SigType[][]{
			new SigType[]{
				SigType.Array, SigType.Any
			},
			new SigType[]{
				SigType.Array, SigType.Any, SigType.Integer
			},
			new SigType[]{
				SigType.Array, SigType.Any, SigType.Integer, SigType.Integer
			},
			new SigType[]{
				SigType.String, SigType.String
			},
			new SigType[]{
				SigType.String, SigType.String, SigType.Integer
			},
			new SigType[]{
				SigType.String, SigType.String, SigType.Integer, SigType.Integer
			}
		}),
		new NativeFunction("findAll", MultiTypeFunctions::findAll, new SigType[][]{
			new SigType[]{
				SigType.Array, SigType.Any
			},
			new SigType[]{
				SigType.Array, SigType.Any, SigType.Integer
			},
			new SigType[]{
				SigType.Array, SigType.Any, SigType.Integer, SigType.Integer
			},
			new SigType[]{
				SigType.String, SigType.String
			},
			new SigType[]{
				SigType.String, SigType.String, SigType.Integer
			},
			new SigType[]{
				SigType.String, SigType.String, SigType.Integer, SigType.Integer
			}
		})
	};

	public static void registerAll(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	// Shared by strings, arrays, and dicts
	public static ExpressionValue length(ExpressionValue[] args) {
		ExpressionValue arg = args[0];
		switch (arg.getType()) {
			case String:
				return new IntegerValue(arg.toString().length());
			case Array:
				var castToArray = (ArrayValue) arg;
				return new IntegerValue(castToArray.elements.length);
			case Dictionary:
				var castToDict = (DictionaryValue) arg;
				return new IntegerValue(castToDict.elements.size());
			default:
				throw new RuntimeException();
		}
	}

	// Shared by arrays and dicts
	public static ExpressionValue set(ExpressionValue[] args) {
		var originalExpr = args[0];
		var indexExpr = args[1];
		var newValue = args[2];

		if (originalExpr.getType() == DataType.Array) {
			var original = ((ArrayValue) originalExpr).elements;
			var index = indexExpr.asInteger();

			if (index < 0) {
				throw new RuntimeException("Index cannot be negative (" + index + ")");
			}
			if (index > original.length) {
				throw new RuntimeException("Index cannot be greater than the array length (" + index + ")");
			}

			ExpressionValue[] ret;
			if (index == original.length) {
				// Special case: append the item to the array.
				ret = Arrays.copyOf(original, original.length + 1);
			} else {
				ret = Arrays.copyOf(original, original.length);
			}

			ret[index] = newValue;

			return new ArrayValue(ret);
		} else if (originalExpr.getType() == DataType.Dictionary) {
			var original = ((DictionaryValue) originalExpr).elements;
			var ret = new HashMap<>(original);
			ret.put(indexExpr, newValue);
			return new DictionaryValue(ret);
		} else {
			throw new ArgTypeException(args);
		}
	}

	// Shared by arrays and dicts.
	public static ExpressionValue remove(ExpressionValue[] args) {
		var originalExpr = args[0];
		var indexExpr = args[1];

		if (originalExpr.getType() == DataType.Array) {
			var original = ((ArrayValue) originalExpr).elements;
			var index = indexExpr.asInteger();

			if (index < 0) {
				throw new RuntimeException("Index cannot be negative (" + index + ")");
			}
			if (index >= original.length) {
				throw new RuntimeException("Index must be smaller than the array length (" + index + ")");
			}

			if (index == original.length - 1) {
				// Special case: truncate the array while copying it.
				return new ArrayValue(Arrays.copyOf(original, original.length - 1));
			}

			var ret = new ExpressionValue[original.length - 1];
			System.arraycopy(original, 0, ret, 0, index);
			System.arraycopy(original, index + 1, ret, index, original.length - 1 - index);

			return new ArrayValue(ret);
		} else if (originalExpr.getType() == DataType.Dictionary) {
			var original = ((DictionaryValue) originalExpr).elements;
			var ret = new HashMap<>(original);
			ret.remove(indexExpr);
			return new DictionaryValue(ret);
		} else {
			throw new ArgTypeException(args);
		}
	}

	// Shared by arrays and strings.
	public static ExpressionValue range(ExpressionValue[] args) {
		var original = args[0];
		var start = args[1];

		// Check the start index.
		var startInt = start.asInteger();
		if (startInt < 0) {
			throw new RuntimeException("Start index cannot be negative (" + startInt + ")");
		}

		// Check the end index, if it was provided.
		int endInt;
		if (args.length <= 2) {
			endInt = Integer.MAX_VALUE;
		} else {
			endInt = args[2].asInteger();
		}
		if (endInt < startInt) {
			throw new RuntimeException("End index cannot be before the start index (" + endInt + ")");
		}

		// Actual range code.
		if (original.getType() == DataType.String) {
			var originalString = original.toString();
			if (startInt == endInt || startInt >= originalString.length()) {
				return new StringValue("");
			}

			if (endInt > originalString.length()) {
				return new StringValue(originalString.substring(startInt));
			} else {
				return new StringValue(originalString.substring(startInt, endInt));
			}
		} else if (original.getType() == DataType.Array) {
			var originalArray = (ArrayValue) original;
			if (startInt == endInt || startInt >= originalArray.elements.length) {
				return new ArrayValue(new ExpressionValue[]{});
			}

			if (endInt > originalArray.elements.length) {
				endInt = originalArray.elements.length;
			}
			var newArray = Arrays.copyOfRange(originalArray.elements, startInt, endInt);
			return new ArrayValue(newArray);
		} else {
			throw new ArgTypeException(args);
		}
	}

	// Shared by arrays and strings.
	public static ExpressionValue find(ExpressionValue[] args) {
		var range = validateFindRange(args);
		var original = args[0];
		var target = args[1];

		if (original.getType() == DataType.String) {
			var originalString = original.toString();
			var targetString = target.toString();

			if (range.start >= originalString.length()) {
				return new IntegerValue(-1);
			}
			var newStringStartIndex = (originalString.indexOf(targetString, range.start));
			if (newStringStartIndex < 0) {
				return new IntegerValue(-1);
			}
			if (newStringStartIndex >= range.end) {
				return new IntegerValue(-1);
			}
			return new IntegerValue(newStringStartIndex);
		} else if (original.getType() == DataType.Array) {
			var originalArray = (ArrayValue) original;
			if (range.start >= originalArray.elements.length) {
				return new IntegerValue(-1);
			}
			if (range.end > originalArray.elements.length) {
				range.end = originalArray.elements.length;
			}
			for (int i = range.start; i < range.end; i++) {
				var currentElement = originalArray.elements[i];
				if (currentElement.equals(target)) {
					return new IntegerValue(i);
				}
			}
			return new IntegerValue(-1);
		} else {
			throw new ArgTypeException(args);
		}
	}

	// Shared by arrays and strings.
	public static ExpressionValue findAll(ExpressionValue[] args) {
		var range = validateFindRange(args);
		var original = args[0];
		var target = args[1];
		var foundIndecies = new ArrayList<Integer>();

		if (original.getType() == DataType.String) {
			var originalString = original.toString();
			var targetString = target.toString();

			if (range.start >= originalString.length()) {
				return new ArrayValue(new ExpressionValue[]{});
			}
			if (range.end > originalString.length()) {
				range.end = originalString.length();
			}
			var currentIndex = originalString.indexOf(targetString, range.start);
			while (currentIndex < range.end && currentIndex >= 0) {
				foundIndecies.add(currentIndex);
				currentIndex = originalString.indexOf(targetString, currentIndex + 1);
			}

		} else if (original.getType() == DataType.Array) {
			var originalArray = ((ArrayValue) original).elements;

			if (range.start >= originalArray.length) {
				return new ArrayValue(new ExpressionValue[]{});
			}
			if (range.end > originalArray.length) {
				range.end = originalArray.length;
			}
			for (int i = range.start; i < range.end; i++) {
				if (target.equals(originalArray[i])) {
					foundIndecies.add(i);
				}
			}

		} else {
			throw new ArgTypeException(args);
		}

		var ret = new ExpressionValue[foundIndecies.size()];
		for (int i = 0; i < foundIndecies.size(); i++) {
			ret[i] = new IntegerValue(foundIndecies.get(i));
		}
		return new ArrayValue(ret);
	}

	// Shared by arrays and strings.
	private static FindRangeStruct validateFindRange(ExpressionValue[] args) {
		// Check the start index, if provided.
		int startInt;
		if (args.length <= 2) {
			startInt = 0;
		} else {
			startInt = args[2].asInteger();
		}
		if (startInt < 0) {
			throw new RuntimeException("Start index cannot be negative (" + startInt + ")");
		}

		// Check the end index, if provided.
		int endInt;
		if (args.length <= 3) {
			endInt = Integer.MAX_VALUE;
		} else {
			endInt = args[3].asInteger();
		}

		return new FindRangeStruct(startInt, endInt);
	}

	private static class FindRangeStruct {
		public int start;
		public int end;

		public FindRangeStruct(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
}
