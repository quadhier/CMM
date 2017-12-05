package Lexer;

import java.util.HashMap;

public class Tag {

	/* identifier */
	public static final int IDT = 256;				// identifer

	/* constant */
	public static final int ICONST = 259;			// integer constant
	public static final int DCONST = 260;			// double floating point constant
	public static final int TRUE = 261;			    // true
	public static final int FALSE = 262;			// false

	/* keyword */

    // type specifier
	public static final int INT = 265;				// int
	public static final int DOUBLE = 266;			// double
	public static final int BOOL = 267; 			// bool
    // selection
	public static final int IF = 272;				// if
	public static final int ELSE = 273;			    // else
    // loop
	public static final int WHILE = 274;			// while
    // jump
	public static final int BREAK = 275;			// break
	public static final int CONTINUE = 276;		    // continue
    // read and write
    public static final int READ = 277;             // read
    public static final int WRITE = 278;            // write

	/* operator */
	// assignment-operator
	public static final int PLASN = 285;			// +=
	public static final int MIASN = 286;			// -=
	public static final int MLASN = 287;			// *=
	public static final int QTASN = 288;			// /=
	public static final int RDASN = 289;			// %=
    // relative operator
	public static final int EQ = 290;				// ==
	public static final int NE = 291;				// !=
	public static final int LE = 293;				// <=
	public static final int GE = 295;				// >=
    // logical operator
	public static final int AND = 296;				// &&
	public static final int OR = 297;				// ||

	// unknown character
    public static final int UNKNOWN = 298;          // unknown: not extended ascii

	/*
	 *
	 * syntax part
	 *
	 */

    // declaration

	public static final int PROG = 310;			    // program
	public static final int STMT = 335;			    // statement
	public static final int DECL = 316;			    // declaration
	public static final int DECLSPEC = 313;		    // declaration-specifier
    public static final int INITLZR = 314;		    // initializer
    public static final int COMPSTMT = 315;		    // compound-statement
	public static final int ASGMSTMT = 331;		    // assignment-statement
	public static final int SELESTMT = 337;		    // selection-statement
	public static final int ITERSTMT = 338;		    // iteration-statement
	public static final int JUMPSTMT = 339;			// jump-statement
    public static final int READSTMT = 340;			// read-statement
    public static final int WRITESTMT = 341;		// write-statement

    // expression
	public static final int EXPR = 342;			    // expression
	public static final int LGOREXPR = 346;		    // logical-OR-expression
	public static final int LGANDEXPR = 347;		// logical-AND-expression
	public static final int EQEXPR = 348;			// equality-expression
	public static final int RELAEXPR = 349;		    // relational-expression
	public static final int ADTVEXPR = 350;		    // addictive-expression
	public static final int MLTVEXPR = 351;		    // multiplicative-expression
	public static final int UNARYEXPR = 344;		// unary-expression
	public static final int PRMYEXPR = 354;		    // primary-expression
	public static final int LVALEXPR = 355;		    // left-value-expression
	public static final int CONSTVAL = 356;		    // constant

    // the kind of declaration
    public static final int VARDECL = 358;        // single variable declaration
    public static final int ARRDECL = 359;        // array declaration:
    // the kind of left-value-expression
    public static final int VARLEXPR = 360;        // single variable left-variable-expression
    public static final int ARRLEXPR = 361;        // array left-variable-expression



    // the set of keywords
	// get tag from string literal
	public static HashMap<String, Integer> strKeywords;
	static {
		strKeywords = new HashMap<>();
		strKeywords.put("true", Tag.TRUE);
		strKeywords.put("false", Tag.FALSE);
		strKeywords.put("int", Tag.INT);
		strKeywords.put("double", Tag.DOUBLE);
		strKeywords.put("bool", Tag.BOOL);
		strKeywords.put("if", Tag.IF);
		strKeywords.put("else", Tag.ELSE);
		strKeywords.put("while", Tag.WHILE);
		strKeywords.put("break", Tag.BREAK);
		strKeywords.put("continue", Tag.CONTINUE);
		strKeywords.put("read", Tag.READ);
		strKeywords.put("write", Tag.WRITE);
	}

	// the set of keywords
	// get string literal from string tag
	public static HashMap<Integer, String> tagKeywords;
	static {
		tagKeywords = new HashMap<>();
		tagKeywords.put(Tag.TRUE, "true");
		tagKeywords.put(Tag.FALSE, "false");
		tagKeywords.put(Tag.INT, "int");
		tagKeywords.put(Tag.DOUBLE, "double");
		tagKeywords.put(Tag.BOOL, "bool");
		tagKeywords.put(Tag.IF, "if");
		tagKeywords.put(Tag.ELSE, "else");
		tagKeywords.put(Tag.WHILE, "while");
		tagKeywords.put(Tag.BREAK, "break");
		tagKeywords.put(Tag.CONTINUE, "continue");
		tagKeywords.put(Tag.READ, "read");
		tagKeywords.put(Tag.WRITE, "write");
		// operators
        tagKeywords.put(PLASN, "+=");
        tagKeywords.put(MIASN, "-=");
        tagKeywords.put(MLASN, "*=");
        tagKeywords.put(QTASN, "/=");
        tagKeywords.put(RDASN, "%=");
        tagKeywords.put(EQ, "==");
        tagKeywords.put(NE, "!=");
        tagKeywords.put(LE, "<=");
        tagKeywords.put(GE, ">=");
        tagKeywords.put(AND, "&&");
        tagKeywords.put(OR , "||");
	}


	private Tag() {

    }

	/**
	 * if it is int, double or bool
	 * @param tag Tag.INT, Tag.DOUBLE, Tag.BOOL
	 * @return
	 * @author comment by Carol
	 */
    public static boolean isTypeKeyword(int tag) {
	    switch(tag) {
            case Tag.INT:
            case Tag.DOUBLE:
            case Tag.BOOL:
                return true;
            default:
                return false;
        }
    }

    public static boolean isAssignmentOperator(int tag) {
	    switch(tag) {
            case '=':
            case Tag.PLASN:
            case Tag.MIASN:
            case Tag.MLASN:
            case Tag.QTASN:
            case Tag.RDASN:
                return true;
            default:
                return false;
        }
    }

    public static boolean isConstantVal(int tag) {
	    switch(tag) {
            case Tag.TRUE:
            case Tag.FALSE:
            case Tag.ICONST:
            case Tag.DCONST:
                return true;
            default:
                return false;
        }
    }

    public static boolean isResultBool(int tag) {
	    switch (tag) {
            case Tag.OR:
            case Tag.AND:
            case Tag.EQ:
            case Tag.NE:
            case Tag.LE:
            case Tag.GE:
            case '>':
            case '<':
            case '!':
                return true;
            default:
                return false;
        }
    }

}