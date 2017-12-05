package Buffer;

import java.io.FileReader;
import java.io.IOException;

public class CharBuffer {

    // FileReader used to fill in the char buffer
    private FileReader reader;

    private int buffsize;
    private int index; // current index, between 0 and 2 * buffersize -1
    // two buffers are loaded alternately
    private char[] buffer1;
    private char[] buffer2;


    public CharBuffer(FileReader reader, int size) {

        this.reader = reader;
        this.buffsize = size;
        index = -1;
        buffer1 = new char[size];
        buffer2 = new char[size];
        read(buffer1);
        read(buffer2);

    }

    // load the buffer
    private int read(char[] buffer) {

        int len = 0;
        try {
            len = reader.read(buffer);
        } catch (IOException e) {
            e.printStackTrace();
            // add to failure list
            // ...
        }
        return len;

    }

    //
    // !!! return value is implicitly casted to int from char
    //
    // peek the n-th char starting from the next char
    // from the buffer but not consume them
    public int peek(int n) {

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

    // extract the n-th char starting from the next char
    // from the buffer and consume chars till it
    public int extract(int n) {

        int c = peek(n);
        int len = 0;
        // buffer1 is now useless, reload it
        if(index < buffsize && index + n >= buffsize) {
            len = read(buffer1);
            // mark the end
            if(len < buffsize)
                buffer1[len < 0 ? 0 : len] = 0;
        }
        // buffer2 is now useless, reload it
        if(index >= buffsize && index + n >= 2 * buffsize) {
            len = read(buffer2);
            // mark the end
            if(len < buffsize)
                buffer2[len < 0 ? 0 : len] = 0;
        }
        // update index
        index = (index + n) % (2 * buffsize);
        return c;

    }

}
