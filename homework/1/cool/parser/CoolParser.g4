parser grammar CoolParser;

options {
	tokenVocab = CoolLexer;
}

@header {
    package cool.parser;
}

program: (classDefine SEMICOLON)* EOF;

classDefine:
	CLASS TYPEID (INHERITS TYPEID)? OPENING_BRACE (
		feature SEMICOLON
	)* CLOSING_BRACE;

feature:
	OBJECTID OPENING_PARENTHESIS (formal (COMMA formal)*)? CLOSING_PARENTHESIS COLON TYPEID
		OPENING_BRACE expression CLOSING_BRACE			# method
	| OBJECTID COLON TYPEID (ASSIGNMENT expression)?	# property;

formal: OBJECTID COLON TYPEID;
/* method argument */

expression:
	expression (AT TYPEID)? DOT OBJECTID OPENING_PARENTHESIS (
		expression (COMMA expression)*
	)? CLOSING_PARENTHESIS # methodCall
	| OBJECTID OPENING_PARENTHESIS (
		expression (COMMA expression)*
	)? CLOSING_PARENTHESIS									# ownMethodCall
	| IF expression THEN expression ELSE expression FI		# if
	| WHILE expression LOOP expression POOL					# while
	| OPENING_BRACE (expression SEMICOLON)+ CLOSING_BRACE	# block
	| LET OBJECTID COLON TYPEID (ASSIGNMENT expression)? (
		COMMA OBJECTID COLON TYPEID (ASSIGNMENT expression)?
	)* IN expression # letIn
	| CASE expression OF (
		OBJECTID COLON TYPEID CASE_ARROW expression SEMICOLON
	)+ ESAC													# case
	| NEW TYPEID											# new
	| INTEGER_NEGATIVE expression							# negative
	| ISVOID expression										# isvoid
	| expression MULTIPLY expression						# multiply
	| expression DIVISION expression						# division
	| expression ADD expression								# add
	| expression MINUS expression							# minus
	| expression LESS_THAN expression						# lessThan
	| expression LESS_OR_EQUAL expression					# lessEqual
	| expression EQUAL expression							# equal
	| NOT expression										# boolNot
	| OPENING_PARENTHESIS expression CLOSING_PARENTHESIS	# parentheses
	| OBJECTID												# id
	| INT													# int
	| STRING												# string
	| TRUE													# true
	| FALSE													# false
	| OBJECTID ASSIGNMENT expression						# assignment;
