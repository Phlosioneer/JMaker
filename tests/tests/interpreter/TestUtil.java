package tests.interpreter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import codegen.JMakerLexer;
import codegen.JMakerParser;
import jmaker.interpreter.ArrayValue;
import jmaker.interpreter.DataType;
import jmaker.interpreter.ExpressionValue;
import jmaker.interpreter.Interpreter;
import jmaker.parser.Block;
import jmaker.parser.Expression;
import jmaker.parser.ExpressionStatementKind;
import jmaker.parser.Statement;
import jmaker.parser.VisitorManager;

public abstract class TestUtil {

	public static String readFile(String filename) {
		var stream = TestUtil.class.getResourceAsStream(filename);
		var reader = new BufferedReader(new InputStreamReader(stream));
		var buffer = new StringBuilder();
		reader.lines().forEach(line->buffer.append(line + '\n'));
		var ret = buffer.toString();
		// Remove last newline.
		if (ret.length() == 0) {
			return "";
		}
		return ret.substring(0, ret.length() - 1);
	}

	public static <T> String arrayToString(T[] array) {
		StringBuilder ret = new StringBuilder();
		ret.append('[');
		if (array.length > 0) {
			ret.append(array[0].toString());
			for (int i = 1; i < array.length; i++) {
				ret.append(", ");
				ret.append(array[i].toString());
			}
		}
		ret.append(']');
		return ret.toString();
	}

	public static Block parseProgram(String code) {
		var chars = CharStreams.fromString(code);
		var lexer = new JMakerLexer(chars);
		var tokens = new CommonTokenStream(lexer);
		var parser = new JMakerParser(tokens);
		var rootBlock = new VisitorManager().visitAll(parser);
		return rootBlock;
	}

	public static Block parseProgram(String[] code) {
		var builder = new StringBuilder();
		builder.append(code[0]);
		for (int i = 1; i < code.length; i++) {
			builder.append('\n');
			builder.append(code[i]);
		}
		return parseProgram(builder.toString());
	}

	// Default outVar is "out".
	public static ExpressionValue runProgram(String code) {
		return runProgram(code, "out");
	}

	public static ExpressionValue runProgram(String code, String outVar) {
		var rootBlock = parseProgram(code);
		var interpreter = new Interpreter(rootBlock);
		interpreter.run();
		var ret = interpreter.memory.get(outVar);
		assertNotNull(ret, outVar + " wasn't defined");
		return ret;
	}

	public static ExpressionValue runProgram(String[] code) {
		return runProgram(code, "out");
	}

	public static ExpressionValue runProgram(String[] code, String outVar) {
		var builder = new StringBuilder();
		builder.append(code[0]);
		for (int i = 1; i < code.length; i++) {
			builder.append('\n');
			builder.append(code[i]);
		}

		return runProgram(builder.toString(), outVar);
	}

	public static void assertArrayEqualsUnordered(ExpressionValue[] expected, ExpressionValue actual) {
		assertNotNull(actual);
		assertEquals(actual.getType(), DataType.Array);
		var inner = ((ArrayValue) actual).elements;
		assertArrayEqualsUnordered(expected, inner);
	}

	public static void assertArrayEqualsUnordered(ExpressionValue[] expected, ExpressionValue[] actual) {
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

	public static Block statementToBlock(Statement statement) {
		return new Block(new Statement[]{
			statement
		});
	}

	public static Block expressionToBlock(Expression expression) {
		return statementToBlock(new Statement.ExpressionStatement(expression, ExpressionStatementKind.NORMAL));
	}
}
