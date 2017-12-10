import Buffer.CharBuffer;
import Buffer.TokenBuffer;
import CMMVM.CMMVM;
import CMMVM.Program;
import Failure.Failure;
import Lexer.*;
import Parser.Parser;
import SynTree.SNode;
import SynTree.SynTree;


import java.io.FileReader;
import java.io.IOException;

public class CMMcompiler {


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


}
