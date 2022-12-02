/* THIS WAS INSPIRED FROM: https://github.com/antlr/grammars-v4/blob/master/esolang/cool/COOL.g4
 */

lexer grammar CoolLexer;

// "Artificial" tokens
tokens {
	ERROR
}

@header {
	package cool.lexer;	
}

@members {    
	private void raiseError(String msg) {
		setText(msg);
		setType(ERROR);
	}
}

fragment A: [aA];
fragment C: [cC];
fragment D: [dD];
fragment E: [eE];
fragment F: [fF];
fragment H: [hH];
fragment I: [iI];
fragment L: [lL];
fragment N: [nN];
fragment O: [oO];
fragment P: [pP];
fragment R: [rR];
fragment S: [sS];
fragment T: [tT];
fragment U: [uU];
fragment V: [vV];
fragment W: [wW];
fragment LOWER_PRINTABLE_ASCII:
	~[\u0000-\u001F\u007F\u0080\u009F];
fragment NEWLINE: '\r'? '\n';
fragment ESCAPED: '\\' (NEWLINE | LOWER_PRINTABLE_ASCII)?;
// every backslash comes in a pair with a character it escapes
fragment STRING_CHR:
	~[\u0000-\u001F\u007F\u0080\u009F\u0022\u005c]; // everything but backslash and quotation mark
fragment TRUE: 't' R U E;
fragment FALSE: 'f' A L S E;

// KEYWORDS
CLASS: C L A S S;
ELSE: E L S E;
FI: F I;
IF: I F;
IN: I N;
INHERITS: I N H E R I T S;
ISVOID: I S V O I D;
LET: L E T;
LOOP: L O O P;
POOL: P O O L;
THEN: T H E N;
WHILE: W H I L E;
CASE: C A S E;
ESAC: E S A C;
NEW: N E W;
OF: O F;
NOT: N O T;

// PRIMITIVES
STRING:
	'"' (ESCAPED | STRING_CHR)* '"' {
		String str = getText();
		
		// Remove quotes
		str = str.substring(1, str.length() - 1);

		// Replace all unprintable characters
		str = str.replace("\\\r\n", "\r\n").
					replace("\\n", "\n").
					replace("\\t", "\t").
					replace("\\b", "\b").
					replace("\\f", "\f").
					replace("\\\n", "\n");

		// Replace any sequence of "\" and a normal character
		str = str.replaceAll("\\\\([\u0020-\u007e])", "$1");

		if (str.length() > 1024) {
            raiseError("String constant too long");
            return ;
		}

		setText(str);
	};
INT: [0-9]+;
TYPEID: [A-Z] [_0-9A-Za-z]*;
OBJECTID: [a-z] [_0-9A-Za-z]*;
BOOL: TRUE | FALSE;

// COMMENTS
fragment OPENING_COMMENT: '(*';
fragment CLOSING_COMMENT: '*)';
fragment BEGINNING_ONE_LINE_COMMENT: '--';

COMMENT:
	OPENING_COMMENT (COMMENT | .)*? (
		CLOSING_COMMENT {skip();}
		| EOF { raiseError("EOF in comment"); }
	);

ONE_LINE_COMMENT:
	BEGINNING_ONE_LINE_COMMENT .*? (NEWLINE | EOF) -> skip;

// SYMBOLS/OPERATORS
COMMA: ',';
SEMICOLON: ';';
COLON: ':';
OPENING_PARENTHESIS: '(';
CLOSING_PARENTHESIS: ')';
OPENING_BRACE: '{';
CLOSING_BRACE: '}';
ASSIGNMENT: '<-';
CASE_ARROW: '=>';
ADD: '+';
MINUS: '-';
MULTIPLY: '*';
DIVISION: '/';
LESS_THAN: '<';
LESS_OR_EQUAL: '<=';
EQUAL: '=';
INTEGER_NEGATIVE: '~';
AT: '@';
DOT: '.';

// skip spaces, tabs, newlines, note that \v is not supported in antlr
WHITESPACE: [ \t\r\n\f]+ -> skip;

// ERROR handling
INVALID_STRING:
	'"' (ESCAPED | STRING_CHR)* (
		'\u0000' (ESCAPED | STRING_CHR)* '"' { raiseError("String contains null character"); }
		| NEWLINE { raiseError("Unterminated string constant"); }
		| EOF { raiseError("EOF in string constant"); }
	);

INVALID_CLOSING_COMMENT:
	CLOSING_COMMENT { raiseError("Unmatched "+ getText()); };

EMPTY_FILE: EOF { raiseError("Empty file"); };

INVALID_CHARACTER: . { raiseError("Invalid character: " + getText()); };