package jmaker.parser;

import java.util.ArrayList;

public class Parser {
	private final ArrayList<Token> tokens;
	private int currentIndex;

	public Parser(ArrayList<Token> tokens) {
		this.tokens = tokens;
		currentIndex = 0;
	}

	public ArrayList<Statement> parseFile() {
		ArrayList<Statement> ret = new ArrayList<>();
		while (currentIndex < tokens.size()) {
			Statement next = parseStatement();
			if (next == null) {
				continue;
			}
			ret.add(next);
		}
		return ret;
	}

	private Statement parseStatement() {
		Token next = getNextToken();

		switch (next.type) {
			case IF:
				return parseIf();
			case WHILE:
				return parseWhile();
			case FOR:
				return parseFor();
			case EOF:
				return null;
			case CURL_LEFT:
				// Back up the parser so that parseBlock can consume the curl again.
				currentIndex -= 1;
				var block = parseBlock();
				return new Statement.BlockStatement(block);
			case ANGLE_RIGHT:
				// This is a command.
				var expression = parseExpression();
				if (expression == null) {
					throw new RuntimeException("Expected expression after '>'");
				}
				var ret = new Statement.ExpressionStatement(expression, true);
				mustMatch(TokenType.SEMICOLON);
				return ret;
			case SEMICOLON:
				// An empty / no-op statement.
				return new Statement.Empty();
			case NAME:
				// This could be either an assignment, a rule, or an expression.
				currentIndex -= 1;

				// Scan ahead. An EQUALS token is only allowed inside an assigment, COLON
				// is only allowed in rules, and expressions are terminated by ";", so we
				// can scan until we find EQUALS, COLON, or SEMICOLON.
				boolean isAssignment = false;
				boolean isExpression = false;
				boolean isRule = false;
				for (int i = currentIndex; i < tokens.size(); i++) {
					Token current = tokens.get(i);
					if (current.type == TokenType.EQUALS) {
						isAssignment = true;
						break;
					}
					if (current.type == TokenType.SEMICOLON) {
						isExpression = true;
						break;
					}
				}
				if (isAssignment) {
					return parseAssignment();
				} else if (isExpression) {
					return new Statement.ExpressionStatement(parseExpression(), false);
				} else {
					throw new RuntimeException("Statement not terminated by ';'");
				}
			default:
				// It must be an expression or a rule.
				currentIndex -= 1;
				var firstExpression = parseExpression();
				if (tryMatchToken(TokenType.SEMICOLON)) {
					return new Statement.ExpressionStatement(firstExpression, false);
				}
				// It must be a rule.
				ArrayList<Expression> targets = new ArrayList<>();
				targets.add(firstExpression);
				while (tryMatchToken(TokenType.COMMA)) {
					targets.add(parseExpression());
				}
				mustMatch(TokenType.COLON);
				ArrayList<Expression> deps = new ArrayList<>();
				if (peek().type != TokenType.CURL_LEFT) {
					deps.add(parseExpression());
					while (tryMatchToken(TokenType.COMMA)) {
						deps.add(parseExpression());
					}
				}
				var ruleBlock = parseBlock();
				return new Statement.Rule(targets, deps, ruleBlock);
		}
	}

	private Statement.Assignment parseAssignment() {
		Token name = getNextToken();
		if (name.type != TokenType.NAME) {
			throw new RuntimeException("Expected NAME, found " + name.type);
		}
		Expression leftSide = parseIndex(new Expression.VariableName(name.text));
		mustMatch(TokenType.EQUALS);
		Expression rightSide = parseExpression();
		mustMatch(TokenType.SEMICOLON);
		return new Statement.Assignment(leftSide, rightSide);
	}

	private Expression parseExpression() {
		Expression ret = parseAnd();
		while (tryMatchToken(TokenType.DOUBLE_OR)) {
			Expression right = parseAnd();
			ret = new Expression.Binary(ret, BinaryOperator.OR, right);
		}
		return ret;
	}

