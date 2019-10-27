package jmaker.parser;

import java.util.Objects;

public class Token {
	public final String text;
	private Span span;
	public final TokenType type;

	public Token(String text, TokenType type) {
		this.text = text;
		this.type = type;
		span = null;
	}

	public Token(char c, TokenType type) {
		this(Character.toString(c), type);
	}

	public Span getSpan() {
		return span;
	}

	public void setSpan(Span span) {
		if (span == null) {
			throw new RuntimeException("Can't set the span to null");
		}
		if (this.span != null) {
			throw new RuntimeException("Can't modify the span");
		}
		this.span = span;
	}

	@Override
	public String toString() {
		return "{'" + text + "', " + type + ", " + span + "}";
	}

	@Override
	public int hashCode() {
		return Objects.hash(span, text, type);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Token)) {
			return false;
		}
		Token other = (Token) obj;
		return Objects.equals(span, other.span) && Objects.equals(text, other.text) && type == other.type;
	}
}
