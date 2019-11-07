package tests.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.StringValue;
import jmaker.parser.BinaryOperator;
import jmaker.parser.Block;
import jmaker.parser.Expression;
import jmaker.parser.ExpressionStatementKind;
import jmaker.parser.Statement;
import tests.interpreter.TestUtil;

class ParseStatementTest {

	@Test
	void testAssignments() {
		Block output;
		Block expectedTree;

		var foo = new Expression.Symbol("foo");
		var three = new IntegerValue(3);

		output = TestUtil.parseProgram("foo = 3;");
		expectedTree = TestUtil.statementToBlock(new Statement.Assignment(foo, three));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("foo += 3;");
		expectedTree = TestUtil.statementToBlock(new Statement.Assignment(
				//
				foo,
				//
				new Expression.Binary(foo, BinaryOperator.ADD, three)));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("foo /= 3;");
		expectedTree = TestUtil.statementToBlock(new Statement.Assignment(
				//
				foo,
				//
				new Expression.Binary(foo, BinaryOperator.DIV, three)));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("foo &&= true;");
		expectedTree = TestUtil.statementToBlock(new Statement.Assignment(
				//
				foo,
				//
				new Expression.Binary(foo, BinaryOperator.AND, new BooleanValue(true))));
		assertEquals(expectedTree, output);
	}

	@Test
	void testIf() {
		Block output;
		Block expectedTree;

		output = TestUtil.parseProgram("if (true) {3;}");
		expectedTree = TestUtil.statementToBlock(new Statement.If(
				//
				new Expression[]{
					new BooleanValue(true)
				},
				//
				new Block[]{
					TestUtil.expressionToBlock(new IntegerValue(3))
				}));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("if (true) {\"foo\";} else if (false) {7;}");
		expectedTree = TestUtil.statementToBlock(new Statement.If(
				//
				new Expression[]{
					new BooleanValue(true),
					new BooleanValue(false)
				},
				//
				new Block[]{
					TestUtil.expressionToBlock(new StringValue("foo")),
					TestUtil.expressionToBlock(new IntegerValue(7))
				}));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("if (true) {} else {}");
		expectedTree = TestUtil.statementToBlock(new Statement.If(
				//
				new Expression[]{
					new BooleanValue(true)
				},
				//
				new Block[]{
					new Block(new Statement[]{})
				},
				//
				new Block(new Statement[]{})));
		assertEquals(expectedTree, output);
	}

	@Test
	void testWhileLoop() {
		var output = TestUtil.parseProgram("while (true) {3;}");
		var expectedTree = TestUtil.statementToBlock(new Statement.WhileLoop(
				//
				new BooleanValue(true),
				//
				TestUtil.expressionToBlock(new IntegerValue(3))));
		assertEquals(expectedTree, output);
	}

	@Test
	void testForEachRewriting() {
		var output = TestUtil.parseProgram("for (foo = bar) {baz(foo);}");
		var expectedTree = new Block(new Statement[]{
			new Statement.BlockStatement(new Block(new Statement[]{
				new Statement.Assignment(new Expression.Symbol("<anon-int-0>"), new IntegerValue(0)),
				new Statement.Assignment(new Expression.Symbol("<anon-array-1>"), new Expression.Symbol("bar")),
				new Statement.Assignment(new Expression.Symbol("<anon-int-2>"), new Expression.FunctionCall(
						//
						new Expression.Symbol("length"),
						//
						new Expression[]{
							new Expression.Symbol("<anon-array-1>")
						})),
				new Statement.WhileLoop(
						//
						new Expression.Binary(
								//
								new Expression.Symbol("<anon-int-0>"),
								//
								BinaryOperator.LESS,
								//
								new Expression.Symbol("<anon-int-2>")),
						//
						new Block(new Statement[]{
							// Name = <anon-array-1>[<anon-int-0>];
							new Statement.Assignment(
									//
									new Expression.Symbol("foo"),
									//
									new Expression.Index(
											//
											new Expression.Symbol("<anon-array-1>"),
											//
											new Expression.Symbol("<anon-int-0>"))),
							// baz(foo);
							new Statement.ExpressionStatement(
									//
									new Expression.FunctionCall(
											//
											new Expression.Symbol("baz"),
											//
											new Expression[]{
												new Expression.Symbol("foo")
											}),
									//
									ExpressionStatementKind.NORMAL),
							// <anon-int-0> += 1;
							new Statement.Assignment(
									//
									new Expression.Symbol("<anon-int-0>"),
									//
									new Expression.Binary(
											//
											new Expression.Symbol("<anon-int-0>"),
											//
											BinaryOperator.ADD,
											//
											new IntegerValue(1)))
						}))
			}))
		});
		assertEquals(expectedTree, output);
	}

