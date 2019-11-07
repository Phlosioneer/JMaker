package jmaker.parser;

import java.util.ArrayList;
import java.util.List;
import codegen.JMakerParser.BlockContext;
import codegen.JMakerParser.FileContext;
import codegen.JMakerParser.StatementContext;

public class BlockVisitor {
	private final VisitorManager parent;

	public BlockVisitor(VisitorManager parent) {
		this.parent = parent;
	}

	private Block visitStatements(List<StatementContext> statementContexts) {
		var statements = new ArrayList<Statement>();
		for (var statementContext : statementContexts) {
			var statement = parent.statementVisitor.visitStatement(statementContext);
			statements.add(statement);
		}

		return new Block(statements);
	}

	public Block visitFile(FileContext context) {
		return visitStatements(context.statement());
	}

	public Block visitBlock(BlockContext context) {
		return visitStatements(context.statement());
	}
}
