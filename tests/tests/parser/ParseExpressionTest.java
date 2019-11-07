package tests.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.StringValue;
import jmaker.parser.BinaryOperator;
import jmaker.parser.Block;
import jmaker.parser.Expression;
import jmaker.parser.ExpressionStatementKind;
import jmaker.parser.Statement;
import jmaker.parser.UnaryOperator;
import tests.interpreter.TestUtil;

class ParseExpressionTest {

	// Helper for testNumbers
	private static Block numberToBlock(int num) {
		Expression inner;
		if (num < 0) {
			inner = new Expression.Unary(new IntegerValue(Math.abs(num)), UnaryOperator.NEGATE);
		} else {
			inner = new IntegerValue(Math.abs(num));
		}
		return TestUtil.expressionToBlock(inner);
	}

	// Helper for testNumbers
	private static Block numberToBlock(double num) {
		Expression inner;
		if (num < 0) {
			inner = new Expression.Unary(new DoubleValue(Math.abs(num)), UnaryOperator.NEGATE);
		} else {
			inner = new DoubleValue(Math.abs(num));
		}
		return TestUtil.expressionToBlock(inner);
	}

	@Test
	void testNumbers() {
		Block output;

		output = TestUtil.parseProgram("32;");
		assertEquals(numberToBlock(32), output);
		output = TestUtil.parseProgram("3448002;");
		assertEquals(numberToBlock(3448002), output);
		output = TestUtil.parseProgram("344_800_2;");
		assertEquals(numberToBlock(3448002), output);
		output = TestUtil.parseProgram("-2;");
		assertEquals(numberToBlock(-2), output);
		output = TestUtil.parseProgram("0;");
		assertEquals(numberToBlock(0), output);

		output = TestUtil.parseProgram("32.4;");
		assertEquals(numberToBlock(32.4), output);
		output = TestUtil.parseProgram("44224.8323;");
		assertEquals(numberToBlock(44224.8323), output);
		output = TestUtil.parseProgram("442_24.83_23;");
		assertEquals(numberToBlock(44224.8323), output);
		output = TestUtil.parseProgram("-0.2;");
		assertEquals(numberToBlock(-0.2), output);
		output = TestUtil.parseProgram("0.0;");
		assertEquals(numberToBlock(0.0), output);
	}

	// Helper for testStrings
	private static Block stringToBlock(String string) {
		return TestUtil.expressionToBlock(new StringValue(string));
	}

	@Test
	void testStrings() {
		Block output;

		output = TestUtil.parseProgram("\"foo\";");
		assertEquals(stringToBlock("foo"), output);
		output = TestUtil.parseProgram("'foo';");
		assertEquals(stringToBlock("foo"), output);
		output = TestUtil.parseProgram("\"he said \\\"foo\\\" not 'bar'.\";");
		assertEquals(stringToBlock("he said \"foo\" not 'bar'."), output);
		output = TestUtil.parseProgram("'he said \"foo\" not \\'bar\\'.';");
		assertEquals(stringToBlock("he said \"foo\" not 'bar'."), output);

		assertThrows(RuntimeException.class, ()-> {
			TestUtil.parseProgram("'he said \\\"foo\\\" not \\'bar\\'.';");
		});
		assertThrows(RuntimeException.class, ()-> {
			TestUtil.parseProgram("\"he said \\\"foo\\\" not \\'bar\\'.\";");
		});

		output = TestUtil.parseProgram("\"newline\\n and tab\\t and windows\\r stuff\";");
		assertEquals(stringToBlock("newline\n and tab\t and windows\r stuff"), output);
		output = TestUtil.parseProgram("'byte\\x2B';");
		assertEquals(stringToBlock("byte+"), output);
		output = TestUtil.parseProgram("'unicode\\u002B';");
		assertEquals(stringToBlock("unicode+"), output);
		output = TestUtil.parseProgram("'backslash\\\\';");
		assertEquals(stringToBlock("backslash\\"), output);

		assertThrows(RuntimeException.class, ()-> {
			TestUtil.parseProgram("'incomplete byte\\x2';");
		});
		assertThrows(RuntimeException.class, ()-> {
			TestUtil.parseProgram("'incomplete unicode\\u';");
		});

		output = TestUtil.parseProgram("r'foobar \\n';");
		assertEquals(stringToBlock("foobar \\n"), output);
		output = TestUtil.parseProgram("r'fake escaped backslash\\\\';");
		assertEquals(stringToBlock("fake escaped backslash\\\\"), output);
		output = TestUtil.parseProgram("r'unusual escape \\f';");
		assertEquals(stringToBlock("unusual escape \\f"), output);
		output = TestUtil.parseProgram("r\"Escaped quote\\\" not escaped\\'\";");
		assertEquals(stringToBlock("Escaped quote\" not escaped\\'"), output);
	}

