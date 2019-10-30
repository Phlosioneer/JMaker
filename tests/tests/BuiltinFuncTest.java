package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import java.util.ArrayList;
import java.util.Map;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.DictionaryValue;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Interpreter;
import jmaker.interpreter.StringValue;
import jmaker.parser.Lexer;
import jmaker.parser.Parser;

class BuiltinFuncTest {

	// Default outVar is "out".
	private static ExpressionValue runProgram(String code) {
		return runProgram(code, "out");
	}

	private static ExpressionValue runProgram(String code, String outVar) {
		var lexer = new Lexer(code);
		var parser = new Parser(lexer.scanAll());
		var interpreter = new Interpreter(parser.parseFile());
		interpreter.run();
		var ret = interpreter.memory.get(outVar);
		assertNotNull(ret, outVar + " wasn't defined");
		return ret;
	}

	private static ExpressionValue runProgram(String[] code) {
		return runProgram(code, "out");
	}

	private static ExpressionValue runProgram(String[] code, String outVar) {
		var builder = new StringBuilder();
		builder.append(code[0]);
		for (int i = 1; i < code.length; i++) {
			builder.append('\n');
			builder.append(code[i]);
		}

		return runProgram(builder.toString(), outVar);
	}

	private static void assertArrayEqualsUnordered(ExpressionValue[] expected, ExpressionValue actual) {
		assertNotNull(actual);
		assertEquals(actual.getType(), DataType.Array);
		var inner = ((ArrayValue) actual).elements;
		assertArrayEqualsUnordered(expected, inner);
	}

	private static void assertArrayEqualsUnordered(ExpressionValue[] expected, ExpressionValue[] actual) {
		assert (expected != null);
		assertNotNull(actual);
		assertEquals(expected.length, actual.length);

		var foundExpectedElements = new boolean[expected.length];
		var foundActualElements = new boolean[actual.length];
		for (int i = 0; i < foundExpectedElements.length; i++) {
			foundExpectedElements[i] = false;
			foundActualElements[i] = false;
		}

		for (int i = 0; i < actual.length; i++) {
			for (int j = 0; j < expected.length; j++) {
				if (foundExpectedElements[j]) {
					continue;
				}
				if (expected[j].equals(actual[i])) {
					foundExpectedElements[j] = true;
					foundActualElements[i] = true;
					break;
				}
			}
		}

		// Did we find everything?
		boolean foundAll = true;
		for (var flag : foundExpectedElements) {
			if (!flag) {
				foundAll = false;
			}
		}
		if (foundAll) {
			return;
		}

		// Figure out the difference.
		var missingElements = new ArrayList<ExpressionValue>();
		var unexpectedElements = new ArrayList<ExpressionValue>();
		for (int i = 0; i < expected.length; i++) {
			if (!foundExpectedElements[i]) {
				missingElements.add(expected[i]);
			}
			if (!foundActualElements[i]) {
				unexpectedElements.add(actual[i]);
			}
		}

		var message = new StringBuilder();
		message.append("Expected elements missing: ");
		message.append(missingElements);
		message.append(", unexpected elements found: ");
		message.append(unexpectedElements);
		fail(message.toString());
	}

