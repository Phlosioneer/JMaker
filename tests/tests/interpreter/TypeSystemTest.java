package tests.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static tests.interpreter.TestUtil.runProgram;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.StringValue;

class TypeSystemTest {

	@Test
	void testImplicitStringCasts() {
		ExpressionValue out;

		out = runProgram("out = \"\" + 3;");
		assertEquals(new StringValue("3"), out);
		out = runProgram("out = \"\" + true;");
		assertEquals(new StringValue("true"), out);
		out = runProgram("out = \"\" + 3.14;");
		assertEquals(new StringValue("3.14"), out);
		out = runProgram("out = \"\" + [\"bar\", 1, 2, \"foo\", \"\"];");
		assertEquals(new StringValue("[\"bar\", 1, 2, \"foo\", \"\"]"), out);
		out = runProgram("out = \"\" + [];");
		assertEquals(new StringValue("[]"), out);
		out = runProgram("out = \"\" + {\"foo\": \"bar\"};");
		assertEquals(new StringValue("{\"foo\": \"bar\"}"), out);
		out = runProgram("out = \"\" + {};");
		assertEquals(new StringValue("{}"), out);

		// Dictionaries can be in any order.
		out = runProgram("out = \"\" + {\"foo\": \"bar\", \"baz\": \"fizz\"};");
		var asStr = out.toString();
		assertTrue(asStr.contains("\"foo\": \"bar\""));
		assertTrue(asStr.contains("\"baz\": \"fizz\""));
		assertEquals(asStr.charAt(0), '{');
		assertEquals(asStr.charAt(asStr.length() - 1), '}');
		assertEquals(asStr.charAt(asStr.indexOf(',') + 1), ' ');

		out = runProgram("out = 3 + \"\";");
		assertEquals(new StringValue("3"), out);
		out = runProgram("out = [] + \"\";");
		assertEquals(new ArrayValue(new ExpressionValue[]{
			new StringValue("")
		}), out);
		out = runProgram("out = {} + \"\";");
		assertEquals(new StringValue("{}"), out);
	}
}
