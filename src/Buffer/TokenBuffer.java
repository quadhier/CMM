package Buffer;

import Lexer.Lexer;
import Lexer.Token;

import java.io.FileReader;

public class TokenBuffer {

    // lexer used to fill in the token buffer
    private Lexer lexer;

    private int buffsize;
    private int index; // current index, between 0 and 2 * buffersize - 1
    // two buffers are loaded alternately
    private Token[] buffer1;
    private Token[] buffer2;


    public TokenBuffer(String filepath, FileReader reader, int size) {

        lexer = new Lexer(filepath, reader);
        buffsize = size;
        index = -1;
        buffer1 = new Token[size];
        buffer2 = new Token[size];
        read(buffer1);
        read(buffer2);

    }

    // load the buffer
    private int read(Token[] buffer) {

        int i = 0;
        for(; i < buffsize; i++) {
            buffer[i] = lexer.scan();
            if (buffer[i] == null)
                break;
        }
        return i;

    }

    // peek the n-th token starting from the next token
    // from the buffer but not consume it
    public Token peek(int n) {

        //
        // !!! LL(buffsize) may be not enough
        //
        assert n < 2 * buffsize;

        // the current index is in the first buffer
        if(index < buffsize) {
            // the required index is in the first buffer
            if(index + n < buffsize) {
                return buffer1[index + n];
                // the required index is in the second buffer
            } else {
                return buffer2[(index + n) % buffsize];
            }
            // the current index is in the second buffer
        } else {
            // the required index is in the second buffer
            if(index + n < 2 * buffsize) {
                return buffer2[(index + n) % buffsize];
                // the required index is in the first buffer
            } else {
                return buffer1[(index + n) % buffsize];

            }
        }

    }

    // extract the n-th token starting from the next token
    // from the buffer and consume tokens till it
    public Token extract(int n) {

        Token t = peek(n);
        int len = 0;
        // buffer1 is now useless, reload it
        if(index < buffsize && index + n >= buffsize) {
            len = read(buffer1);
            // mark the end
            if(len < buffsize)
                buffer1[len < 0 ? 0 : len] = null;
        }
        // buffer2 is now useless, reload it
        if(index >= buffsize && index + n >= 2 * buffsize) {
            len = read(buffer2);
            // mark the end
            if(len < buffsize)
                buffer2[len < 0 ? 0 : len] = null;
        }
        // update index
        index = (index + n) % (2 * buffsize);
        return t;

    }





}
