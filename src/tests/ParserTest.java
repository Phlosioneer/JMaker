package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;
import jmaker.parser.BinaryOperator;
import jmaker.parser.Block;
import jmaker.parser.Expression;
import jmaker.parser.Expression.Binary;
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
					new Expression.Number(2)),
			new Statement.Rule(
					//
					new Expression[]{
						new Expression.StringLiteral("foo.class")
					},
					//
					new Expression[]{
						new Expression.StringLiteral("foo.java")
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
														new Expression.StringLiteral("javac "),
														//
														BinaryOperator.ADD,
														//
														new Expression.Index(
																//
																new Expression.Symbol("deps"),
																//
																new Expression.Number(0))),
												//
												BinaryOperator.ADD,
												//
												new Expression.StringLiteral(" -o ")),
										//
										BinaryOperator.ADD,
										//
										new Expression.Index(
												//
												new Expression.Symbol("targets"),
												//
												new Expression.Number(0))),
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
						new Expression.BooleanLiteral(true),
						new Expression.Binary(
								//
								new Expression.Binary(
										//
										new Expression.Number(6),
										//
										BinaryOperator.ADD,
										//
										new Expression.Number(2)),
								//
								BinaryOperator.EQUAL,
								//
								new Expression.Number(8))
					},
					//
					new Block[]{
						new Block(new Statement[]{
							new Statement.Assignment(
									//
									new Expression.Symbol("var1"),
									//
									new Expression.StringLiteral("hello"))
						}),
						new Block(new Statement[]{
							new Statement.Assignment(
									//
									new Expression.Symbol("var1"),
									//
									new Expression.Number(253))
						})
					},
					//
					new Block(new Statement[]{
						new Statement.If(
								//
								new Expression[]{
									new Expression.BooleanLiteral(false)
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
														new Expression.Number(3.14),
														//
														BinaryOperator.ADD,
														//
														new Expression.Number(2)))
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
															new Expression.StringLiteral("testArray")),
													//
													new Expression.Number(0)))
								}))
					})),
			// Desugared for loop
			new Statement.BlockStatement(new Block(new Statement[]{
				new Statement.Assignment(
						//
						new Expression.Symbol("i"),
						//
						new Expression.Number(0)),
				new Statement.WhileLoop(
						//
						new Expression.Binary(
								//
								new Expression.Symbol("i"),
								//
								BinaryOperator.LESS,
								//
								new Expression.Number(20)),
						//
						new Block(new Statement[]{
							new Statement.Rule(
									//
									new Expression[]{
										new Expression.Binary(
												//
												new Expression.Binary(
														//
														new Expression.StringLiteral("file"),
														//
														BinaryOperator.ADD,
														//
														new Expression.Symbol("i")),
												//
												BinaryOperator.ADD,
												//
												new Expression.StringLiteral(".txt"))
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
																		new Expression.StringLiteral("cat "),
																		//
																		BinaryOperator.ADD,
																		//
																		new Expression.Symbol("i")),
																//
																BinaryOperator.ADD,
																//
																new Expression.StringLiteral(" > ")),
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
											new Expression.Number(1)))
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
							new Expression.Number(2)),
					//
					new Block(new Statement[]{
						new Statement.ExpressionStatement(
								//
								new Expression.FunctionCall(
										//
										new Expression.Symbol("println"),
										//
										new Expression[]{
											new Expression.StringLiteral("Hello World"),
											new Expression.Symbol("var1")
										}),
								//
								false),
						new Statement.Assignment(
								//
								new Expression.Symbol("var1"),
								//
								new Expression.Number(0))
					}))
		});

		assertEquals(expectedTree.toString(), output.toString());
	}
}
