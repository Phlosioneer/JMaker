package jmaker.parser;

public enum TokenType {
	// Singles
	// ( ) [ ] < >
	PAREN_LEFT, PAREN_RIGHT, BRACKET_LEFT, BRACKET_RIGHT, ANGLE_LEFT, ANGLE_RIGHT,
	// , - + = ; / * : !
	COMMA, MINUS, PLUS, EQUALS, SEMICOLON, SLASH, STAR, COLON, BANG,
	// { }
	CURL_LEFT, CURL_RIGHT,

	// Doubles
	// == != <= >= && ||
	DOUBLE_EQUAL, BANG_EQUAL, LESS_EQUAL, GREATER_EQUAL, DOUBLE_AND, DOUBLE_OR,

	// Literals
	STRING, INT, DOUBLE, TRUE, FALSE,

	// Keywords
	IF, WHILE, FOR, ELSE,

	// A variable or function name
	NAME,

	EOF
}
