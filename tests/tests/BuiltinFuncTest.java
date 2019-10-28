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
		// keys()
		{
			var out = runProgram("out = keys({\"foo\": 3, \"bar\": 5});");
			assertArrayEqualsUnordered(new ExpressionValue[]{
				new StringValue("foo"),
				new StringValue("bar")
			}, out);
		}

		// values()
		{
			var out = runProgram("out = values({\"foo\": 3, \"bar\": 5});");
			assertArrayEqualsUnordered(new ExpressionValue[]{
				new IntegerValue(3),
				new IntegerValue(5)
			}, out);
		}

		// pairs()
		{
			var out = runProgram("out = pairs({\"foo\": 3, \"bar\": 5});");
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
		}

		// subDict()
		{
			var out = runProgram("out = subDict({\"foo\": 3, \"bar\": 5}, [\"foo\", \"baz\"]);");
			assertEquals(new DictionaryValue(Map.of(new StringValue("foo"), new IntegerValue(3))), out);
		}

		// contains()
		{
			var out = runProgram("out = contains({\"foo\": 3, \"bar\": 5}, \"foo\");");
			assertEquals(new BooleanValue(true), out);
			out = runProgram("out = contains({\"foo\": 3, \"bar\": 5}, \"baz\");");
			assertEquals(new BooleanValue(false), out);
		}
	}
}
