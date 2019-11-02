
parser grammar JMakerParser;

options { tokenVocab = JMakerLexer; }

file : statement*;

statement :
	  assignment
	| ifStatement
	| whileStatement
	| forStatement
	| ruleStatement
	| ANGLE_RIGHT? expression SEMICOLON
	| block
	| functionDef;

assignment : NAME assignOp expression SEMICOLON;
assignOp : EQUALS | PLUS_EQUAL | MINUS_EQUAL | STAR_EQUAL | SLASH_EQUAL | AMP_EQUAL | PIPE_EQUAL; 

ifStatement : IF expression block (ELSE IF expression block)* (ELSE block)?;

whileStatement : WHILE expression block;

forStatement : forManual | forEach;
forManual : FOR PAREN_LEFT assignment? SEMICOLON expression? SEMICOLON assignment? PAREN_RIGHT block;
forEach : FOR PAREN_LEFT assignment PAREN_RIGHT block;

ruleStatement : expressionList COLON expressionList? block;

functionDef : FUNCTION PAREN_LEFT (funcDefArg (COMMA funcDefArg)*)? PAREN_RIGHT block;
funcDefArg : NAME STAR?;

block : CURL_LEFT statement* CURL_RIGHT;

expressionList : expression (COMMA expression)*;

expression
	: expression binop = PIPE expression
	| expression binop = (DOUBLE_AMP | DOUBLE_PIPE) expression
	// Note: comparison operators can't chain.
	| expression binop = (DOUBLE_EQUAL | BANG_EQUAL | ANGLE_LEFT | ANGLE_RIGHT | LESS_EQUAL | GREATER_EQUAL) expression
	| expression_other;
expression_other
	: expression_other binop=(PLUS | MINUS) expression_other
	| expression_other binop=(STAR | FORWARD_SLASH) expression_other
	| unary
	| primary
	| unambiguousVar;

unary : unop=(BANG | MINUS) expression;
primary : literal | PAREN_LEFT expression PAREN_RIGHT | functionCall | index;

unambiguousVar : PAREN_LEFT expression PAREN_RIGHT | NAME | index;

functionCall : unambiguousVar (DOT (NAME | index))* PAREN_LEFT expressionList? PAREN_RIGHT;


index : (PAREN_LEFT expression PAREN_RIGHT | NAME) indexBrackets+;
indexBrackets : BRACKET_LEFT ( expressionList | expression? COLON expression? ) BRACKET_RIGHT;

literal : arrayLiteral | dictLiteral | booleanLiteral | STRING | INTEGER | FLOAT;
booleanLiteral : TRUE | FALSE;
arrayLiteral : BRACKET_LEFT expressionList? COMMA? BRACKET_RIGHT;
dictLiteral : CURL_LEFT (keyValuePair (COMMA keyValuePair)*)? COMMA? CURL_RIGHT;
keyValuePair : expression COLON expression;