package SynTree;

import CMMVM.Bytecode;
import CMMVM.Opcode;
import CMMVM.Program;
import Failure.Failure;
import Lexer.Tag;

import java.util.ArrayList;


// iteration-statement
public class IterStmtNode extends SNode {

    private NnaryExprNode expression;
    private StmtNode statement;

    public IterStmtNode() {
        super(Tag.ITERSTMT);
        expression = null;
        statement = null;
    }

    public void setExpression(NnaryExprNode expression) {
        this.expression = expression;
    }

    public void setStatement(StmtNode statement) {
        this.statement = statement;
    }

	@Override
	public void checkAndBuild() {

        if(expression == null)
            return;

		expression.setCurrentEnv(currentEnv);
		expression.checkAndBuild();
		if (expression.getDataType() != Tag.BOOL) {
			Failure.addFailure(SynTree.getFilepath(), expression.getStartLine(), expression.getStartPos(), Failure.ERROR,
					"expected bool type expression");
		}
		// infinite loop allowed
		if(statement == null)
			return;
		boolean alreadyInLoop = currentEnv.isInLoop();
		if (alreadyInLoop){
			statement.setCurrentEnv(currentEnv);
			statement.checkAndBuild();
		}else {
			currentEnv.setInLoop(true);
			statement.setCurrentEnv(currentEnv);
			statement.checkAndBuild();
			if (statement.getChild().getTag() == Tag.COMPSTMT)
				currentEnv.setInLoop(false);
			if (statement.getChild().getTag()==Tag.IF)
				currentEnv.setInLoop(false);
		}
	}

	@Override
	public void visit() {
		if (currentEnv.isContinueLoop() || currentEnv.isBreakLoop())
			return;
		while ((boolean) expression.getValue()) {
			//if statement is single jumpStatement, do nothing
			if (statement.getChild().getTag()==Tag.JUMPSTMT)
				return;
			statement.visit();
			//if the statement is compound statement
			if (statement.getChild().getTag() == Tag.COMPSTMT) {
				//if continues
				if (((CompStmtNode) statement.getChild()).getNextEnv().isContinueLoop()) {
					statement.currentEnv.setContinueLoop(false); //把外层的while的isContinue值重置
					((CompStmtNode) statement.getChild()).setContinueLoop(false); //把内层的while的isContinue值重置
					continue;
				}
				//if breaks
				if (((CompStmtNode) statement.getChild()).getNextEnv().isBreakLoop()) {
					statement.currentEnv.setBreakLoop(false);
					((CompStmtNode) statement.getChild()).setBreakLoop(false);
					break;
				}
			}  //if the statement is a single if statement
			else if(statement.getChild().getTag() == Tag.SELESTMT){
				SeleStmtNode seleStmtNode = (SeleStmtNode) statement.getChild();
				StmtNode ifStmt = seleStmtNode.getIfStatement();
				StmtNode elseStmt = seleStmtNode.getElseStatement();
				if (ifStmt.getChild().getTag() == Tag.COMPSTMT) {
					//if continues
					if (((CompStmtNode) ifStmt.getChild()).getNextEnv().isContinueLoop()) {
						ifStmt.currentEnv.setContinueLoop(false); //把外层的while的isContinue值重置
						((CompStmtNode) ifStmt.getChild()).setContinueLoop(false); //把内层的while的isContinue值重置
						continue;
					}
					//if breaks
					if (((CompStmtNode) ifStmt.getChild()).getNextEnv().isBreakLoop()) {
						ifStmt.currentEnv.setBreakLoop(false);
						((CompStmtNode) ifStmt.getChild()).setBreakLoop(false);
						break;
					}
				}
				if (elseStmt!=null && elseStmt.getChild().getTag() == Tag.COMPSTMT){
					//if continues
					if (((CompStmtNode) elseStmt.getChild()).getNextEnv().isContinueLoop()) {
						elseStmt.currentEnv.setContinueLoop(false); //把外层的while的isContinue值重置
						((CompStmtNode) elseStmt.getChild()).setContinueLoop(false); //把内层的while的isContinue值重置
						continue;
					}
					//if breaks
					if (((CompStmtNode) elseStmt.getChild()).getNextEnv().isBreakLoop()) {
						elseStmt.currentEnv.setBreakLoop(false);
						((CompStmtNode) elseStmt.getChild()).setBreakLoop(false);
						break;
					}
				}
			}
		}
	}

    @Override
    public void traverse(int blank) {
        if(blank > 0)
            System.out.print(" |");
        for(int i = 0 ; i < blank; i++) {
            if(i == blank - 1)
                System.out.print("---");
            else
                System.out.print("    |");
        }
        System.out.println("IterStmt");
        expression.traverse(blank + 1);
        if(statement != null)
        	statement.traverse(blank + 1);
    }


    @Override
    public void genBytecode(Program program) {
        int targetAddr1 = program.getCodeNum();

        program.storeIterStart(targetAddr1);
        program.createBreakList();

        expression.genBytecode(program);
        int codeAddr = program.getCodeNum();
        program.addCode(Opcode.bez);
        if(statement != null)
        	statement.genBytecode(program);
        program.addCode(Opcode.jmp, targetAddr1);
        int targetAddr2 = program.getCodeNum();
        program.backpatch(codeAddr, targetAddr2);

        program.backpatch(targetAddr2);
        program.removreIterStart();
        program.removreBreakList();
    }

}