	private Expression parseAnd() {
		Expression ret = parseEquality();
		while (tryMatchToken(TokenType.DOUBLE_AND)) {
			Expression right = parseEquality();
			ret = new Expression.Binary(ret, BinaryOperator.AND, right);
		}
		return ret;
	}

	private Expression parseEquality() {
		Expression ret = parseComparison();
		if (tryMatchToken(TokenType.DOUBLE_EQUAL)) {
			Expression right = parseComparison();
			return new Expression.Binary(ret, BinaryOperator.EQUAL, right);
		}
		if (tryMatchToken(TokenType.BANG_EQUAL)) {
			Expression right = parseComparison();
			return new Expression.Binary(ret, BinaryOperator.NOT_EQUAL, right);
		}
		return ret;
	}

	private Expression parseComparison() {
		Expression ret = parseAddSub();
		if (tryMatchToken(TokenType.ANGLE_LEFT)) {
			var right = parseAddSub();
			return new Expression.Binary(ret, BinaryOperator.LESS, right);
		}
		if (tryMatchToken(TokenType.ANGLE_RIGHT)) {
			var right = parseAddSub();
			return new Expression.Binary(ret, BinaryOperator.GREATER, right);
		}
		if (tryMatchToken(TokenType.LESS_EQUAL)) {
			var right = parseAddSub();
			return new Expression.Binary(ret, BinaryOperator.LESS_EQUAL, right);
		}
		if (tryMatchToken(TokenType.GREATER_EQUAL)) {
			var right = parseAddSub();
			return new Expression.Binary(ret, BinaryOperator.GREATER_EQUAL, right);
		}
		return ret;
	}

	private Expression parseAddSub() {
		var ret = parseMultDiv();

		while (peek().type == TokenType.PLUS || peek().type == TokenType.MINUS) {
			BinaryOperator op;
			if (tryMatchToken(TokenType.PLUS)) {
				op = BinaryOperator.ADD;
			} else {
				mustMatch(TokenType.MINUS);
				op = BinaryOperator.SUB;
			}
			var right = parseMultDiv();
			ret = new Expression.Binary(ret, op, right);
		}

		return ret;
	}

	private Expression parseMultDiv() {
		var ret = parseUnary();

		while (peek().type == TokenType.STAR || peek().type == TokenType.SLASH) {
			BinaryOperator op;
			if (tryMatchToken(TokenType.STAR)) {
				op = BinaryOperator.MULT;
			} else {
				mustMatch(TokenType.SLASH);
				op = BinaryOperator.DIV;
			}
			var right = parseUnary();
			ret = new Expression.Binary(ret, op, right);
		}

		return ret;
	}

	private Expression parseUnary() {
		if (tryMatchToken(TokenType.BANG)) {
			var rest = parseUnary();
			return new Expression.Unary(rest, UnaryOperator.NOT);
		}
		if (tryMatchToken(TokenType.MINUS)) {
			var rest = parseUnary();
			return new Expression.Unary(rest, UnaryOperator.NEGATE);
		}
		return parsePrimary();
	}

	private Expression parsePrimary() {
		var nextToken = getNextToken();
		switch (nextToken.type) {
			case STRING:
				return new Expression.StringLiteral(nextToken.text);
			case INT:
				return new Expression.Number(Integer.parseInt(nextToken.text));
			case DOUBLE:
				return new Expression.Number(Double.parseDouble(nextToken.text));
			case TRUE:
				return new Expression.BooleanLiteral(true);
			case FALSE:
				return new Expression.BooleanLiteral(false);
			case PAREN_LEFT:
				var expression = parseExpression();
				mustMatch(TokenType.PAREN_RIGHT);
				if (tryMatchToken(TokenType.BRACKET_LEFT)) {
					return parseIndex(expression);
				} else if (tryMatchToken(TokenType.PAREN_LEFT)) {
					var args = parseArgs();
					return new Expression.FunctionCall(expression, args);
				} else {
					return expression;
				}
			case NAME:
				var name = new Expression.VariableName(nextToken.text);
				if (peek().type == TokenType.BRACKET_LEFT) {
					return parseIndex(name);
				} else if (tryMatchToken(TokenType.PAREN_LEFT)) {
					var args = parseArgs();
					return new Expression.FunctionCall(name, args);
				} else {
					return name;
				}
			default:
				throw new RuntimeException("Unrecognized token for Primary: " + nextToken);
		}
	}

