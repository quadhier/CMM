package Lexer;

import Buffer.CharBuffer;
import Failure.Failure;

import java.io.FileReader;

public class Lexer {

    private String filepath;
    private CharBuffer cbuffer;
    private int c;
    private int line;
    private int pos;

    public Lexer(String fpath, FileReader reader) {

        filepath = fpath;
        this.cbuffer = new CharBuffer(reader, 10);
        c = 0;
        line = 1;
        pos = 0;

    }

    private int peek(int n) {
        return cbuffer.peek(n);
    }

    private void extract(int n) {
        cbuffer.extract(n);
        pos += n;
    }


    public Token scan() {

        while(true) {

            c = peek(1);


            // deal with whitespace characters
            while (Character.isWhitespace(c)) {
                // init a new line
                if (c == '\n') {
                    line++;
                    pos = -1; // because the following extract will add one
                }
                extract(1);
                c = peek(1);
            }

            if(c == 0) {
                return null;
            }


            // deal with comment
            if (c == '/') {
                int startline = line;
                int startpos = pos + 1;
                extract(1);
                c = peek(1);
                // single line comment
                if (c == '/') {
                    while (c != '\n') {
                        extract(1);
                        c = peek(1);
                        //
                        // may meet end of file
                        // then return null and stop the lexer and parser
                        //
                        if(c == 0) {
                            return null;
                        }
                    }
                    extract(1);
                    line++;
                    pos = 0;
                    continue;
                // multi line comment
                } else if (c == '*') {
                    extract(1);
                    c = peek(1);
                    while (c != '*' || peek(2) != '/') {
                        // init a new line
                        // and immediately continue to see if the condition satisfied
                        if (c == '\n') {
                            line++;
                            pos = -1;
                            extract(1);
                            c = peek(1);
                            continue;
                        }
                        // unterminated multiline comment reaches end of file
                        if (c == 0) {
                            Failure.addFailure(filepath, startline, startpos, Failure.ERROR, "unterminated multiline comment");
                            //
                            // This will cause the parser and lexer to stop
                            //
                            return null;
                        }
                        extract(1);
                        c = peek(1);
                    }
                    extract(2);
                    continue;
                } else  {
                    return new Token('/', line, pos, pos);
                }
            } // end of comment


            // deal with number
            if(Character.isDigit(c) || c == '.') {
                int startpos = pos + 1;
                boolean hasDot = (c == '.'); // if it is a floating point number
                StringBuilder num = new StringBuilder();
                num.append((char)c);
                extract(1);
                c = peek(1);
                while(true) {
                    if(Character.isDigit(c)) {
                        num.append((char)c);
                        extract(1);
                        c = peek(1);
                    } else if(!hasDot && c == '.') {
                        hasDot = true;
                        num.append((char)c);
                        extract(1);
                        c = peek(1);
                    // otherwise, return the constant generated so far
                    } else {
                        int endpos = pos;
                        // invalid suffix
                        if(Character.isAlphabetic(c) || c == '.') {
                            StringBuilder suffix = new StringBuilder();
                            suffix.append((char)c);
                            extract(1);
                            c = peek(1);
                            while (true) {
                                if (Character.isDigit(c) || Character.isAlphabetic(c) || c == '.') {
                                    suffix.append((char) c);
                                    extract(1);
                                    c = peek(1);
                                } else {
                                    break;
                                }
                            }
                            Failure.addFailure(filepath, line, endpos + 1, Failure.ERROR, "invalid suffix '" + suffix.toString() + "'");
                        }

                        if(hasDot) {
                            return new FltConstant(num.toString(), Tag.DCONST, line, startpos, endpos);
                        } else {
                            return new IntConstant(num.toString(), Tag.ICONST, line, startpos, endpos);
                        }

                    }

                }
            } // end of digit


//            // deal with string
//            if(c == '"') {
//                int startpos = pos + 1;
//                int startline = line;
//                StringBuilder str = new StringBuilder();
//                extract(1);
//                c = peek(1);
//                while(c != '"') {
//                    if(c ==  0 || c == '\n') {
//                        Failure.addFailure(filepath, startline, startpos, Failure.ERROR, "missing terminating '\"' character");
//                        return new StrLiteral(str.toString(), Tag.SLITRL, line, startpos, pos);
//                    } else {
//                        str.append((char)c);
//                        extract(1);
//                        c = peek(1);
//                    }
//                }
//                extract(1);
//                return new StrLiteral(str.toString(), Tag.SLITRL, line, startpos, pos);
//            }


//            // deal with character constant
//            if(c == '\'') {
//                int startpos = pos + 1;
//                extract(1);
//                c = peek(1);
//                Token ch = null;
//                if(c == '\'') {
//                    //
//                    // return a token with NUL character instead of a null pointer
//                    //
//                    ch = new Token("", Tag.CCONST, line , pos, pos + 1);
//                    Failure.addFailure(filepath, line, pos, Failure.ERROR, "empty character constant");
//                    extract(1);
//                    return ch;
//                }
//                ch = new Token(Character.toString((char)c), Tag.CCONST, line, pos, pos + 2);
//                extract(1);
//                c = peek(1);
//                // avoid adding errors in the while loop
//                if(c != '\''){
//                    Failure.addFailure(filepath, line, startpos, Failure.ERROR, "multi character constant not allowed");
//                }
//                while(c != '\'') {
//                    // If it is newline or end-of-file return without extracting the character
//                    // It doesn't matter whether line and pos need to be updated in front of an end-of-file character
//                    if(c == '\n' || c == 0) {
//                        line++;
//                        pos = 0;
//                        Failure.addFailure(filepath, line, startpos, Failure.ERROR, "missing terminating ''' character");
//                        return ch;
//                    }
//                    extract(1);
//                    c = peek(1);
//                }
//                extract(1);
//                return ch;
//            }


            // deal with identifier
            if(Character.isAlphabetic(c) || c == '_') {
                int startpos = pos + 1;
                StringBuilder idtTmp = new StringBuilder();
                while(Character.isAlphabetic(c) || Character.isDigit(c) || c == '_') {
                    idtTmp.append((char)c);
                    extract(1);
                    c = peek(1);
                }
                String idt = idtTmp.toString();
                Integer tag = Tag.strKeywords.get(idt);
                if(tag == null) {
                    return new Identifer(idt, Tag.IDT, line, startpos, pos);
                } else {
                    return new Token(tag, line, startpos, pos);
                }
            }

            // deal with punctuators
            int tmp = peek(2);
            int tmpPos = pos;
            if(tmp == '='){
                switch (c) {
                    case '+':
                        extract(2);
                        return new Token(Tag.PLASN, line, tmpPos + 1, tmpPos + 2);
                    case '-':
                        extract(2);
                        return new Token(Tag.MIASN, line, tmpPos + 1, tmpPos + 2);
                    case '*':
                        extract(2);
                        return new Token(Tag.MLASN, line, tmpPos + 1, tmpPos + 2);
                    case '/':
                        extract(2);
                        return new Token(Tag.QTASN, line, tmpPos + 1, tmpPos + 2);
                    case '%':
                        extract(2);
                        return new Token(Tag.RDASN, line, tmpPos + 1, tmpPos + 2);
                    case '=':
                        extract(2);
                        return new Token(Tag.EQ, line, tmpPos + 1, tmpPos + 2);
                    case '!':
                        extract(2);
                        return new Token(Tag.NE, line, tmpPos + 1, tmpPos + 2);
                    case '<':
                        extract(2);
                        return new Token(Tag.LE, line, tmpPos + 1, tmpPos + 2);
                    case '>':
                        extract(2);
                        return new Token(Tag.GE, line, tmpPos + 1, tmpPos + 2);
                    default:
                            break;
                }
            } else if(tmp == '|' && c == '|') {
                extract(2);
                return new Token(Tag.OR, line, tmpPos + 1, tmpPos + 2);
            } else if(tmp == '&' || c == '&') {
                extract(2);
                return new Token(Tag.AND, line, tmpPos + 1, tmpPos + 2);
            }


            extract(1);
            if(c > 255)
                c = Tag.UNKNOWN;
            return new Token(c, line, pos, pos);

        } // end of while

    } // end of scan

} // end of class
