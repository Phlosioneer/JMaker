package tests;

import static jmaker.parser.TokenType.ANGLE_LEFT;
import static jmaker.parser.TokenType.ANGLE_RIGHT;
import static jmaker.parser.TokenType.BANG;
import static jmaker.parser.TokenType.BANG_EQUAL;
import static jmaker.parser.TokenType.BRACKET_LEFT;
import static jmaker.parser.TokenType.BRACKET_RIGHT;
import static jmaker.parser.TokenType.COLON;
import static jmaker.parser.TokenType.COMMA;
import static jmaker.parser.TokenType.CURL_LEFT;
import static jmaker.parser.TokenType.CURL_RIGHT;
import static jmaker.parser.TokenType.DOUBLE;
import static jmaker.parser.TokenType.DOUBLE_AND;
import static jmaker.parser.TokenType.DOUBLE_EQUAL;
import static jmaker.parser.TokenType.DOUBLE_OR;
import static jmaker.parser.TokenType.ELSE;
import static jmaker.parser.TokenType.EOF;
import static jmaker.parser.TokenType.EQUALS;
import static jmaker.parser.TokenType.FALSE;
import static jmaker.parser.TokenType.FOR;
import static jmaker.parser.TokenType.GREATER_EQUAL;
import static jmaker.parser.TokenType.IF;
import static jmaker.parser.TokenType.INT;
import static jmaker.parser.TokenType.LESS_EQUAL;
import static jmaker.parser.TokenType.MINUS;
import static jmaker.parser.TokenType.NAME;
import static jmaker.parser.TokenType.PAREN_LEFT;
import static jmaker.parser.TokenType.PAREN_RIGHT;
import static jmaker.parser.TokenType.PLUS;
import static jmaker.parser.TokenType.SEMICOLON;
import static jmaker.parser.TokenType.SLASH;
import static jmaker.parser.TokenType.STAR;
import static jmaker.parser.TokenType.STRING;
import static jmaker.parser.TokenType.TRUE;
import static jmaker.parser.TokenType.WHILE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import jmaker.parser.Lexer;
import jmaker.parser.TokenType;

class LexerTest {

	public LexerTest() {

	}

	@Test
	void testFile1() {
		var input = TestUtil.readFile("lexerTest1.txt");
		var lexer = new Lexer(input);
		var output = lexer.scanAll();
		assertEquals(25, output.size());

		assertEquals(TokenType.NAME, output.get(0).type);
		assertEquals(TokenType.EQUALS, output.get(1).type);
		assertEquals(TokenType.INT, output.get(2).type);
		assertEquals(TokenType.SEMICOLON, output.get(3).type);

		assertEquals(TokenType.STRING, output.get(4).type);
		assertEquals(TokenType.COLON, output.get(5).type);
		assertEquals(TokenType.STRING, output.get(6).type);
		assertEquals(TokenType.CURL_LEFT, output.get(7).type);

		assertEquals(TokenType.ANGLE_RIGHT, output.get(8).type);
		assertEquals(TokenType.STRING, output.get(9).type);
		assertEquals(TokenType.PLUS, output.get(10).type);
		assertEquals(TokenType.NAME, output.get(11).type);
		assertEquals(TokenType.BRACKET_LEFT, output.get(12).type);
		assertEquals(TokenType.INT, output.get(13).type);
		assertEquals(TokenType.BRACKET_RIGHT, output.get(14).type);
		assertEquals(TokenType.PLUS, output.get(15).type);
		assertEquals(TokenType.STRING, output.get(16).type);
		assertEquals(TokenType.PLUS, output.get(17).type);
		assertEquals(TokenType.NAME, output.get(18).type);
		assertEquals(TokenType.BRACKET_LEFT, output.get(19).type);
		assertEquals(TokenType.INT, output.get(20).type);
		assertEquals(TokenType.BRACKET_RIGHT, output.get(21).type);
		assertEquals(TokenType.SEMICOLON, output.get(22).type);

		assertEquals(TokenType.CURL_RIGHT, output.get(23).type);
		assertEquals(TokenType.EOF, output.get(24).type);

		assertEquals("TEST", output.get(0).text);
		assertEquals("2", output.get(2).text);
		assertEquals("foo.class", output.get(4).text);
		assertEquals("foo.java", output.get(6).text);
		assertEquals("javac ", output.get(9).text);
		assertEquals("deps", output.get(11).text);
		assertEquals("0", output.get(13).text);
		assertEquals(" -o ", output.get(16).text);
		assertEquals("targets", output.get(18).text);
		assertEquals("0", output.get(20).text);
	}

