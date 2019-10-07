package jmaker.parser;

import java.util.ArrayList;
import tests.TestUtil;

public class Block {
	public final Statement[] statements;

	public Block(ArrayList<Statement> statementList) {
		this(statementList.toArray(size->new Statement[size]));
	}

	public Block(Statement[] statements) {
		this.statements = statements;
	}

	@Override
	public String toString() {
		return TestUtil.arrayToString(statements);
	}
}
