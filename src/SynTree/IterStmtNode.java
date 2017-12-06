package SynTree;

import CMMVM.Program;
import Failure.Failure;
import Lexer.Tag;


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

		expression.setCurrentEnv(currentEnv);
		expression.checkAndBuild();
		if (expression.getDataType() != Tag.BOOL) {
			Failure.addFailure(SynTree.getFilepath(), expression.getStartLine(), expression.getStartPos(), Failure.ERROR,
					"expected bool type expression");
		}
	   /* if (statement.getChild() instanceof CompStmtNode)
            ((CompStmtNode) statement.getChild()).getNextEnv().setInLoop();*/
		currentEnv.setInLoop(true);
		/*if (statement.getChild().getTag() == Tag.COMPSTMT)
			((CompStmtNode) statement.getChild()).getNextEnv().setInLoop();*/
		statement.setCurrentEnv(currentEnv);
		statement.checkAndBuild();
		if (statement.getChild().getTag() == Tag.COMPSTMT)
			currentEnv.setInLoop(false);
	}

	@Override
	public void visit() {
		while ((boolean) expression.getValue()) {
			statement.visit();
			//if continues
			if (statement.getChild().getTag() == Tag.COMPSTMT) {
				if (((CompStmtNode) statement.getChild()).getNextEnv().isContinueLoop())
					((CompStmtNode) statement.getChild()).setContinueLoop(false);
			}else if (statement.getChild().currentEnv.isContinueLoop())
				statement.getChild().currentEnv.setContinueLoop(false);
		}

	}

	@Override
	public void traverse(int blank) {
		if (blank > 0)
			System.out.print(" |");
		for (int i = 0; i < blank; i++) {
			if (i == blank - 1)
				System.out.print("---");
			else
				System.out.print("    |");
		}
		System.out.println("IterStmt");
		expression.traverse(blank + 1);
		statement.traverse(blank + 1);
	}


	@Override
	public void genBytecode(Program program) {

	}

}
