package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Interpreter;
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

	@Test
	void testTypeFunctions() {
		ExpressionValue out;

		out = runProgram("out = isBool(true);");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = isBool(5);");
		assertEquals(out, new BooleanValue(false));

		out = runProgram("out = isInteger(5);");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = isInteger(5.0);");
		assertEquals(out, new BooleanValue(false));

		out = runProgram("out = isDouble(5.0);");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = isDouble(5);");
		assertEquals(out, new BooleanValue(false));

		out = runProgram("out = isString(\"5\");");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = isString(5);");
		assertEquals(out, new BooleanValue(false));

		out = runProgram("out = isArray([5, 0.1]);");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = isArray(true);");
		assertEquals(out, new BooleanValue(false));

		out = runProgram("out = isDict({});");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = isDict([]);");
		assertEquals(out, new BooleanValue(false));

		out = runProgram("out = isFunction(isDict);");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = isFunction(4);");
		assertEquals(out, new BooleanValue(false));

		out = runProgram("out = parseInt(\"4\");");
		assertEquals(out, new IntegerValue(4));
		out = runProgram("out = parseInt(\"-432\");");
		assertEquals(out, new IntegerValue(-432));

		out = runProgram("out = parseDouble(\"4.2\");");
		assertEquals(out, new DoubleValue(4.2));
		out = runProgram("out = parseDouble(\"624\");");
		assertEquals(out, new DoubleValue(624));

		out = runProgram("out = call(isDouble, [4.0]);");
		assertEquals(out, new BooleanValue(true));
		out = runProgram("out = call(parseInt, [\"111\"]);");
		assertEquals(out, new IntegerValue(111));
		out = runProgram("out = call(call, [parseDouble, [\"3.14\"]]);");
		assertEquals(out, new DoubleValue(3.14));
	}

	@Test
	void testTypeFunctionBadArgs() {
		assertThrows(RuntimeException.class, ()->runProgram("parseInt(4);"));
		assertThrows(RuntimeException.class, ()->runProgram("parseDouble(4);"));
	}
}
