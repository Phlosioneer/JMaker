package jmaker.runtime;

import java.util.Locale;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.Memory;
import jmaker.interpreter.StringValue;

public class StringFunctions {

	private static NativeFunction[] functions = new NativeFunction[]{
		new NativeFunction("toLower", StringFunctions::toLower),
		new NativeFunction("toUpper", StringFunctions::toUpper),
		new NativeFunction("isLower", StringFunctions::isLower),
		new NativeFunction("isUpper", StringFunctions::isUpper),
		new NativeFunction("isAlphabetic", StringFunctions::isAlphabetic),
		new NativeFunction("isNumeric", StringFunctions::isNumeric),
		new NativeFunction("isWhitespace", StringFunctions::isWhitespace),
		new NativeFunction("isAscii", StringFunctions::isAscii),
		new NativeFunction("trim", StringFunctions::trim),
		new NativeFunction("replaceRange", StringFunctions::replaceRange),
		new NativeFunction("replaceFirst", StringFunctions::replaceFirst),
		new NativeFunction("replaceAll", StringFunctions::replaceAll),
		new NativeFunction("split", StringFunctions::split)
	};

	public static void registerAll(Memory memory) {
		for (var func : functions) {
			memory.set(func.symbolName, func);
		}
	}

	public static ExpressionValue toLower(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		// This function only changes ASCII characters. English == Ascii, at least for
		// upper/lower case stuff.
		return new StringValue(original.toString().toLowerCase(Locale.ENGLISH));
	}

	public static ExpressionValue toUpper(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		// This function only changes ASCII characters. English == Ascii, at least for
		// upper/lower case stuff.
		return new StringValue(original.toString().toUpperCase(Locale.ENGLISH));
	}

	public static ExpressionValue isLower(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		for (char c : original.toString().toCharArray()) {
			if (Character.isUpperCase(c)) {
				return new BooleanValue(false);
			}
		}
		return new BooleanValue(true);
	}

	public static ExpressionValue isUpper(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		for (char c : original.toString().toCharArray()) {
			if (Character.isLowerCase(c)) {
				return new BooleanValue(false);
			}
		}
		return new BooleanValue(true);
	}

	public static ExpressionValue isAlphabetic(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		for (char c : original.toString().toCharArray()) {
			if (!Character.isAlphabetic(c)) {
				return new BooleanValue(false);
			}
		}
		return new BooleanValue(true);
	}

	public static ExpressionValue isNumeric(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		for (char c : original.toString().toCharArray()) {
			if (!Character.isDigit(c)) {
				return new BooleanValue(false);
			}
		}
		return new BooleanValue(true);
	}

	public static ExpressionValue isWhitespace(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		for (char c : original.toString().toCharArray()) {
			if (!Character.isWhitespace(c)) {
				return new BooleanValue(false);
			}
		}
		return new BooleanValue(true);
	}

	public static ExpressionValue isAscii(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		for (char c : original.toString().toCharArray()) {
			int codePoint = c;
			int unicodePart = codePoint >> 7;

			if (unicodePart != 0) {
				return new BooleanValue(false);
			}
		}
		return new BooleanValue(true);
	}

	public static ExpressionValue trim(ExpressionValue[] args) {
		if (args.length != 1) {
			throw new ArgCountException(1, args.length);
		}

		var original = args[0];
		if (original.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		return new StringValue(original.toString().trim());
	}

	public static ExpressionValue replaceRange(ExpressionValue[] args) {
		if (args.length != 4) {
			throw new ArgCountException(4, args.length);
		}

		var original = args[0];
		var replacement = args[1];
		var startIndex = args[2];
		var endIndex = args[3];

		if (original.getType() != DataType.String || replacement.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}
		if (startIndex.getType() != DataType.Number_Int || endIndex.getType() != DataType.Number_Int) {
			throw new ArgTypeException(args);
		}

		var originalString = original.toString();
		var replacementString = replacement.toString();
		var startIndexInt = startIndex.asInteger();
		var endIndexInt = endIndex.asInteger();

		if (startIndexInt < 0) {
			throw new RuntimeException("Start index cannot be negative (" + startIndexInt + ")");
		}
		if (endIndexInt < startIndexInt) {
			throw new RuntimeException("End index cannot be before the start index (" + endIndexInt + ")");
		}
		if (endIndexInt > originalString.length()) {
			throw new RuntimeException("End index cannot be after the end of the string (" + endIndexInt + ")");
		}

		var newString = new StringBuilder();
		newString.append(originalString.substring(0, startIndexInt));
		newString.append(replacementString);
		newString.append(originalString.substring(endIndexInt));
		return new StringValue(newString.toString());
	}

	public static ExpressionValue replaceFirst(ExpressionValue[] args) {
		if (args.length != 3) {
			throw new ArgCountException(4, args.length);
		}

		var original = args[0];
		var pattern = args[1];
		var replacement = args[2];

		if (original.getType() != DataType.String || pattern.getType() != DataType.String || replacement.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var originalString = original.toString();
		var patternString = pattern.toString();
		var replacementString = replacement.toString();

		var newString = originalString.replaceFirst(patternString, replacementString);

		return new StringValue(newString);
	}

	public static ExpressionValue replaceAll(ExpressionValue[] args) {
		if (args.length != 3) {
			throw new ArgCountException(4, args.length);
		}

		var original = args[0];
		var pattern = args[1];
		var replacement = args[2];

		if (original.getType() != DataType.String || pattern.getType() != DataType.String || replacement.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var originalString = original.toString();
		var patternString = pattern.toString();
		var replacementString = replacement.toString();

		var newString = originalString.replaceAll(patternString, replacementString);

		return new StringValue(newString);
	}

	public static ExpressionValue split(ExpressionValue[] args) {
		if (args.length != 2) {
			throw new ArgCountException(2, args.length);
		}

		var original = args[0];
		var pattern = args[1];

		if (original.getType() != DataType.String || pattern.getType() != DataType.String) {
			throw new ArgTypeException(args);
		}

		var originalString = original.toString();
		var patternString = pattern.toString();

		var splitStrings = originalString.split(patternString);
		var convertedArray = new ExpressionValue[splitStrings.length];
		for (int i = 0; i < splitStrings.length; i++) {
			convertedArray[i] = new StringValue(splitStrings[i]);
		}
		return new ArrayValue(convertedArray);
	}
}