	@Test
	void testFile2() {
		final var expectedTokens = new TokenType[]{
			// 2
			IF, TRUE, CURL_LEFT,
			// 3
			NAME, EQUALS, STRING, SEMICOLON,
			// 4
			CURL_RIGHT, ELSE, IF, PAREN_LEFT, INT, PLUS, INT, PAREN_RIGHT, DOUBLE_EQUAL,
			INT, CURL_LEFT,
			// 5
			NAME, EQUALS, INT, SEMICOLON,
			// 6
			CURL_RIGHT, ELSE, CURL_LEFT,
			// 7
			IF, PAREN_LEFT, FALSE, PAREN_RIGHT, CURL_LEFT,
			// 8
			NAME, EQUALS, DOUBLE, PLUS, INT, SEMICOLON,
			// 9
			CURL_RIGHT, ELSE, CURL_LEFT,
			// 10
			NAME, EQUALS, NAME, BRACKET_LEFT, STRING, BRACKET_RIGHT, BRACKET_LEFT, INT,
			BRACKET_RIGHT, SEMICOLON,
			// 11
			CURL_RIGHT,
			// 12
			CURL_RIGHT,
			// 14
			FOR, PAREN_LEFT, NAME, EQUALS, INT, SEMICOLON, NAME, ANGLE_LEFT, INT,
			SEMICOLON, NAME, EQUALS, NAME, PLUS, INT, PAREN_RIGHT, CURL_LEFT,
			// 15
			STRING, PLUS, NAME, PLUS, STRING, COLON, CURL_LEFT,
			// 16
			ANGLE_RIGHT, STRING, PLUS, NAME, PLUS, STRING, PLUS, NAME, SEMICOLON,
			// 17
			CURL_RIGHT,
			// 18
			CURL_RIGHT,
			// 20
			WHILE, NAME, GREATER_EQUAL, INT, CURL_LEFT,
			// 21
			NAME, PAREN_LEFT, STRING, COMMA, NAME, PAREN_RIGHT, SEMICOLON,
			// 22
			NAME, EQUALS, INT, SEMICOLON,
			// 23
			CURL_RIGHT, EOF
		};

		var input = TestUtil.readFile("lexerTest2.txt");
		var lexer = new Lexer(input);
		var output = lexer.scanAll();
		assertEquals(expectedTokens.length, output.size());

		for (int i = 0; i < expectedTokens.length; i++) {
			assertEquals(expectedTokens[i], output.get(i).type);
		}
	}

	@Test
	void testFile3() {
		final var expectedTokens = new TokenType[]{
			// 1
			NAME, MINUS, NAME, DOUBLE_EQUAL, INT, SEMICOLON,
			// 9
			IF, PAREN_LEFT, BANG, PAREN_LEFT, STRING, BANG_EQUAL, STRING, PAREN_RIGHT, PAREN_RIGHT, CURL_LEFT,
			// 10
			NAME, EQUALS, BRACKET_LEFT, STRING, COMMA, STRING, COMMA, STRING, COMMA, STRING, COMMA,
			// 11
			STRING, COMMA, STRING, COMMA, STRING, BRACKET_RIGHT, SEMICOLON,
			// 12
			CURL_RIGHT,
			// 14
			NAME, EQUALS, INT, STAR, INT, PLUS, INT, SLASH, MINUS, INT, MINUS,
			INT, SEMICOLON,

			// 15
			NAME, EQUALS, PAREN_LEFT, INT, ANGLE_LEFT, INT, PAREN_RIGHT, DOUBLE_AND, NAME,
			ANGLE_RIGHT, NAME, DOUBLE_AND, PAREN_LEFT, NAME, LESS_EQUAL, NAME, DOUBLE_OR,
			PAREN_LEFT, DOUBLE, GREATER_EQUAL, NAME, PAREN_RIGHT, DOUBLE_AND, BANG, BANG,
			TRUE, PAREN_RIGHT, SEMICOLON,

			EOF
		};

		var input = TestUtil.readFile("lexerTest3.txt");
		var lexer = new Lexer(input);
		var output = lexer.scanAll();
		assertEquals(expectedTokens.length, output.size());

		for (int i = 0; i < expectedTokens.length; i++) {
			assertEquals(expectedTokens[i], output.get(i).type);
		}
	}

	@Test
	void testBadStrings() {
		assertThrows(RuntimeException.class, ()->new Lexer("\"end of file terminated").scanAll());
		assertThrows(RuntimeException.class, ()->new Lexer("\"bad escape:\\y\"").scanAll());
		assertThrows(RuntimeException.class, ()->new Lexer("\"raw newline\nin string\"").scanAll());
		assertThrows(RuntimeException.class, ()->new Lexer("\"end of file escaped \\").scanAll());
	}
}
