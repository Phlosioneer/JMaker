lexer grammar JMakerLexer;

// Whitespace
WHITESPACE : [ \t\r\n]+ -> channel(HIDDEN);
LINE_COMMENT : '//' .*? '\r'? '\n' -> channel(HIDDEN);
BLOCK_COMMENT : '/*' .*? '*/' -> channel(HIDDEN);

// Keywords

IF : 'if';
WHILE : 'while';
FOR : 'for';
ELSE : 'else';
FUNCTION : 'function';
TRUE : 'true';
FALSE : 'false';
VAR : 'var';

// Symbols

PAREN_LEFT : '(';
PAREN_RIGHT : ')';
BRACKET_LEFT : '[';
BRACKET_RIGHT : ']';
ANGLE_LEFT : '<';
ANGLE_RIGHT : '>';
CURL_LEFT : '{';
CURL_RIGHT : '}';
SINGLE_QUOTE : '\'';
DOUBLE_QUOTE : '"';

COMMA : ',';
MINUS : '-';
PLUS : '+';
EQUALS : '=';
SEMICOLON : ';';
FORWARD_SLASH : '/';
STAR : '*';
COLON : ':';
BANG : '!';
DOT : '.';
PIPE : '|';

DOUBLE_EQUAL : '==';
BANG_EQUAL : '!=';
LESS_EQUAL : '<=';
GREATER_EQUAL : '>=';
DOUBLE_AMP : '&&';
DOUBLE_PIPE : '||';
ARROW_LEFT : '->';
PLUS_EQUAL : '+=';
MINUS_EQUAL : '-=';
STAR_EQUAL : '*=';
SLASH_EQUAL : '/=';
AMP_EQUAL : '&&=';
PIPE_EQUAL : '||=';

// Numbers

DEC_INTEGER : [1-9] ('_'? [0-9])* | '0'+;
//OCT_INTEGER : '0' [oO] [0-7] ('_'? [0-7])*;
//HEX_INTEGER : '0' [xX] [0-9a-fA-F] ('_'? [0-9a-fA-F])*;
//BIN_INTEGER : '0' [bB] [01] ('_'? [01])*;

INTEGER : DEC_INTEGER; // | OCT_INTEGER | HEX_INTEGER | BIN_INTEGER;

DEC_FLOAT : DEC_INTEGER '.' [0-9] ('_'? [0-9])*;
//OCT_FLOAT : OCT_INTEGER '.' [0-7] ('_'? [0-7])*;
//HEX_FLOAT : HEX_INTEGER '.' [0-9a-fA-F] 
//BIN_FLOAT : BIN_INTEGER '.' [01] ('_'? [01])*;

FLOAT : DEC_FLOAT; // | OCT_FLOAT | HEX_FLOAT | BIN_FLOAT;

// Strings

fragment STRING_CHAR : ~[\\] | '\\' ESCAPE_SEQUENCE;
fragment ESCAPE_SEQUENCE : ESCAPE_CHAR | UNICODE;
fragment ESCAPE_CHAR : ["'"\\/nrt];
fragment UNICODE : 'u' UNICODE_HEX UNICODE_HEX UNICODE_HEX UNICODE_HEX;
fragment UNICODE_HEX : [0-9a-fA-F];

NORMAL_STRING : (SINGLE_QUOTE STRING_CHAR* SINGLE_QUOTE) | (DOUBLE_QUOTE STRING_CHAR* DOUBLE_QUOTE);

RAW_STRING : [rR] [pP] ((SINGLE_QUOTE ( ~'\\' | '\\' SINGLE_QUOTE | '\\' .)*? SINGLE_QUOTE)
	| (DOUBLE_QUOTE ( ~'\\' | '\\' DOUBLE_QUOTE | '\\' .)*? DOUBLE_QUOTE));
	
STRING : NORMAL_STRING | RAW_STRING;

// Future goal: support unicode names
NAME : [a-zA-Z_] [a-zA-Z0-9_]*;
