package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.StringValue;
import jmaker.parser.BinaryOperator;
import jmaker.parser.Block;
import jmaker.parser.Expression;
import jmaker.parser.Lexer;
import jmaker.parser.Parser;
import jmaker.parser.Statement;

class ParserTest {

	public ParserTest() {

	}

	@Test
	void testFile1() {
		var input = TestUtil.readFile("jmakerTest1.txt");
		var lexer = new Lexer(input);
		var parser = new Parser(lexer.scanAll());
		var output = parser.parseFile();

		var expectedTree = new Block(new Statement[]{
			new Statement.Assignment(
					//
					new Expression.Symbol("TEST"),
					//
					new IntegerValue(2)),
			new Statement.Rule(
					//
					new Expression[]{
						new StringValue("foo.class")
					},
					//
					new Expression[]{
						new StringValue("foo.java")
					},
					//
					new Block(new Statement[]{
						new Statement.ExpressionStatement(
								//
								new Expression.Binary(
										//
										new Expression.Binary(
												//
												new Expression.Binary(
														//
														new StringValue("javac "),
														//
														BinaryOperator.ADD,
														//
														new Expression.Index(
																//
																new Expression.Symbol("deps"),
																//
																new IntegerValue(0))),
												//
												BinaryOperator.ADD,
												//
												new StringValue(" -o ")),
										//
										BinaryOperator.ADD,
										//
										new Expression.Index(
												//
												new Expression.Symbol("targets"),
												//
												new IntegerValue(0))),
								//
								true)
					}))
		});

		assertEquals(expectedTree.toString(), output.toString());
	}

	@Test
	void testFile2() {
		System.out.println("Test2-------");
		var input = TestUtil.readFile("jmakerTest2.txt");
		var lexer = new Lexer(input);
		var parser = new Parser(lexer.scanAll());
		var output = parser.parseFile();

		var expectedTree = new Block(new Statement[]{
			new Statement.If(
					//
					new Expression[]{
						new BooleanValue(true),
						new Expression.Binary(
								//
								new Expression.Binary(
										//
										new IntegerValue(6),
										//
										BinaryOperator.ADD,
										//
										new IntegerValue(2)),
								//
								BinaryOperator.EQUAL,
								//
								new IntegerValue(8))
					},
					//
					new Block[]{
						new Block(new Statement[]{
							new Statement.Assignment(
									//
									new Expression.Symbol("var1"),
									//
									new StringValue("hello"))
						}),
						new Block(new Statement[]{
							new Statement.Assignment(
									//
									new Expression.Symbol("var1"),
									//
									new IntegerValue(253))
						})
					},
					//
					new Block(new Statement[]{
						new Statement.If(
								//
								new Expression[]{
									new BooleanValue(false)
								},
								//
								new Block[]{
									new Block(new Statement[]{
										new Statement.Assignment(
												//
												new Expression.Symbol("var1"),
												//
												new Expression.Binary(
														//
														new DoubleValue(3.14),
														//
														BinaryOperator.ADD,
														//
														new IntegerValue(2)))
									})
								},
								//
								new Block(new Statement[]{
									new Statement.Assignment(
											//
											new Expression.Symbol("var1"),
											//
											new Expression.Index(
													//
													new Expression.Index(
															//
															new Expression.Symbol("env"),
															//
															new StringValue("testArray")),
													//
													new IntegerValue(0)))
								}))
					})),
			// Desugared for loop
			new Statement.BlockStatement(new Block(new Statement[]{
				new Statement.Assignment(
						//
						new Expression.Symbol("i"),
						//
						new IntegerValue(0)),
				new Statement.WhileLoop(
						//
						new Expression.Binary(
								//
								new Expression.Symbol("i"),
								//
								BinaryOperator.LESS,
								//
								new IntegerValue(20)),
						//
						new Block(new Statement[]{
							new Statement.Rule(
									//
									new Expression[]{
										new Expression.Binary(
												//
												new Expression.Binary(
														//
														new StringValue("file"),
														//
														BinaryOperator.ADD,
														//
														new Expression.Symbol("i")),
												//
												BinaryOperator.ADD,
												//
												new StringValue(".txt"))
									},
									//
									new Expression[]{},
									//
									new Block(new Statement[]{
										new Statement.ExpressionStatement(
												//
												new Expression.Binary(
														//
														new Expression.Binary(
																//
																new Expression.Binary(
																		//
																		new StringValue("cat "),
																		//
																		BinaryOperator.ADD,
																		//
																		new Expression.Symbol("i")),
																//
																BinaryOperator.ADD,
																//
																new StringValue(" > ")),
														//
														BinaryOperator.ADD,
														//
														new Expression.Symbol("target")),
												//
												true)
									})),
							new Statement.Assignment(
									//
									new Expression.Symbol("i"),
									//
									new Expression.Binary(
											//
											new Expression.Symbol("i"),
											//
											BinaryOperator.ADD,
											//
											new IntegerValue(1)))
						}))
			})),
			new Statement.WhileLoop(
					//
					new Expression.Binary(
							//
							new Expression.Symbol("var1"),
							//
							BinaryOperator.GREATER_EQUAL,
							//
							new IntegerValue(2)),
					//
					new Block(new Statement[]{
						new Statement.ExpressionStatement(
								//
								new Expression.FunctionCall(
										//
										new Expression.Symbol("println"),
										//
										new Expression[]{
											new StringValue("Hello World"),
											new Expression.Symbol("var1")
										}),
								//
								false),
						new Statement.Assignment(
								//
								new Expression.Symbol("var1"),
								//
								new IntegerValue(0))
					}))
		});

		assertEquals(expectedTree.toString(), output.toString());
	}
}