	@Test
	void testChainedLogicOperators() {
		Block output;
		Block expectedTree;

		output = TestUtil.parseProgram("true && b && false;");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Binary(
						//
						//
						new Expression.Binary(
								//
								new BooleanValue(true),
								//
								BinaryOperator.AND,
								//
								new Expression.Symbol("b")),
						//
						BinaryOperator.AND,
						//
						new BooleanValue(false)));
		output = TestUtil.parseProgram("true || b || false;");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Binary(
						//
						//
						new Expression.Binary(
								//
								new BooleanValue(true),
								//
								BinaryOperator.OR,
								//
								new Expression.Symbol("b")),
						//
						BinaryOperator.OR,
						//
						new BooleanValue(false)));
		assertEquals(expectedTree, output);
	}

	@Test
	void testIndirectFunctionCall() {
		var output = TestUtil.parseProgram("(foo[0])(\"bar\", 5 + 3);");

		var expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.FunctionCall(
						//
						new Expression.Index(
								//
								new Expression.Symbol("foo"),
								//
								new IntegerValue(0)),
						//
						new Expression[]{
							new StringValue("bar"),
							new Expression.Binary(
									//
									new IntegerValue(5),
									//
									BinaryOperator.ADD,
									//
									new IntegerValue(3))
						}));

		assertEquals(expectedTree, output);
	}

	@Test
	void testIndirectIndex() {
		var output = TestUtil.parseProgram("(foo + bar)[\"baz\"];");

		var expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Index(
						//
						new Expression.Binary(
								//
								new Expression.Symbol("foo"),
								//
								BinaryOperator.ADD,
								//
								new Expression.Symbol("bar")),
						//
						new StringValue("baz")));

		assertEquals(expectedTree, output);
	}

	@Test
	void testRangedIndex() {
		Block output;
		Block expectedTree;

		output = TestUtil.parseProgram("foo[:];");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.IndexRange(new Expression.Symbol("foo"), null, null));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("foo[1 : 4];");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.IndexRange(
						//
						new Expression.Symbol("foo"),
						//
						new IntegerValue(1),
						//
						new IntegerValue(4)));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("foo[2:];");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.IndexRange(
						//
						new Expression.Symbol("foo"),
						//
						new IntegerValue(2),
						//
						null));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("foo[:5];");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.IndexRange(
						//
						new Expression.Symbol("foo"),
						//
						null,
						//
						new IntegerValue(5)));
		assertEquals(expectedTree, output);
	}

	@Test
	void testChainedIndex() {
		var output = TestUtil.parseProgram("foo[3]['bar'][2:5];");
		var expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Index(
						//
						new Expression.Index(
								//
								new Expression.IndexRange(
										//
										new Expression.Symbol("foo"),
										//
										new IntegerValue(2),
										//
										new IntegerValue(5)),
								//
								new StringValue("bar")),
						//
						new IntegerValue(3)));
		assertEquals(expectedTree, output);
	}

	@Test
	void testLambda() {
		Block output;
		Block expectedTree;

		var lambdaName = new Expression.Symbol("<anon-function-0>");

		output = TestUtil.parseProgram("()->7;");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Lambda(
						//
						new Statement.FunctionDefinition(
								//
								lambdaName,
								//
								new Expression.Symbol[]{},
								//
								0,
								//
								TestUtil.statementToBlock(
										//
										new Statement.ExpressionStatement(
												//
												new IntegerValue(7),
												//
												ExpressionStatementKind.RETURN)))));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("x->x + foo(x, 2);");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Lambda(new Statement.FunctionDefinition(
						//
						lambdaName,
						//
						new Expression.Symbol[]{
							new Expression.Symbol("x")
						},
						//
						0,
						//
						TestUtil.statementToBlock(
								//
								new Statement.ExpressionStatement(
										//
										new Expression.Binary(
												//
												new Expression.Symbol("x"),
												//
												BinaryOperator.ADD,
												//
												new Expression.FunctionCall(
														//
														new Expression.Symbol("foo"),
														//
														new Expression[]{
															new Expression.Symbol("x"),
															new IntegerValue(2)
														})),
										//
										ExpressionStatementKind.RETURN)))));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("(x, y)->{3;};");
		expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Lambda(new Statement.FunctionDefinition(
						//
						lambdaName,
						//
						new Expression.Symbol[]{
							new Expression.Symbol("x"),
							new Expression.Symbol("y")
						},
						//
						0,
						//
						TestUtil.expressionToBlock(new IntegerValue(3)))));
		assertEquals(expectedTree, output);
	}

	@Test
	void testArrayLiteral() {
		var output = TestUtil.parseProgram("[4, 2, true, []];");

		var expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Array(new Expression[]{
					new IntegerValue(4),
					new IntegerValue(2),
					new BooleanValue(true),
					new Expression.Array(new Expression[]{})
				}));

		assertEquals(expectedTree, output);
	}

	@Test
	void testDictLiteral() {
		var output = TestUtil.parseProgram("{2: 2, \"foo\": bar, 2: baz, [fizz, 7.5]: {}};");

		var expectedTree = TestUtil.expressionToBlock(
				//
				new Expression.Dictionary(
						//
						new Expression[]{
							new IntegerValue(2),
							new StringValue("foo"),
							new IntegerValue(2),
							new Expression.Array(new Expression[]{
								new Expression.Symbol("fizz"),
								new DoubleValue(7.5)
							})
						},
						//
						new Expression[]{
							new IntegerValue(2),
							new Expression.Symbol("bar"),
							new Expression.Symbol("baz"),
							new Expression.Dictionary(
									//
									new Expression[]{},
									//
									new Expression[]{})
						}));

		assertEquals(expectedTree, output);
	}

	@Test
	void testOrderOfOperations() {
		Block output;
		Block expectedTree;

		output = TestUtil.parseProgram("-2 < 2;");
		expectedTree = TestUtil.expressionToBlock(new Expression.Binary(
				//
				new Expression.Unary(new IntegerValue(2), UnaryOperator.NEGATE),
				//
				BinaryOperator.LESS,
				//
				new IntegerValue(2)));
		assertEquals(expectedTree, output);
	}

	@Test
	void testCannotChainLogic() {
		assertThrows(RuntimeException.class, ()-> {
			TestUtil.parseProgram("true == false != true;");
		});
	}
}
