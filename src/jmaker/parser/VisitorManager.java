package jmaker.parser;

import codegen.JMakerParser;

public class VisitorManager {
	public final BlockVisitor blockVisitor;
	public final StatementVisitor statementVisitor;
	public final ExpressionVisitor expressionVisitor;

	private int anonymousNameId;

	public VisitorManager() {
		blockVisitor = new BlockVisitor(this);
		statementVisitor = new StatementVisitor(this);
		expressionVisitor = new ExpressionVisitor(this);
		anonymousNameId = 0;
	}

	public Block visitAll(JMakerParser parser) {
		var fileAST = parser.file();
		return blockVisitor.visitFile(fileAST);
	}

	public Expression.Symbol generateAnonymousName(String description) {
		var id = anonymousNameId;
		anonymousNameId += 1;
		var name = "<anon-" + description + "-" + id + ">";
		return new Expression.Symbol(name);
	}
}
