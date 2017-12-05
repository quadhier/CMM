package Lexer;


public class Token
{

    private int tag; // tag indicating the type of the token
	private int line; // line number in the source code file
	private int startpos; // start position of the token
    private int endpos; // end postion of the token

    private String lexeme;

    Token(int tag, int line, int startpos, int endpos) {
        lexeme = null;
        this.tag = tag;
        this.line = line;
        this.startpos = startpos;
        this.endpos = endpos;
    }

    Token(String lexeme, int tag, int line, int startpos, int endpos) {
        this.lexeme = lexeme;
        this.tag = tag;
        this.line = line;
        this.startpos = startpos;
        this.endpos = endpos;
    }

    public String getLexeme() {
        //
        // !!! for keywords and punctuators, the lexeme is null
        //
        if(tag < 256) {
            return Character.toString((char)tag);
        } else {
            String lex = Tag.tagKeywords.get(new Integer(tag));
            if(lex == null)
                return lexeme;
            else
                return lex;
        }
    }

    public int getTag() {
        return tag;
    }

    public int getLine() {
        return line;
    }

    public int getStartpos() {
        return startpos;
    }

    public int getEndpos() {
        return endpos;
    }
}







