package jmaker.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import jmaker.parser.Expression.Symbol;

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
		public int hashCode() {
			return Objects.hash(expression, isCommand);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof ExpressionStatement)) {
				return false;
			}
			ExpressionStatement other = (ExpressionStatement) obj;
			return Objects.equals(expression, other.expression) && isCommand == other.isCommand;
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
		public int hashCode() {
			return Objects.hash(leftSide, rightSide);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Assignment)) {
				return false;
			}
			Assignment other = (Assignment) obj;
			return Objects.equals(leftSide, other.leftSide) && Objects.equals(rightSide, other.rightSide);
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
		public int hashCode() {
			return Objects.hash(block, condition);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof WhileLoop)) {
				return false;
			}
			WhileLoop other = (WhileLoop) obj;
			return Objects.equals(block, other.block) && Objects.equals(condition, other.condition);
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(blocks);
			result = prime * result + Arrays.hashCode(conditionals);
			result = prime * result + Objects.hash(elseBlock);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof If)) {
				return false;
			}
			If other = (If) obj;
			return Arrays.equals(blocks, other.blocks) && Arrays.equals(conditionals, other.conditionals) && Objects.equals(elseBlock, other.elseBlock);
		}
	}

	public static class BlockStatement extends Statement {
		public final Block block;

		public BlockStatement(Block block) {
			this.block = block;
		}

		@Override
		public int hashCode() {
			return Objects.hash(block);
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof BlockStatement)) {
				return false;
			}
			BlockStatement other = (BlockStatement) obj;
			return Objects.equals(block, other.block);
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
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(dependencies);
			result = prime * result + Arrays.hashCode(targets);
			result = prime * result + Objects.hash(block);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof Rule)) {
				return false;
			}
			Rule other = (Rule) obj;
			return Objects.equals(block, other.block) && Arrays.equals(dependencies, other.dependencies) && Arrays.equals(targets, other.targets);
		}
	}

	public static class Empty extends Statement {
		@Override
		public int hashCode() {
			return 0;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			return obj instanceof Empty;
		}
	}
}
