parser grammar CoolParser;

options {
	tokenVocab = CoolLexer;
}

@header {
    package cool.parser;
}

program: (classDefine SEMICOLON)+ EOF;

// some weird conflict when named class
classDefine:
	CLASS name = TYPEID (INHERITS parent = TYPEID)? OPENING_BRACE (
		feature SEMICOLON
	)* CLOSING_BRACE;

feature:
	OBJECTID OPENING_PARENTHESIS (formal (COMMA formal)*)? CLOSING_PARENTHESIS COLON TYPEID
		OPENING_BRACE expression CLOSING_BRACE			# method
	| OBJECTID COLON TYPEID (ASSIGNMENT expression)?	# attribute;

formal: OBJECTID COLON TYPEID;

variable: OBJECTID COLON TYPEID (ASSIGNMENT expression)?;

alternative: OBJECTID COLON TYPEID CASE_ARROW expression;

expression:
	caller = expression (AT TYPEID)? DOT OBJECTID OPENING_PARENTHESIS (
		arguments += expression (COMMA arguments += expression)*
	)? CLOSING_PARENTHESIS # methodCall
	| OBJECTID OPENING_PARENTHESIS (
		arguments += expression (COMMA arguments += expression)*
	)? CLOSING_PARENTHESIS																		# ownMethodCall
	| IF condition = expression THEN consequent = expression ELSE ifAlternative = expression FI	# if
	| WHILE condition = expression LOOP body = expression POOL									# while
	| OPENING_BRACE (expression SEMICOLON)+ CLOSING_BRACE										# block
	| LET variable ( COMMA variable)* IN expression												# let
	| CASE expression OF (alternative SEMICOLON)+ ESAC											# case
	| NEW TYPEID																				# new
	| INTEGER_NEGATIVE expression																# negative
	| ISVOID expression																			# isvoid
	| lhs = expression op = (MULTIPLY | DIVISION) rhs = expression								# multiplication
	| lhs = expression op = (ADD | MINUS) rhs = expression										# addition
	| lhs = expression op = (LESS_THAN | LESS_OR_EQUAL | EQUAL) rhs = expression				# relational
	| NOT expression																			# not
	| OPENING_PARENTHESIS expression CLOSING_PARENTHESIS										# enclosed
	| OBJECTID																					# id
	| INT																						# int
	| STRING																					# string
	| BOOL																						# bool
	| OBJECTID ASSIGNMENT expression															# assignment;