	@Test
	void testManualForRewrite() {
		Block output;
		Block expectedTree;

		var firstAssign = new Statement.Assignment(new Expression.Symbol("i"), new IntegerValue(0));
		var condition = new Expression.Binary(
				//
				new Expression.Symbol("i"),
				//
				BinaryOperator.LESS,
				//
				new IntegerValue(10));
		var secondAssign = new Statement.Assignment(new Expression.Symbol("i"), new Expression.Binary(
				//
				new Expression.Symbol("i"),
				//
				BinaryOperator.ADD,
				//
				new IntegerValue(1)));
		var body = new Statement.ExpressionStatement(
				//
				new Expression.Symbol("i"),
				//
				ExpressionStatementKind.NORMAL);

		// All three present.
		output = TestUtil.parseProgram("for (i = 0; i < 10; i += 1) {i;}");
		expectedTree = TestUtil.statementToBlock(new Statement.BlockStatement(new Block(new Statement[]{
			firstAssign,
			new Statement.WhileLoop(condition, new Block(new Statement[]{
				body,
				secondAssign
			}))
		})));
		assertEquals(expectedTree, output);

		// Missing first.
		output = TestUtil.parseProgram("for (; i < 10; i += 1) {i;}");
		expectedTree = TestUtil.statementToBlock(new Statement.WhileLoop(
				//
				condition,
				//
				new Block(new Statement[]{
					body,
					secondAssign
				})));
		assertEquals(expectedTree, output);

		// Missing second.
		output = TestUtil.parseProgram("for (i = 0;; i += 1) {i;}");
		expectedTree = TestUtil.statementToBlock(new Statement.BlockStatement(new Block(new Statement[]{
			firstAssign,
			new Statement.WhileLoop(new BooleanValue(true), new Block(new Statement[]{
				body,
				secondAssign
			}))
		})));
		assertEquals(expectedTree, output);

		// Missing third.
		output = TestUtil.parseProgram("for (i = 0; i < 10;) {i;}");
		expectedTree = TestUtil.statementToBlock(new Statement.BlockStatement(new Block(new Statement[]{
			firstAssign,
			new Statement.WhileLoop(condition, TestUtil.statementToBlock(body))
		})));
		assertEquals(expectedTree, output);

		// Missing all.
		output = TestUtil.parseProgram("for (;;) {i;}");
		expectedTree = TestUtil.statementToBlock(new Statement.WhileLoop(
				//
				new BooleanValue(true),
				//
				TestUtil.statementToBlock(body)));
		assertEquals(expectedTree, output);
	}

	@Test
	void testRules() {
		Block output;
		Block expectedTree;

		output = TestUtil.parseProgram("'*.o' : '*.c', '*.h' {}");
		expectedTree = TestUtil.statementToBlock(new Statement.Rule(
				//
				new Expression[]{
					new StringValue("*.o")
				},
				//
				new Expression[]{
					new StringValue("*.c"),
					new StringValue("*.h")
				},
				//
				new Block(new Statement[]{})));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("'foo' : {}");
		expectedTree = TestUtil.statementToBlock(new Statement.Rule(
				//
				new Expression[]{
					new StringValue("foo")
				},
				//
				new Expression[]{},
				//
				new Block(new Statement[]{})));
	}

	@Test
	void testFunctionDefinition() {
		Block output;
		Block expectedTree;

		output = TestUtil.parseProgram("function foo() {}");
		expectedTree = TestUtil.statementToBlock(new Statement.FunctionDefinition(
				//
				new Expression.Symbol("foo"),
				//
				new Expression.Symbol[]{},
				//
				0,
				//
				new Block(new Statement[]{})));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("function foo(bar, a, b) { 3; }");
		expectedTree = TestUtil.statementToBlock(new Statement.FunctionDefinition(
				//
				new Expression.Symbol("foo"),
				//
				new Expression.Symbol[]{
					new Expression.Symbol("bar"),
					new Expression.Symbol("a"),
					new Expression.Symbol("b")
				},
				//
				0,
				//
				TestUtil.expressionToBlock(new IntegerValue(3))));
		assertEquals(expectedTree, output);

		output = TestUtil.parseProgram("function foo(bar, a*, b) { 3; }");
		expectedTree = TestUtil.statementToBlock(new Statement.FunctionDefinition(
				//
				new Expression.Symbol("foo"),
				//
				new Expression.Symbol[]{
					new Expression.Symbol("bar"),
					new Expression.Symbol("a"),
					new Expression.Symbol("b")
				},
				//
				1,
				//
				TestUtil.expressionToBlock(new IntegerValue(3))));
		assertEquals(expectedTree, output);
	}

	@Test
	void testEmptyStatement() {
		var output = TestUtil.parseProgram(";;;");
		var expectedTree = new Block(new Statement[]{
			new Statement.Empty(),
			new Statement.Empty(),
			new Statement.Empty()
		});
		assertEquals(expectedTree, output);
	}
}
