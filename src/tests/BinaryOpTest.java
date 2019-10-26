package tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import jmaker.interpreter.BooleanValue;
import jmaker.interpreter.DoubleValue;
import jmaker.interpreter.IntegerValue;
import jmaker.interpreter.Interpreter;
import jmaker.parser.BinaryOperator;
import jmaker.parser.Block;
import jmaker.parser.Expression;
import jmaker.parser.Statement;

class BinaryOpTest {

	Interpreter interp;

	@BeforeEach
	void initTest() {
		interp = new Interpreter(new Block(new Statement[]{}));
	}

	@Test
	void intEqualTest() {
		var case1 = interp.runBinaryOp(new IntegerValue(1), new IntegerValue(1), BinaryOperator.EQUAL);
		assertEquals(new BooleanValue(true), case1);

		var case2 = interp.runBinaryOp(new IntegerValue(-1), new IntegerValue(1), BinaryOperator.EQUAL);
		assertEquals(new BooleanValue(false), case2);
	}

	@Test
	void boolEqualTest() {
		var case1 = interp.runBinaryOp(new BooleanValue(true), new BooleanValue(true), BinaryOperator.EQUAL);
		assertEquals(new BooleanValue(true), case1);

		var case2 = interp.runBinaryOp(new BooleanValue(false), new BooleanValue(true), BinaryOperator.EQUAL);
		assertEquals(new BooleanValue(false), case2);
	}

	@Test
	void doubleEqualTest() {
		var case1 = interp.runBinaryOp(new DoubleValue(1.25), new DoubleValue(1.25), BinaryOperator.EQUAL);
		assertEquals(new BooleanValue(true), case1);

		var case2 = interp.runBinaryOp(new DoubleValue(14.2), new DoubleValue(14.201), BinaryOperator.EQUAL);
		assertEquals(new BooleanValue(false), case2);
	}

	@Test
	void additionTest() {
		var left = new Expression.Binary(new IntegerValue(7), BinaryOperator.ADD, new IntegerValue(3));
		var right = new Expression.Binary(new DoubleValue(2.1), BinaryOperator.ADD, new DoubleValue(-14.0));
		var full = new Expression.Binary(left, BinaryOperator.ADD, right);
		var ret = interp.runExpression(full);
		assertEquals(new DoubleValue(7 + 3 + 2.1 - 14), ret);
	}
}
