package jmaker.parser;

import java.util.ArrayList;

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
		public final Expression leftSide;
		public final Expression rightSide;

		public Assignment(Expression leftSide, Expression rightSide) {
			this.leftSide = leftSide;
			this.rightSide = rightSide;
		}

		@Override
		public String toString() {
			return "{Assignment, left: " + leftSide + ", right:" + rightSide + "}";
		}
	}

	public static class WhileLoop extends Statement {
		public final ArrayList<Statement> block;
		public final Expression condition;

		// While loop
		public WhileLoop(Expression condition, ArrayList<Statement> block) {
			this.block = block;
			this.condition = condition;
			if (block == null) {
				throw new RuntimeException("Block cannot be null.");
			}
			if (condition == null) {
				throw new RuntimeException("Condition cannot be null.");
			}
		}
	}

	public static class ForLoop extends Statement {
		public final ArrayList<Statement> block;
		public final Assignment setup;
		public final Expression condition;
		public final Assignment increment;

		// For loop
		public ForLoop(ArrayList<Statement> block, Assignment setup, Expression condition, Assignment increment) {
			this.block = block;
			this.setup = setup;
			this.condition = condition;
			this.increment = increment;
			if (block == null) {
				throw new RuntimeException("Block cannot be null.");
			}
			if (setup == null) {
				throw new RuntimeException("Setup cannot be null in 'for' loop.");
			}
			if (condition == null) {
				throw new RuntimeException("Condition cannot be null.");
			}
			if (increment == null) {
				throw new RuntimeException("Increment cannot be null in 'for' loop.");
			}
		}
	}

	public static class If extends Statement {
		public final ArrayList<Expression> conditionals;
		public final ArrayList<ArrayList<Statement>> blocks;
		public final ArrayList<Statement> elseBlock;

		public If(Expression conditional, ArrayList<Statement> block) {
			assert (conditional != null);
			assert (block != null);
			conditionals = new ArrayList<>();
			blocks = new ArrayList<>();
			conditionals.add(conditional);
			blocks.add(block);
			elseBlock = null;
		}

		public If(ArrayList<Expression> conditionals, ArrayList<ArrayList<Statement>> blocks) {
			assert (conditionals != null);
			assert (blocks != null);
			assert (conditionals.size() > 0);
			assert (blocks.size() > 0);
			assert (conditionals.size() == blocks.size());
			this.conditionals = conditionals;
			this.blocks = blocks;
			elseBlock = null;
		}

		public If(ArrayList<Expression> conditionals, ArrayList<ArrayList<Statement>> blocks, ArrayList<Statement> elseBlock) {
			assert (conditionals != null);
			assert (blocks != null);
			assert (elseBlock != null);
			assert (conditionals.size() > 0);
			assert (blocks.size() > 0);
			assert (conditionals.size() == blocks.size());
			this.conditionals = conditionals;
			this.blocks = blocks;
			this.elseBlock = elseBlock;
		}
	}

	public static class BlockStatement extends Statement {
		public ArrayList<Statement> statements;

		public BlockStatement(ArrayList<Statement> statements) {
			this.statements = statements;
		}
	}

	public static class Rule extends Statement {
		public ArrayList<Expression> targets;
		public ArrayList<Expression> dependencies;
		public ArrayList<Statement> block;

		public Rule(ArrayList<Expression> targets, ArrayList<Expression> dependencies, ArrayList<Statement> block) {
			this.targets = targets;
			this.dependencies = dependencies;
			this.block = block;
		}

		@Override
		public String toString() {
			return "{Rule, targets: " + targets + ", deps: " + dependencies + ", block: " + block + "}";
		}
	}

	public static class Empty extends Statement {}
}
