import Buffer.CharBuffer;
import Buffer.TokenBuffer;
import CMMVM.CMMVM;
import CMMVM.Program;
import Failure.Failure;
import Lexer.*;
import Parser.Parser;
import SynTree.SNode;
import SynTree.SynTree;
import javafx.scene.Parent;


import java.io.*;
import java.lang.reflect.Field;

public class CMMcompiler {

    private static FileReader getReader(String filepath) {

        FileReader reader = null;
        try {
            reader = new FileReader(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            try {
                if(reader != null) {
                    reader.close();
                }
            } catch (IOException e1) {
                e.printStackTrace();
            }

        }
        return reader;
    }


    public static void main(String[] args) {

        String filepath;
        filepath = "src/test.cmm";
//        filepath = "test_cases/test1_变量声明.cmm";
//        filepath = "test_cases/test2_一般变量赋值.cmm";
//        filepath = "test_cases/test3_数组.cmm";
//        filepath = "test_cases/test4_算术运算.cmm";
//        filepath = "test_cases/test5_IF-ELSE.cmm";
//        filepath = "test_cases/test6_WHILE.cmm";
//        filepath = "test_cases/test7_IF-ELSE与WHILE.cmm";
//        filepath = "test_cases/test8_阶乘.cmm";
//        filepath = "test_cases/test9_数组排序.cmm";
//        filepath = "src/test_semantic_errors.cmm";
//        filepath = args[0];

        FileReader reader = getReader(filepath);
        if(reader == null)
            return;

//        CharBuffer cbuffer = new CharBuffer(reader, 10);
//        while(cbuffer.peek(1) != 0) {
//            System.out.println((char)cbuffer.extract(1));
//        }

//        TokenBuffer tbuffer =  new TokenBuffer(filepath, reader, 10);
//        while(tbuffer.peek(1) != null) {
//            Token t = tbuffer.extract(1);
//            System.out.print(t.getTag() + ": " + t.getLine() + ":" + t.getStartpos() + ":" + t.getEndpos() + "  ");
//            if(t.getTag() == Tag.ICONST) {
//                System.out.println(t.getLexeme());
//            } else if(t.getTag() == Tag.DCONST) {
//                System.out.println(t.getLexeme());
//            } else if(t.getTag() == Tag.IDT) {
//                System.out.println(t.getLexeme());
//            } else {
//                if(t.getTag() < 256)
//                    System.out.println((char)t.getTag());
//                else
//                    System.out.println(t.getTag());
//            }
//        }
//        Failure.reportFailure();


        Parser parser = new Parser(filepath, reader);
        // get parse tree
        SNode root = parser.parse();
        SynTree synTree = new SynTree(root, filepath);

        // check semantic and build symbol table
        synTree.checkAndBuild();

        // report warnings and errors
        Failure.reportFailure();
        // and if there are only warnings without errors, proceed
        if(!Failure.canContinue()) {
            return;
        }

        //synTree.traverse(0);
        synTree.visit();

        // build CFG

        // optimization

        // generate code
        Program program = synTree.genBytecode();
        //program.serialize();

        // run code on the virtual machine
        CMMVM cmmvm = new CMMVM(program);
        cmmvm.execute();
    }

    public static String getFileName(String filepath) {
        int slashIdx = filepath.lastIndexOf('/');
        if(slashIdx >= 0) {
            filepath = filepath.substring(slashIdx, filepath.length() - 1);
        }
        int dotIdx = filepath.lastIndexOf('.');
        if(dotIdx > 0) {
            filepath = filepath.substring(0, dotIdx);
        }
        return filepath;
    }

    public static void printTree(String filepath) {
        FileReader reader = getReader(filepath);
        if(reader == null)
            return;
        Parser parser = new Parser(filepath, reader);
        SynTree synTree = new SynTree(parser.parse(), filepath);
        synTree.checkAndBuild();
        Failure.reportFailure();
        if (!Failure.canContinue()) {
            return;
        }
        synTree.traverse(0);
    }

    public static void compile(String filepath) {
        FileReader reader = getReader(filepath);
        if (reader == null)
            return;
        Parser parser = new Parser(filepath, reader);
        SynTree synTree = new SynTree(parser.parse(), filepath);
        synTree.checkAndBuild();
        Failure.reportFailure();
        if (!Failure.canContinue()) {
            return;
        }
        Program program = synTree.genBytecode();
        FileOutputStream fout = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(getFileName(filepath) + ".cmmbyte");
            fout = new FileOutputStream(file);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(program);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fout != null) {
                    fout.close();
                }
                if(oos != null) {
                    oos.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
    }

    public static void view(String filepath) {
        FileReader reader = getReader(filepath);
        if (reader == null)
            return;
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        Program program = null;
        try {
            fin = new FileInputStream(filepath);
            ois = new ObjectInputStream(fin);
            program = (Program) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fin != null) {
                    fin.close();
                }
                if(ois != null) {
                    ois.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if(program != null) {
            System.out.println(program.toString());
        }
    }

    public static void execute(String filepath) {
        FileReader reader = getReader(filepath);
        if (reader == null)
            return;
        FileInputStream fin = null;
        ObjectInputStream ois = null;
        Program program = null;
        try {
            fin = new FileInputStream(filepath);
            ois = new ObjectInputStream(fin);
            program = (Program) ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(fin != null) {
                    fin.close();
                }
                if(ois != null) {
                    ois.close();
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        if(program != null) {
            CMMVM cmmvm = new CMMVM(program);
            cmmvm.execute();
        }

    }

    public static void interpret(String filepath) {
        FileReader reader = getReader(filepath);
        if (reader == null)
            return;
        Parser parser = new Parser(filepath, reader);
        SynTree synTree = new SynTree(parser.parse(), filepath);
        synTree.checkAndBuild();
        Failure.reportFailure();
        if (!Failure.canContinue()) {
            return;
        }
        synTree.visit();

    }


}
