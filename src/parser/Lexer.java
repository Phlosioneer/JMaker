package parser;

import java.util.ArrayList;

public class Lexer {
	private String text;
	private int currentLine;
	private int currentColumn;
	private int currentIndex;

	public Lexer(String text) {
		this.text = text;
		currentLine = 0;
		currentColumn = 0;
		currentIndex = 0;
	}

	public ArrayList<Token> scanAll() {
		ArrayList<Token> ret = new ArrayList<>();
		while (currentIndex < text.length()) {
			Span span = new Span(currentLine, currentColumn);
			Token next = scanToken();
			if (next == null) {
				continue;
			}
			span.setEnd(currentLine, currentColumn);
			next.setSpan(span);
			ret.add(next);
		}

		return ret;
	}

	private Token scanToken() {
		char c = getNextChar();
		switch (c) {
			case '(':
				return new Token(c, TokenType.PAREN_LEFT);
			case ')':
				return new Token(c, TokenType.PAREN_RIGHT);
			case '[':
				return new Token(c, TokenType.BRACKET_LEFT);
			case ']':
				return new Token(c, TokenType.BRACKET_RIGHT);
			case ',':
				return new Token(c, TokenType.COMMA);
			case '-':
				return new Token(c, TokenType.MINUS);
			case '+':
				return new Token(c, TokenType.PLUS);
			case ';':
				return new Token(c, TokenType.SEMICOLON);
			case '*':
				return new Token(c, TokenType.STAR);
			case ':':
				return new Token(c, TokenType.COLON);
			case '{':
				return new Token(c, TokenType.CURL_LEFT);
			case '}':
				return new Token(c, TokenType.CURL_RIGHT);
			case '<':
				if (peek() == '=') {
					getNextChar();
					return new Token(c, TokenType.LESS_EQUAL);
				} else {
					return new Token(c, TokenType.ANGLE_LEFT);
				}
			case '>':
				if (peek() == '=') {
					getNextChar();
					return new Token(c, TokenType.GREATER_EQUAL);
				} else {
					return new Token(c, TokenType.ANGLE_RIGHT);
				}
			case '=':
				if (peek() == '=') {
					getNextChar();
					return new Token(c, TokenType.DOUBLE_EQUAL);
				} else {
					return new Token(c, TokenType.EQUALS);
				}
			case '!':
				if (peek() == '=') {
					getNextChar();
					return new Token(c, TokenType.BANG_EQUAL);
				} else {
					return new Token(c, TokenType.BANG);
				}
			case '/':
				if (peek() == '/') {
					// Consume until end of line, including the \n.
					while (getNextChar() != '\n') {
						// Also break if we hit end of file.
						if (peek() == '\0') {
							break;
						}
					}

					// Return nothing.
					return null;
				} else if (peek() == '*') {
					// Consume until we see a '*' followed by a '/'.
					while (true) {
						c = getNextChar();
						if (c == '*' && peek() == '/') {
							break;
						}
						if (c == '\0') {
							// Also break if we hit end of file.
							break;
						}
					}

					// Return nothing.
					return null;
				} else {
					return new Token(c, TokenType.SLASH);
				}
			case ' ':
			case '\r':
			case '\t':
			case '\n':
				return null;
			case '"':
				return parseString();
		}
		if (Character.isAlphabetic(c) || c == '_') {
			return parseSymbolOrKeyword(c);
		}
		if (Character.isDigit(c)) {
			return parseNumber(c);
		}
		throw new RuntimeException("Unrecognized character: " + c + " (" + (int) c + ")");
	}

	private Token parseSymbolOrKeyword(char firstChar) {
		StringBuilder ret = new StringBuilder();
		ret.append(firstChar);
		while (true) {
			char c = peek();
			if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
				getNextChar();
				ret.append(c);
			} else {
				break;
			}
		}
		String finalString = ret.toString();
		if (finalString.equals("if")) {
			return new Token(finalString, TokenType.IF);
		}
		if (finalString.equals("do")) {
			return new Token(finalString, TokenType.DO);
		}
		if (finalString.equals("for")) {
			return new Token(finalString, TokenType.FOR);
		}
		if (finalString.equals("while")) {
			return new Token(finalString, TokenType.WHILE);
		}
		return new Token(ret.toString(), TokenType.NAME);
	}

	private Token parseNumber(char firstChar) {
		StringBuilder ret = new StringBuilder();
		ret.append(firstChar);
		boolean haveSeenDot = false;
		while (true) {
			char c = peek();
			if (Character.isDigit(c)) {
				getNextChar();
				ret.append(c);
			} else if (c == '.' && !haveSeenDot) {
				getNextChar();
				ret.append('.');
				haveSeenDot = true;
			} else {
				break;
			}
		}

		if (haveSeenDot) {
			return new Token(ret.toString(), TokenType.DOUBLE);
		} else {
			return new Token(ret.toString(), TokenType.INT);
		}
	}

	private Token parseString() {
		StringBuilder ret = new StringBuilder();
		while (true) {
			char c = getNextChar();
			if (c == '"' || c == '\0') {
				break;
			}
			if (c == '\\') {
				c = getNextChar();
				if (c == '\0') {
					ret.append('\\');
					break;
				}
				switch (c) {
					case '\\':
						ret.append('\\');
						break;
					case 'n':
					case '\n':
						ret.append('\n');
						break;
					case 'r':
						ret.append('\r');
						break;
					case 't':
					case '\t':
						ret.append('\t');
						break;
					default:
						throw new RuntimeException("Unrecognized escape code: \\" + c);
				}
			} else if (c == '\n') {
				throw new RuntimeException("String isn't terminated");
			} else {
				ret.append(c);
			}
		}

		return new Token(ret.toString(), TokenType.STRING);
	}

	private char peek() {
		if (currentIndex >= text.length()) {
			return '\0';
		} else {
			return text.charAt(currentIndex);
		}
	}

	private char getNextChar() {
		if (currentIndex >= text.length()) {
			return '\0';
		}

		char ret = text.charAt(currentIndex);
		currentIndex += 1;
		currentColumn += 1;
		if (ret == '\n') {
			currentLine += 1;
			currentColumn = 0;
		}
		return ret;
	}
}
