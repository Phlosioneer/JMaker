
parser grammar JMakerParser;

options { tokenVocab = JMakerLexer; }

file : statement*;

statement :
	  assignment SEMICOLON
	| ifStatement
	| whileStatement
	| forStatement
	| ruleStatement
	| ANGLE_RIGHT? expression SEMICOLON
	| RETURN expression? SEMICOLON
	| block
	| functionDef;

assignment : NAME assignOp expression;
assignOp : EQUALS | PLUS_EQUAL | MINUS_EQUAL | STAR_EQUAL | SLASH_EQUAL | AMP_EQUAL | PIPE_EQUAL; 

ifStatement : IF ifCondition=expression ifBlock=block elseIfStatement* (ELSE elseBlock=block)?;
elseIfStatement : ELSE IF expression block; 

whileStatement : WHILE expression block;

forStatement : forManual | forEach;
forManual : FOR PAREN_LEFT
	init=simpleAssignment? SEMICOLON
	condition=expression? SEMICOLON
	update=assignment? PAREN_RIGHT block;
forEach : FOR PAREN_LEFT simpleAssignment PAREN_RIGHT block;
simpleAssignment : NAME EQUALS expression;

ruleStatement : targets=expressionList COLON deps=expressionList? block;

functionDef : FUNCTION NAME PAREN_LEFT (funcDefArg (COMMA funcDefArg)*)? PAREN_RIGHT block;
funcDefArg : NAME STAR?;

block : CURL_LEFT statement* CURL_RIGHT;

expressionList : expression (COMMA expression)*;

expression
	: unambiguousVar
	| primary
	| lambda
	| unop=(BANG | MINUS) right=expression
	| left=expression binop=(STAR | FORWARD_SLASH) right=expression
	| left=expression binop=(PLUS | MINUS) right=expression
	| left=expression
		binop=
			( DOUBLE_EQUAL | BANG_EQUAL | ANGLE_LEFT | ANGLE_RIGHT 
			| LESS_EQUAL | GREATER_EQUAL)
		right=expression
	| left=expression binop=(DOUBLE_AMP | DOUBLE_PIPE) right=expression
	| left=expression binop=PIPE right=expression;
	
/*
expression
	: left=expression binop = PIPE right=expression
	| left=expression binop = (DOUBLE_AMP | DOUBLE_PIPE) right=expression
	// Note: comparison operators can't chain.
	| left=expression
		binop =
			( DOUBLE_EQUAL | BANG_EQUAL | ANGLE_LEFT | ANGLE_RIGHT
			| LESS_EQUAL | GREATER_EQUAL)
		right=expression
	| expression_other;
expression_other
	: left=expression_other binop=(PLUS | MINUS) right=expression_other
	| left=expression_other binop=(STAR | FORWARD_SLASH) right=expression_other
	| unary
	| primary
	| unambiguousVar
	| lambda;
*/

primary : literal | PAREN_LEFT expression PAREN_RIGHT | functionCall;

unambiguousVar : PAREN_LEFT expression PAREN_RIGHT | NAME | index;

functionCall : unambiguousVar PAREN_LEFT expressionList? PAREN_RIGHT;

lambda : lambdaArgs ARROW_LEFT (expression | block);
lambdaArgs : NAME | PAREN_LEFT (NAME (COMMA NAME)* COMMA?)? PAREN_RIGHT;

index : (PAREN_LEFT expression PAREN_RIGHT | NAME) indexBrackets+;
indexBrackets : BRACKET_LEFT ( only=expression | start=expression? COLON end=expression? ) BRACKET_RIGHT;

literal : arrayLiteral | dictLiteral | TRUE | FALSE | STRING | INTEGER | FLOAT;
arrayLiteral : BRACKET_LEFT expressionList? COMMA? BRACKET_RIGHT;
dictLiteral : CURL_LEFT (keyValuePair (COMMA keyValuePair)*)? COMMA? CURL_RIGHT;
keyValuePair : key=expression COLON value=expression;