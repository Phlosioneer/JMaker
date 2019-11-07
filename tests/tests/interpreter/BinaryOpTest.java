package tests.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tests.interpreter.TestUtil.runProgram;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DictionaryValue;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Interpreter;
import jmaker.interpreter.StringValue;
import jmaker.parser.Block;
import jmaker.parser.Statement;

class BinaryOpTest {

	Interpreter interp;

	@BeforeEach
	void initTest() {
		interp = new Interpreter(new Block(new Statement[]{}));
	}

	@Test
	void equalTest() {
		actualEqualTest(true, "==");
		actualEqualTest(false, "!=");
	}

	void actualEqualTest(boolean isEqual, String sign) {
		ExpressionValue out;
		var isEqualBool = new BooleanValue(isEqual);
		var notEqualBool = new BooleanValue(!isEqual);

		out = runProgram("out = 1 " + sign + " 1;");
		assertEquals(isEqualBool, out);
		out = runProgram("out = -1 " + sign + " 1;");
		assertEquals(notEqualBool, out);

		out = runProgram("out = true " + sign + " true;");
		assertEquals(isEqualBool, out);
		out = runProgram("out = true " + sign + " false;");
		assertEquals(notEqualBool, out);

		out = runProgram("out = 1.25 " + sign + " 1.25;");
		assertEquals(isEqualBool, out);
		out = runProgram("out = 14.2 " + sign + " 14.201;");
		assertEquals(notEqualBool, out);

		out = runProgram("out = \"foo\" " + sign + " \"foo\";");
		assertEquals(isEqualBool, out);
		out = runProgram("out = \"foo\" " + sign + " \"bar\";");
		assertEquals(notEqualBool, out);

		out = runProgram("out = [1, 2.3, true, \"baz\"] " + sign + " [1, 2.3, true, \"baz\"];");
		assertEquals(isEqualBool, out);
		out = runProgram("out = [1, 2.3, true, \"baz\"] " + sign + " [1];");
		assertEquals(notEqualBool, out);

		out = runProgram("out = {\"foo\": 2, \"bar\": true} " + sign + " {\"bar\": true, \"foo\": 2};");
		assertEquals(isEqualBool, out);
		out = runProgram("out = {\"foo\": 2} " + sign + " {\"bar\": true};");
		assertEquals(notEqualBool, out);

		out = runProgram("out = 1.0 " + sign + " 1;");
		assertEquals(isEqualBool, out);
		out = runProgram("out = 1 " + sign + " \"1\";");
		assertEquals(notEqualBool, out);
		out = runProgram("out = [] " + sign + " {};");
		assertEquals(notEqualBool, out);
		out = runProgram("out = 1.0 " + sign + " [1.0];");
		assertEquals(notEqualBool, out);
	}

	@Test
	void mathTest() {
		ExpressionValue out;

		out = runProgram("out = (7 + 3) + (2.1 - 14);");
		assertEquals(new DoubleValue(7 + 3 + 2.1 - 14), out);
		out = runProgram("out = 4 * 8 + 3 / 2.45 * -1;");
		assertEquals(new DoubleValue(4 * 8 + 3 / 2.45 * -1), out);
		out = runProgram("out = 34 * 14.1 - (18 - 3) * 1.4;");
		assertEquals(new DoubleValue(34 * 14.1 - (18 - 3) * 1.4), out);

		// Integer division
		out = runProgram("out = 7 / 2;");
		assertEquals(new IntegerValue(3), out);

	}

	@Test
	void comparisonTest() {
		ExpressionValue out;

		out = runProgram("out = 3 > 1;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = 23.1 > 23.01;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = -2 > 4;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = 8 > 8;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = 8.001 > 8.002;");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = 3 < 1;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = 23.1 < 23.01;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = -2 < 4;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = 8 < 8;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = 8.001 < 8.002;");
		assertEquals(new BooleanValue(true), out);

		out = runProgram("out = 3 >= 1;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = 23.1 >= 23.01;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = -2 >= 4;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = 8 >= 8;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = 8.001 >= 8.002;");
		assertEquals(new BooleanValue(false), out);

		out = runProgram("out = 3 <= 1;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = 23.1 <= 23.01;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = -2 <= 4;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = 8 <= 8;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = 8.001 <= 8.002;");
		assertEquals(new BooleanValue(true), out);
	}

	@Test
	void logicTest() {
		ExpressionValue out;

		out = runProgram("out = true && true;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = false && true;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = false && false;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = true || true;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = true || false;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = false || false;");
		assertEquals(new BooleanValue(false), out);
	}

	@Test
	void appendTest() {
		ExpressionValue out;

		// This is NOT a test of the implicit casting rules. That's tested elsewhere.
		out = runProgram("out = \"foo\" + \"bar\";");
		assertEquals(new StringValue("foobar"), out);
		out = runProgram("out = \"foo\" + true;");
		assertEquals(new StringValue("footrue"), out);
		out = runProgram("out = \"foo\" + 3;");
		assertEquals(new StringValue("foo3"), out);
		out = runProgram("out = \"\" + 2;");
		assertEquals(new StringValue("2"), out);

		out = runProgram("out = [1, 2] + 3;");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(1),
			new IntegerValue(2),
			new IntegerValue(3)
		}), out);
		out = runProgram("out = [8, true, 2.5] + \"foo\";");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(8),
			new BooleanValue(true),
			new DoubleValue(2.5),
			new StringValue("foo")
		}), out);
		out = runProgram("out = [4, 2] + [3, 1];");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(4),
			new IntegerValue(2),
			new IntegerValue(3),
			new IntegerValue(1)
		}), out);
		out = runProgram("out = [] + 2;");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new IntegerValue(2)
		}), out);

		out = runProgram("out = {\"foo\": 1} + {\"bar\": 2};");
		assertEquals(new DictionaryValue(Map.of(
				//
				new StringValue("foo"), new IntegerValue(1),
				//
				new StringValue("bar"), new IntegerValue(2))), out);
		out = runProgram("out = {\"foo\": 1} + {\"foo\": 2};");
		assertEquals(new DictionaryValue(Map.of(
				//
				new StringValue("foo"), new IntegerValue(2))), out);
		out = runProgram("out = {} + {};");
		assertEquals(new DictionaryValue(new HashMap<>()), out);
	}

	@Test
	void testUnary() {
		ExpressionValue out;

		out = runProgram("out = !true;");
		assertEquals(new BooleanValue(false), out);
		out = runProgram("out = !false;");
		assertEquals(new BooleanValue(true), out);
		out = runProgram("out = !!!false;");
		assertEquals(new BooleanValue(true), out);

		out = runProgram("out = -(83);");
		assertEquals(new IntegerValue(-83), out);
		out = runProgram("out = --3;");
		assertEquals(new IntegerValue(3), out);
	}
}
