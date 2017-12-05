package SynTree;

import CFG.BasicBlock;
import CMMVM.Bytecode;
import CMMVM.Program;
import SymTable.Env;

import java.util.ArrayList;

public class SynTree {


    private SNode root;
    private static String filepath;
    private Env rootEnv;

    public SynTree(SNode root, String filepath) {
        this.root = root;
        SynTree.filepath = filepath;
        rootEnv = new Env(null);
    }

    public static String getFilepath() {
        return filepath;
    }

    public void checkAndBuild() {
        root.setCurrentEnv(rootEnv);
        root.checkAndBuild();
    }

    public void visit() {
        root.visit();
    }


    public void traverse(int blank) {
        root.traverse(blank);
    }


    public BasicBlock genCFG() {
        return null;
    }

    public Program genBytecode() {
        Program program = new Program();
        root.genBytecode(program);
        return program;
    }

}