	private Expression parseIndex(Expression base) {
		Expression ret = base;
		while (tryMatchToken(TokenType.BRACKET_LEFT)) {
			Expression inner = parseExpression();
			mustMatch(TokenType.BRACKET_RIGHT);
			ret = new Expression.Index(ret, inner);
		}

		return ret;
	}

	private ArrayList<Expression> parseArgs() {
		ArrayList<Expression> ret = new ArrayList<>();
		while (tryMatchToken(TokenType.COMMA)) {
			ret.add(parseExpression());
		}
		mustMatch(TokenType.PAREN_RIGHT);
		return ret;
	}

	private Statement.ForLoop parseFor() {
		mustMatch(TokenType.PAREN_LEFT);
		var setup = parseAssignment();
		mustMatch(TokenType.SEMICOLON);
		var conditional = parseExpression();
		mustMatch(TokenType.SEMICOLON);
		var increment = parseAssignment();
		var block = parseBlock();
		return new Statement.ForLoop(block, setup, conditional, increment);
	}

	private Statement.WhileLoop parseWhile() {
		var condition = parseExpression();
		var block = parseBlock();
		return new Statement.WhileLoop(condition, block);
	}

	private ArrayList<Statement> parseBlock() {
		mustMatch(TokenType.CURL_LEFT);
		ArrayList<Statement> ret = new ArrayList<>();
		while (!tryMatchToken(TokenType.CURL_RIGHT)) {
			Statement current = parseStatement();
			if (current == null) {
				break;
			}
			ret.add(current);
		}

		return ret;
	}

	private Statement.If parseIf() {
		Expression condition = parseExpression();
		ArrayList<Statement> mainBlock = parseBlock();
		if (peek().type == TokenType.ELSE) {
			advance();
			ArrayList<Expression> ifConditions = new ArrayList<>();
			ArrayList<ArrayList<Statement>> ifBlocks = new ArrayList<>();
			ifConditions.add(condition);
			ifBlocks.add(mainBlock);
			while (tryMatchToken(TokenType.IF)) {
				mustMatch(TokenType.PAREN_LEFT);
				ifConditions.add(parseExpression());
				mustMatch(TokenType.PAREN_RIGHT);
				ifBlocks.add(parseBlock());
				if (!tryMatchToken(TokenType.ELSE)) {
					return new Statement.If(ifConditions, ifBlocks);
				}
			}

			// One final else block.
			var elseBlock = parseBlock();
			return new Statement.If(ifConditions, ifBlocks, elseBlock);
		} else {
			// Just a normal if statement.
			return new Statement.If(condition, mainBlock);
		}
	}

	private Token getNextToken() {
		if (currentIndex >= tokens.size()) {
			return new Token('\0', TokenType.EOF);
		}
		Token ret = tokens.get(currentIndex);
		currentIndex += 1;
		return ret;
	}

	private void advance() {
		getNextToken();
	}

	private boolean tryMatchToken(TokenType type) {
		if (peek().type == type) {
			advance();
			return true;
		} else {
			return false;
		}
	}

	private Token peek() {
		if (currentIndex >= tokens.size()) {
			return new Token('\0', TokenType.EOF);
		}
		return tokens.get(currentIndex);
	}

	private void mustMatch(TokenType expectedType) {
		var tokenType = getNextToken().type;
		if (tokenType != expectedType) {
			throw new RuntimeException("Expected " + expectedType + " token, found " + tokenType);
		}
	}
}
