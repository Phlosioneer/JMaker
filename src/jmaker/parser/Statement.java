package jmaker.parser;

import java.util.Arrays;
import java.util.Objects;
import jmaker.parser.Expression.Symbol;

public interface Statement {

	public static class ExpressionStatement implements Statement {
		// Can be null if kind == RETURN.
		public final Expression expression;
		public final ExpressionStatementKind kind;

		public ExpressionStatement(Expression expression, ExpressionStatementKind kind) {
			this.expression = expression;
			this.kind = kind;
		}

		@Override
		public int hashCode() {
			return Objects.hash(expression, kind);
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
			return Objects.equals(expression, other.expression) && kind == other.kind;
		}
	}

	public static class Assignment implements Statement {
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

	public static class WhileLoop implements Statement {
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

	public static class If implements Statement {
		public final Expression[] conditionals;
		public final Block[] blocks;
		public final Block elseBlock;

		public If(Expression[] conditionals, Block[] blocks) {
			this(conditionals, blocks, null);
		}

		public If(Expression[] conditionals, Block[] blocks, Block elseBlock) {
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

	public static class BlockStatement implements Statement {
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

	public static class Rule implements Statement {
		public final Expression[] targets;
		public final Expression[] dependencies;
		public final Block block;

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

	public static class FunctionDefinition implements Statement {
		public final Symbol[] argNames;
		public final int pipeArg;
		public final Symbol functionName;
		public final Block block;

		public FunctionDefinition(Symbol functionName, Symbol[] argNames, int pipeArg, Block block) {
			this.pipeArg = pipeArg;
			if (argNames == null) {
				this.argNames = new Symbol[0];
			} else {
				this.argNames = argNames;
			}
			this.functionName = functionName;
			this.block = block;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + Arrays.hashCode(argNames);
			result = prime * result + Objects.hash(block, functionName, pipeArg);
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof FunctionDefinition)) {
				return false;
			}
			FunctionDefinition other = (FunctionDefinition) obj;
			return Arrays.equals(argNames, other.argNames) && Objects.equals(block, other.block) && Objects.equals(functionName, other.functionName) && pipeArg == other.pipeArg;
		}
	}

	public static class Empty implements Statement {
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
