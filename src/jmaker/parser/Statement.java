package jmaker.parser;

import java.util.ArrayList;
import jmaker.parser.Expression.Symbol;
import tests.TestUtil;

public class Statement {

	protected Statement() {}

	@Override
	public String toString() {
		throw new UnsupportedOperationException(getClass().getName() + " MUST implement toString");
	}

	public static class ExpressionStatement extends Statement {
		public final Expression expression;
		public final boolean isCommand;

		public ExpressionStatement(Expression expression, boolean isCommand) {
			this.expression = expression;
			this.isCommand = isCommand;
		}

		@Override
		public String toString() {
			String commandString;
			if (isCommand) {
				commandString = "command";
			} else {
				commandString = "normal";
			}
			return "{ExpressionStatement, " + commandString + ", " + expression + "}";
		}
	}

	public static class Assignment extends Statement {
		// The left side isn't an arbitrary expression - it's a name followed by
		// any number of index expressions.
		public final Symbol leftSide;
		public final Expression rightSide;

		public Assignment(Symbol leftSide, Expression rightSide) {
			this.leftSide = leftSide;
			this.rightSide = rightSide;
		}

		@Override
		public String toString() {
			return "{Assignment, left: " + leftSide + ", right:" + rightSide + "}";
		}
	}

	public static class WhileLoop extends Statement {
		public final Block block;
		public final Expression condition;

		// While loop
		public WhileLoop(Expression condition, Block block) {
			this.block = block;
			this.condition = condition;
			if (block == null) {
				throw new RuntimeException("Block cannot be null.");
			}
			if (condition == null) {
				throw new RuntimeException("Condition cannot be null.");
			}
		}

		@Override
		public String toString() {
			return "{While, condition: " + condition + ", block: " + block + "}";
		}
	}

	public static class If extends Statement {
		public final Expression[] conditionals;
		public final Block[] blocks;
		public final Block elseBlock;

		public If(Expression conditional, Block block) {
			assert (conditional != null);
			assert (block != null);
			conditionals = new Expression[]{
				conditional
			};
			blocks = new Block[]{
				block
			};
			elseBlock = null;
		}

		public If(ArrayList<Expression> conditionals, ArrayList<Block> blocks) {
			this(conditionals.toArray(size->new Expression[size]), blocks.toArray(size->new Block[size]));
		}

		public If(Expression[] conditionals, Block[] blocks) {
			this(conditionals, blocks, null);
		}

		public If(ArrayList<Expression> conditionals, ArrayList<Block> blocks, Block elseBlock) {
			this(conditionals.toArray(size->new Expression[size]), blocks.toArray(size->new Block[size]), elseBlock);
		}

		public If(Expression[] conditionals, Block[] blocks, Block elseBlock) {
			assert (conditionals != null);
			assert (blocks != null);
			assert (conditionals.length > 0);
			assert (conditionals.length == blocks.length);
			this.conditionals = conditionals;
			this.blocks = blocks;
			this.elseBlock = elseBlock;
		}

		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			ret.append("{If, conditionals: ");
			ret.append(TestUtil.arrayToString(conditionals));
			ret.append(", blocks: ");
			ret.append(TestUtil.arrayToString(blocks));
			ret.append(", elseBlock: ");
			ret.append(elseBlock);
			ret.append("}");
			return ret.toString();
		}
	}

	public static class BlockStatement extends Statement {
		public final Block block;

		public BlockStatement(Block block) {
			this.block = block;
		}

		@Override
		public String toString() {
			return "{Block: " + block + "}";
		}
	}

	public static class Rule extends Statement {
		public final Expression[] targets;
		public final Expression[] dependencies;
		public final Block block;

		public Rule(ArrayList<Expression> targets, ArrayList<Expression> dependencies, Block block) {
			this(targets.toArray(size->new Expression[size]), dependencies.toArray(size->new Expression[size]), block);
		}

		public Rule(Expression[] targets, Expression[] dependencies, Block block) {
			this.targets = targets;
			this.dependencies = dependencies;
			this.block = block;
			assert (targets.length > 0);
		}

		@Override
		public String toString() {
			StringBuilder ret = new StringBuilder();
			ret.append("{Rule, targets: ");
			ret.append(TestUtil.arrayToString(targets));
			ret.append(", deps: ");
			ret.append(TestUtil.arrayToString(dependencies));
			ret.append(", block: ");
			ret.append(block);
			ret.append("}");
			return ret.toString();
		}
	}

	public static class Empty extends Statement {
		@Override
		public String toString() {
			return "{EmptyStatement}";
		}
	}
}
