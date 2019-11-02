package jmaker.parser;

import java.util.ArrayList;
import java.util.Arrays;

public class Block {
	public final Statement[] statements;

	public Block(ArrayList<Statement> statementList) {
		this(statementList.toArray(size->new Statement[size]));
	}

	public Block(Statement[] statements) {
		this.statements = statements;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(statements);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Block)) {
			return false;
		}
		Block other = (Block) obj;
		return Arrays.equals(statements, other.statements);
	}

}