	@Test
	void testTypeFunctions() {
		ExpressionValue out;

		out = runProgram("out = isBool(true);");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isBool(5);");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = isInteger(5);");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isInteger(5.0);");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = isDouble(5.0);");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isDouble(5);");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = isString(\"5\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isString(5);");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = isArray([5, 0.1]);");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isArray(true);");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = isDict({});");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isDict([]);");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = isFunction(isDict);");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = isFunction(4);");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = parseInt(\"4\");");
		assertEquals(new IntegerValue(4), out);
		out = runProgram("out = parseInt(\"-432\");");
		assertEquals(new IntegerValue(-432), out);

		out = runProgram("out = parseDouble(\"4.2\");");
		assertEquals(new DoubleValue(4.2), out);
		out = runProgram("out = parseDouble(\"624\");");
		assertEquals(new DoubleValue(624), out);

		out = runProgram("out = call(isDouble, [4.0]);");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = call(parseInt, [\"111\"]);");
		assertEquals(new IntegerValue(111), out);
		out = runProgram("out = call(call, [parseDouble, [\"3.14\"]]);");
		assertEquals(new DoubleValue(3.14), out);
	}

	@Test
	void testTypeFunctionBadArgs() {
		assertThrows(RuntimeException.class, ()->runProgram("parseInt(4);"));
		assertThrows(RuntimeException.class, ()->runProgram("parseDouble(4);"));
	}

	@Test
	void testDictFunctions() {
		ExpressionValue out;
		// keys()
		out = runProgram("out = keys({\"foo\": 3, \"bar\": 5});");
		assertArrayEqualsUnordered(new ExpressionValue[]{
			new StringValue("foo"),
			new StringValue("bar")
		}, out);

		// values()
		out = runProgram("out = values({\"foo\": 3, \"bar\": 5});");
		assertArrayEqualsUnordered(new ExpressionValue[]{
			new IntegerValue(3),
			new IntegerValue(5)
		}, out);

		// pairs()
		out = runProgram("out = pairs({\"foo\": 3, \"bar\": 5});");
		assertArrayEqualsUnordered(new ExpressionValue[]{
			new ArrayValue(new ExpressionValue[]{
				new StringValue("foo"),
				new IntegerValue(3)
			}),
			new ArrayValue(new ExpressionValue[]{
				new StringValue("bar"),
				new IntegerValue(5)
			})
		}, out);

		// subDict()
		out = runProgram("out = subDict({\"foo\": 3, \"bar\": 5}, [\"foo\", \"baz\"]);");
		assertEquals(new DictionaryValue(Map.of(new StringValue("foo"), new IntegerValue(3))), out);

		// contains()
		out = runProgram("out = contains({\"foo\": 3, \"bar\": 5}, \"foo\");");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = contains({\"foo\": 3, \"bar\": 5}, \"baz\");");
		assertEquals(new BooleanValue(false), out);
	}

	@Test
	void testMultiTypeFunctions_length() {
		ExpressionValue out;

		// length()
		out = runProgram("out = length(\"foo\");");
		assertEquals(new IntegerValue(3), out);
		out = runProgram("out = length(\"\");");
		assertEquals(new IntegerValue(0), out);
		out = runProgram("out = length([3, \"bar\", [-1.1, true], 0, false]);");
		assertEquals(new IntegerValue(5), out);
		out = runProgram("out = length([]);");
		assertEquals(new IntegerValue(0), out);
		out = runProgram("out = length({0: \"foo\", 2: \"bar\", 4: \"baz\"});");
		assertEquals(new IntegerValue(3), out);
		out = runProgram("out = length({});");
		assertEquals(new IntegerValue(0), out);
	}

	@Test
	void testMultiTypeFunctions_set() {
		ExpressionValue out;

		out = runProgram("out = set([0, 1, 2], 1, \"foo\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(0),
			new StringValue("foo"),
			new IntegerValue(2)
		}), out);
		out = runProgram("out = set([], 0, \"append\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new StringValue("append")
		}), out);
		out = runProgram("out = set({\"foo\": false, \"bar\": true}, \"bar\", 3);");
		assertEquals(new DictionaryValue(Map.of(
				//
				new StringValue("foo"), new BooleanValue(false),
				//
				new StringValue("bar"), new IntegerValue(3))), out);
		out = runProgram("out = set({}, \"bar\", 3);");
		assertEquals(new DictionaryValue(Map.of(new StringValue("bar"), new IntegerValue(3))), out);
	}

	@Test
	void testMultiTypeFunctions_remove() {
		ExpressionValue out;

		out = runProgram("out = remove([2, 3, 4], 0);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(3),
			new IntegerValue(4)
		}), out);
		out = runProgram("out = remove([2, 3, 4], 2);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(2),
			new IntegerValue(3)
		}), out);
		out = runProgram("out = remove({\"foo\": \"bar\", \"bar\": true}, \"bar\");");
		assertEquals(new DictionaryValue(Map.of(new StringValue("foo"), new StringValue("bar"))), out);
	}

	@Test
	void testMultiTypeFunctions_range() {
		ExpressionValue out;

		// Start only
		out = runProgram("out = range(\"foobar\", 3);");
		assertEquals(new StringValue("bar"), out);
		out = runProgram("out = range([2, 3, 5, 7, 11, 13], 3);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(7),
			new IntegerValue(11),
			new IntegerValue(13)
		}), out);

		// Start and End
		out = runProgram("out = range(\"foobar\", 2, 5);");
		assertEquals(new StringValue("oba"), out);
		out = runProgram("out = range([2, 3, 5, 7, 11, 13], 2, 5);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(5),
			new IntegerValue(7),
			new IntegerValue(11)
		}), out);

		// Start past end of string/array
		out = runProgram("out = range(\"foobar\", 8);");
		assertEquals(new StringValue(""), out);
		out = runProgram("out = range([2, 3, 5, 7, 11, 13], 8);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);

		// - Start and end past end of string/array
		out = runProgram("out = range(\"foobar\", 8, 20);");
		assertEquals(new StringValue(""), out);
		out = runProgram("out = range([2, 3, 5, 7, 11, 13], 8, 20);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);

		// End past end of string/array
		out = runProgram("out = range(\"foobar\", 2, 20);");
		assertEquals(new StringValue("obar"), out);
		out = runProgram("out = range([2, 3, 5, 7, 11, 13], 2, 20);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(5),
			new IntegerValue(7),
			new IntegerValue(11),
			new IntegerValue(13)
		}), out);

		// Start = 0
		out = runProgram("out = range(\"foobar\", 0, 3);");
		assertEquals(new StringValue("foo"), out);
		out = runProgram("out = range([2, 3, 5, 7, 11, 13], 0, 3);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(2),
			new IntegerValue(3),
			new IntegerValue(5)
		}), out);

		// Range length = 0
		out = runProgram("out = range(\"foobar\", 2, 2);");
		assertEquals(new StringValue(""), out);
		out = runProgram("out = range([2, 3, 5, 7, 11, 13], 2, 2);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);
	}

	@Test
	void testMultiTypeFunctions_find() {
		ExpressionValue out;

		out = runProgram("out = find(\"the quick brown fox jumped over the lazy dog.\", \"fox\");");
		assertEquals(new IntegerValue(16), out);
		out = runProgram("out = find(\"the quick brown fox jumped over the lazy dog.\", \"the\");");
		assertEquals(new IntegerValue(0), out);
		out = runProgram("out = find(\"the quick brown fox jumped over the lazy dog.\", \"rabbit\");");
		assertEquals(new IntegerValue(-1), out);
		out = runProgram("out = find(\"the quick brown fox jumped over the lazy dog.\", \"the\", 5);");
		assertEquals(new IntegerValue(32), out);
		out = runProgram("out = find(\"the quick brown fox jumped over the lazy dog.\", \"the\", 5, 15);");
		assertEquals(new IntegerValue(-1), out);
		out = runProgram("out = find(\"foobar\", \"foo\", 10);");
		assertEquals(new IntegerValue(-1), out);
		out = runProgram("out = find(\"foobar\", \"bar\", 0, 4);");
		assertEquals(new IntegerValue(3), out);
		out = runProgram("out = find(\"foobar\", \"bar\", 0, 3);");
		assertEquals(new IntegerValue(-1), out);

		out = runProgram("out = find([4, true, false, 81, 9.0, true], false);");
		assertEquals(new IntegerValue(2), out);
		out = runProgram("out = find([4, true, false, 81, 9.0, true], true);");
		assertEquals(new IntegerValue(1), out);
		out = runProgram("out = find([4, true, false, 81, 9.0, \"baz\"], \"foo\");");
		assertEquals(new IntegerValue(-1), out);
		out = runProgram("out = find([4, true, false, 81, 9.0, true], true, 3);");
		assertEquals(new IntegerValue(5), out);
		out = runProgram("out = find([4, true, false, 81, 9.0, true], true, 3, 5);");
		assertEquals(new IntegerValue(-1), out);
		out = runProgram("out = find([\"foo\"], \"foo\", 8);");
		assertEquals(new IntegerValue(-1), out);
	}

	@Test
	void testMultiTypeFunctions_findAll() {
		ExpressionValue out;

		// Strings
		out = runProgram("out = findAll(\"the quick brown fox jumped over the lazy dog.\", \"fox\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(16)
		}), out);
		out = runProgram("out = findAll(\"the quick brown fox jumped over the lazy dog.\", \"the\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(0),
			new IntegerValue(32)
		}), out);
		out = runProgram("out = findAll(\"the quick brown fox jumped over the lazy dog.\", \"rabbit\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);
		out = runProgram("out = findAll(\"the quick brown fox jumped over the lazy dog.\", \"the\", 5);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(32)
		}), out);
		out = runProgram("out = findAll(\"the quick brown fox jumped over the lazy dog.\", \"the\", 5, 15);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);
		out = runProgram("out = findAll(\"foobar\", \"foo\", 10);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);
		out = runProgram("out = findAll(\"foobar\", \"bar\", 0, 4);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(3)
		}), out);
		out = runProgram("out = findAll(\"foobar\", \"bar\", 0, 3);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);

		// Arrays
		out = runProgram("out = findAll([4, true, false, 81, 9.0, true], false);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(2)
		}), out);
		out = runProgram("out = findAll([4, true, false, 81, 9.0, true], true);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(1),
			new IntegerValue(5)
		}), out);
		out = runProgram("out = findAll([4, true, false, 81, 9.0, \"baz\"], \"foo\");");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);
		out = runProgram("out = findAll([4, true, false, 81, 9.0, true], true, 3);");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(5)
		}), out);
		out = runProgram("out = findAll([4, true, false, 81, 9.0, true], true, 3, 5);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);
		out = runProgram("out = findAll([\"foo\"], \"foo\", 8);");
		assertEquals(new ArrayValue(new ExpressionValue[]{}), out);
	}
}
