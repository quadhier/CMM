package Parser;

import Buffer.TokenBuffer;
import Failure.Failure;
import Lexer.Identifer;
import Lexer.Tag;
import Lexer.Token;
import SynTree.*;

import java.io.FileReader;

public class Parser {

    private String filepath;
    private TokenBuffer tbuffer;
    private Token t;

    public Parser(String fpath, FileReader reader) {
        filepath = fpath;
        tbuffer = new TokenBuffer(fpath, reader, 10);
        t = null;
    }

    private Token peek(int n) {
        return tbuffer.peek(n);
    }

    private void extract(int n) {
        tbuffer.extract(n);
    }

    private void expect(int expectTag, String failMsg) {
        t = peek(1);
        if(t != null && t.getTag() == expectTag) {
            extract(1);
        } else {
            Failure.addFailure(filepath, peek(0).getLine(), peek(0).getEndpos() + 1, Failure.ERROR, failMsg);
        }
    }

    // to see if the tag of a token
    // is in the FIRST set of expression production
    private boolean isInExprFirst(int tag) {
        if (t.getTag() == '(' || t.getTag() == '-'
                || t.getTag() == '!' || t.getTag() == Tag.IDT
                || Tag.isConstantVal(t.getTag()) ){
            return true;
        }
        return false;
    }

    public ProgNode parse() {
        return program();
    }

    // <program> = <statement> { <statement> }
    private ProgNode program() {
        ProgNode progNode = new ProgNode();
        t = peek(1);
        while(true) {
            StmtNode stmtNode = statement();
            if(stmtNode != null) {
                progNode.addStatement(stmtNode);
            } else {
                break;
            }
        }
        return progNode;
    }

    // <statement> = <declaration>
    // | <compound-statement>
    // | <assignment-statement>
    // | <selection-statement>
    // | <iteration-statement>
    // | <jump-statement>
    // | <read-statement>
    // | <write-statement> ;
    private StmtNode statement() {
        StmtNode stmtNode = new StmtNode();
        while(true) {
            t = peek(1);
            if(t == null) {
                return null;
            }
            // a type keyword follows
            // then it is declaration
            if (Tag.isTypeKeyword(t.getTag())) {
                stmtNode.setChild(declaration());
                break;
            // a '{' follows
            // then it is a compound-statement
            } else if (t.getTag() == '{') {
                stmtNode.setChild(compoundStatement());
                break;
            // an identifier follows
            // then it is an assignment-statement
            } else if (t.getTag() == Tag.IDT) {
                stmtNode.setChild(assignmentStatement());
                break;
            // keyword 'if' follows
            // then it is a selection-statement
            } else if (t.getTag() == Tag.IF) {
                stmtNode.setChild(selectionStatement());
                break;
            // keyword 'while' follows
            // then it is a iteration-statement
            } else if (t.getTag() == Tag.WHILE) {
                stmtNode.setChild(iterationStatement());
                break;
            // keyword 'break' or 'continue' follows
            // then it is a jump-statement
            } else if (t.getTag() == Tag.BREAK || t.getTag() == Tag.CONTINUE) {
                stmtNode.setChild(jumpStatement());
                break;
            // keyword 'read' follows
            // then it is a read-statement
            } else if (t.getTag() == Tag.READ) {
                stmtNode.setChild(readStatement());
                break;
            // keyword 'write' follows
            // then it is a write-statement
            } else if (t.getTag() == Tag.WRITE) {
                stmtNode.setChild(writeStatement());
                break;
            // empty statement
            } else if(t.getTag() == ';') {
                extract(1);
            } else {
                // skip unexpected token and continue parsing
                Failure.addFailure(filepath, t.getLine(), t.getStartpos(), Failure.ERROR, "unexpected token '" + t.getLexeme() + "'" + ", expected '{', keyword or identifier");
                extract(1);
            }
        } // end of while
        return stmtNode;
    }

    // !!! may return null
    // <declaration> = <declaration-specifiers> <initializer> { "," <initializer> } ";" ;
    // <declaration-specifier> = ( "char" | "int" | "double" | "_Bool" ) { "[" <additive-expression> "]" } ;
    private DeclNode declaration() {
        DeclNode declNode = null;
        t = peek(1);
        Token specifier = t;
        extract(1); // extract specifier
        t = peek(1);
        // if '[' follows then it is an array declaration
        if(t.getTag() == '[') {
            declNode = new DeclNode(Tag.ARRDECL);
            declNode.setDeclarationSpecifer(specifier);
            // parse the dimensions of array declaration
            while(true) {
                t = peek(1);
                if(t.getTag() != '[') {
                    break;
                }
                extract(1); // extract '['
                declNode.addDimensionLength(additiveExpression());
                t = peek(1);
                if(t.getTag() != ']') {
                    Failure.addFailure(filepath, t.getLine(), t.getStartpos(), Failure.ERROR, "missing ']'");
                } else {
                    extract(1); // extract ']'
                }
            }
        // if identifier directly follows then it is simply a variable declaration
        } else if(t.getTag() == Tag.IDT) {
            declNode = new DeclNode(Tag.VARDECL);
            declNode.setDeclarationSpecifer(specifier);
        }
        t = peek(1);
        if(t == null) {
            return null;
        }
        if(t.getTag() != Tag.IDT) {
            // if ';' directly follows,
            // end the parsing of declaration and return null
            if (t.getTag() == ';') {
                expect(Tag.IDT, "expected identifier");
                extract(1); // consume ';'
                return null;
            // panic mode entered
            } else {
                // consume tokens till one in the synchronizing set is met
                while(true) {
                    t = peek(1);
                    if(t == null) {
                        Failure.addFailure(filepath, peek(0).getLine(), peek(0).getEndpos() + 1, Failure.ERROR, "expected identifer");
                        return null;
                    } else if(t.getTag() == Tag.IDT) {
                        break;
                    } else if(Tag.isTypeKeyword(t.getTag()) || t.getTag() == '{'
                            || t.getTag() == Tag.IF || t.getTag() == Tag.WHILE
                            || t.getTag() == Tag.BREAK || t.getTag() == Tag.CONTINUE
                            || t.getTag() == Tag.READ || t.getTag() == Tag.WRITE) {
                        return null;
                    } else {
                        Failure.addFailure(filepath, t.getLine(), t.getEndpos(), Failure.ERROR, "unexpected token skipped, expected identifer");
                        extract(1);
                    }
                } // end of panic while
            }
        } // end of dealing with unexpected token that is not identifier

        // get declared initializer(s)
        while (t.getTag() == Tag.IDT) {
            // declNode can never be null, once control reaches here
            assert declNode != null;
            declNode.addInitializer(initializer());
            t = peek(1);
            if(t.getTag() == ',') {
                extract(1);
                t = peek(1);
            } else {
                // there should be a ';' at the end of declaration
                expect(';', "expected ';' at the end of declaration");
                break;
            }
        }
        return declNode;
    }

    // !!! expression may be null
    // <initializer> = <identifier> [ "=" <expression> ] ;
    private InitlzrNode initializer() {
        InitlzrNode initlzrNode = new InitlzrNode();
        t = peek(1);
        if(t.getTag() == Tag.IDT) {
            initlzrNode.setIdentifer((Identifer) t);
            extract(1);
        }
        t = peek(1);
        if(t.getTag() == '=') {
            extract(1);
            t = peek(1);
            // expect the start symbols of expression
            if(!isInExprFirst(t.getTag())) {
                Failure.addFailure(filepath, peek(0).getLine(), peek(0).getEndpos() + 1, Failure.ERROR, "expected expression");
                return initlzrNode;
            }
            initlzrNode.setExpression(expression());
        }
        return initlzrNode;
    }

    // <compound-statement> = "{" { <statement> } "}" ;
    private CompStmtNode compoundStatement() {
        CompStmtNode compStmtNode = new CompStmtNode();
        extract(1);
        t = peek(1);
        while(t != null && t.getTag() != '}') {
            compStmtNode.addStatement(statement());
            t = peek(1);
            // if end of file is reached and no '}' is found
            // report error
            if(t != null && t.getTag() != '}' && peek(2) == null) {
                Failure.addFailure(filepath, t.getLine(), t.getEndpos(), Failure.ERROR, "missing '}'");
                break;
            }
        }
        expect('}', "missing '}'");
        return compStmtNode;
    }

    // !!! may return null
    // <assignment-statement> = <left-value-expression> <assignment-operator> <expression> ;
    // <assignment-operator> = "=" | "*=" | "/=" | "%=" | "+=" | "-=";
    private AsgmStmtNode assignmentStatement() {
        AsgmStmtNode asgmStmtNode = new AsgmStmtNode();
        asgmStmtNode.setLeftValueExpression(leftValueExpression());
        t = peek(1);
        if(Tag.isAssignmentOperator(t.getTag())) {
            asgmStmtNode.setAssignmentOperator(t);
            extract(1);
        } else {
            Failure.addFailure(filepath, peek(0).getLine(), peek(0).getEndpos() + 1, Failure.ERROR, "expected assignment operator");
        }

        t = peek(1);
        // if ';' directly follows,
        // end the parsing of assignment expression and return null
        if (t.getTag() == ';') {
            expect(Tag.IDT, "expected expression");
            extract(1); // consume ';'
            return null;
            // panic mode entered
        } else if(!isInExprFirst(t.getTag())) {
            // consume tokens till one in the synchronizing set is met
            while (true) {
                if (Tag.isTypeKeyword(t.getTag()) || t.getTag() == '{'
                        || t.getTag() == Tag.IDT || t.getTag() == Tag.IF
                        || t.getTag() == Tag.WHILE || t.getTag() == Tag.BREAK
                        || t.getTag() == Tag.CONTINUE || t.getTag() == Tag.READ
                        || t.getTag() == Tag.WRITE) {
                    expect(Tag.IDT, "expected expression");
                    return null;
                }
                t = peek(1);
                if (t == null) {
                    Failure.addFailure(filepath, peek(0).getLine(), peek(0).getEndpos() + 1, Failure.ERROR, "expected expression");
                    return null;
                } else {
                    extract(1);
                }
            }
        } else {
            asgmStmtNode.setExpression(expression());
            expect(';', "expected ';' at the end of the declaration");
            return asgmStmtNode;
        }
    }

    // <selection-statement> = "if" "(" <expression> ")" <statement> [ "else" <statement> ]
    private SeleStmtNode selectionStatement() {
        SeleStmtNode seleStmtNode = new SeleStmtNode();
        extract(1); // extract 'if'
        expect('(', "expected '(' after 'if'");
        seleStmtNode.setExpression(expression());
        expect(')', "missing ')'");
        seleStmtNode.setIfStatement(statement());
        t = peek(1);
        if(t != null && t.getTag() == Tag.ELSE) {
            extract(1); // extract 'else'
            seleStmtNode.setElseStatement(statement());
        }
        return seleStmtNode;
    }

    // <iteration-statement> = "while" "(" <expression> ")" <statement>
    private IterStmtNode iterationStatement() {
        IterStmtNode iterStmtNode = new IterStmtNode();
        extract(1);
        expect('(', "expected '(' after 'while'");
        iterStmtNode.setExpression(expression());
        expect(')', "missing ')' ");
        iterStmtNode.setStatement(statement());
        return iterStmtNode;
    }

    // <jump-statement> = ( "continue" | "break" ) ";"
    private JumpStmtNode jumpStatement() {
        t = peek(1);
        JumpStmtNode jumpStmtNode = new JumpStmtNode();
        jumpStmtNode.setJumpType(t);
        extract(1);
        expect(';', "expected ';' at the end of the statement");
        return jumpStmtNode;
    }

    // <read-statement> = "read" "(" <left-value-expression> ")"
    private ReadStmtNode readStatement() {
        ReadStmtNode readStmtNode = new ReadStmtNode();
        extract(1); // extract 'read'
        expect('(', "expected '(' after 'read'");
        readStmtNode.setLeftValueExpression(leftValueExpression());
        expect(')', "missing ')'");
        expect(';', "expected ';' at the end of the statement");
        return readStmtNode;
    }

    // <write-statement> = "write" "(" <expression> ")"
    private WriteStmtNode writeStatement() {
        WriteStmtNode writeStmtNode = new WriteStmtNode();
        extract(1); // extract 'write'
        expect('(', "expected '(' after 'write'");
        writeStmtNode.setExpression(expression());
        expect(')', "missing ')'");
        expect(';', "expected ';' at the end of the statement");
        return writeStmtNode;
    }

    // <left-value-expression> = <identifier> { "[" <additive-expression> "]" }
    private NnaryExprNode leftValueExpression() {
        NnaryExprNode lValExprNode = null;
        Identifer identifer = (Identifer) peek(1);
        extract(1);
        t = peek(1);
        // if '[' follows then it is an array
        if(t.getTag() == '[') {
            lValExprNode = new NnaryExprNode(Tag.ARRLEXPR);
            lValExprNode.setIdentifier(identifer);
            while(true) {
                t = peek(1);
                if(t.getTag() != '[') {
                    break;
                }
                extract(1); // extract '['
                lValExprNode.addChildExpression(additiveExpression());
                extract(1); // extract ']'
            }
        // if no '[' follows then it is a single variable
        } else {
            lValExprNode = new NnaryExprNode(Tag.VARLEXPR);
            lValExprNode.setIdentifier(identifer);
        }
        return lValExprNode;
    }

    // <expression> = <logical-OR-expression> ;
    private NnaryExprNode expression() {
        return logicalOrExpression();
    }


    // <logical-OR-expression> = <logical-AND-expression>
    // | <logical-OR-expression> "||" <logical-AND-expression> ;
    private NnaryExprNode logicalOrExpression() {
        // if it is not a complete logical-OR-expression
        // return its child expression node
        NnaryExprNode lgAndExprNode = logicalAndExpression();
        t = peek(1);
        if(t == null || t.getTag() != Tag.OR) {
            return lgAndExprNode;
        }
        // else create a logical-OR-expression and return
        NnaryExprNode lgOrExprNode = new NnaryExprNode(Tag.LGOREXPR);
        lgOrExprNode.addChildExpression(lgAndExprNode);
        t = peek(1);
        while(t != null && t.getTag() == Tag.OR) {
            Token opt = t;
            extract(1);
            lgAndExprNode = logicalAndExpression();
            lgAndExprNode.setOpt(opt);
            lgOrExprNode.addChildExpression(lgAndExprNode);
            t = peek(1);
        }
        return lgOrExprNode;
    }


    // <logical-AND-expression> = <equality-expression>
    // | <logical-AND-expression> "&&" <equality-expression>
    private NnaryExprNode logicalAndExpression() {
        // if it is not a complete logical-AND-expression
        // return its child expression node
        NnaryExprNode eqExprNode = equalityExpression();
        t = peek(1);
        if(t == null || t.getTag() != Tag.AND) {
            return eqExprNode;
        }
        // else create a logical-AND-expression and return
        NnaryExprNode lgAndExprNode = new NnaryExprNode(Tag.LGANDEXPR);
        lgAndExprNode.addChildExpression(eqExprNode);
        t = peek(1);
        while(t != null && t.getTag() == Tag.AND) {
            Token opt = t;
            extract(1);
            eqExprNode = equalityExpression();
            eqExprNode.setOpt(opt);
            lgAndExprNode.addChildExpression(eqExprNode);
            t = peek(1);
        }
        return lgAndExprNode;
    }


    // <equality-expression> = <relational-expression>
    // | <equality-expression> "==" <relational-expression>
    // | <equality-expression> "!=" <relational-expression> ;
    private NnaryExprNode equalityExpression() {
        // if it is not a complete equality-expression
        // return its child expression node
        NnaryExprNode relaExprNode = relationalExpression();
        t = peek(1);
        if(t == null || (t.getTag() != Tag.EQ && t.getTag() != Tag.NE)) {
            return relaExprNode;
        }

        // else create an equality-expression and return
        NnaryExprNode eqExprNode = new NnaryExprNode(Tag.EQEXPR);
        eqExprNode.addChildExpression(relaExprNode);
        t = peek(1);
        while(t != null && ( t.getTag() == Tag.EQ || t.getTag() == Tag.NE)) {
            Token opt = t;
            extract(1);
            relaExprNode = relationalExpression();
            relaExprNode.setOpt(opt);
            eqExprNode.addChildExpression(relaExprNode);
            t = peek(1);
        }
        return eqExprNode;
    }


    // <relational-expression> = <additive-expression>
    // | <relational-expression> "<" <additive-expression>
    // | <relational-expression> ">" <additive-expression>
    // | <relational-expression> ">=" <additive-expression>
    // | <relational-expression> "<=" <additive-expression> ;
    private NnaryExprNode relationalExpression() {
        // if it is not a complete relational-expression
        // return its child expression node
        NnaryExprNode adtvExprNode = additiveExpression();
        t = peek(1);
        if(t == null || (t.getTag() != '<' && t.getTag() != Tag.LE && t.getTag() != '>' && t.getTag() != Tag.GE)) {
            return adtvExprNode;
        }

        // else create a relational-expression and return
        NnaryExprNode relaExprNode = new NnaryExprNode(Tag.RELAEXPR);
        relaExprNode.addChildExpression(adtvExprNode);
        t = peek(1);
        while(t != null && (t.getTag() == '<' || t.getTag() == '>'
                || t.getTag() == Tag.LE || t.getTag() == Tag.GE)) {
            Token opt = t;
            extract(1);
            adtvExprNode = additiveExpression();
            adtvExprNode.setOpt(opt);
            relaExprNode.addChildExpression(adtvExprNode);
            t = peek(1);
        }
        return relaExprNode;
    }

    // <additive-expression> = <multiplicative-expression>
    // | <additive-expression> "+" <multiplicative-expression>
    // | <additive-expression> "-" <multiplicatvie-expression> ;
    private NnaryExprNode additiveExpression() {
        // if it is not a complete additive-expression
        // return its child expression node
        NnaryExprNode mltvExprNode = multiplicativeExpression();
        t = peek(1);
        if(t == null || (t.getTag() != '+' && t.getTag() != '-')) {
            return mltvExprNode;
        }

        // else create an additive-expression and return
        NnaryExprNode adtvExprNode = new NnaryExprNode(Tag.ADTVEXPR);
        adtvExprNode.addChildExpression(mltvExprNode);
        t = peek(1);
        while(t != null && ( t.getTag() == '+' || t.getTag() == '-')) {
            Token opt = t;
            extract(1);
            mltvExprNode = multiplicativeExpression();
            mltvExprNode.setOpt(opt);
            adtvExprNode.addChildExpression(mltvExprNode);
            t = peek(1);
        }
        return adtvExprNode;
    }

    // <multiplicatvie-expression> = <unary-expression>
    // | <multiplicative-expression> "*" <unary-expression>
    // | <multiplicative-expression> "/" <unary-expression>
    // | <multiplicative-expression> "%" <unary-expression> ;
    private NnaryExprNode multiplicativeExpression() {
        // if it is not a complete multiplicative-expression
        // return its child expression node
        NnaryExprNode unaryExprNode = unaryExpression();
        t = peek(1);
        if(t == null || (t.getTag() != '*' && t.getTag() != '/' && t.getTag() != '%')) {
            return unaryExprNode;
        }

        // else create an unary-expression and return
        NnaryExprNode mltvExprNode = new NnaryExprNode(Tag.MLTVEXPR);
        mltvExprNode.addChildExpression(unaryExprNode);
        t = peek(1);
        while(t != null && ( t.getTag() == '*' || t.getTag() == '/' || t.getTag() == '%')) {
            Token opt = t;
            extract(1);
            unaryExprNode = unaryExpression();
            unaryExprNode.setOpt(opt);
            mltvExprNode.addChildExpression(unaryExprNode);
            t = peek(1);
        }
        return mltvExprNode;
    }

    // <unary-expression> = [ <unary-operator> ] <primary-expression> ;
    // <unary-operator> = "-" | "!" ;
    private NnaryExprNode unaryExpression() {
        t = peek(1);
        if(t == null || ( t.getTag() != '-' && t.getTag() != '!')) {
            return primaryExpression();
        }
        extract(1);
        NnaryExprNode unaryExprNode = new NnaryExprNode(Tag.UNARYEXPR);
        unaryExprNode.setOpt(t);
        unaryExprNode.addChildExpression(primaryExpression());
        return unaryExprNode;
    }

    // !!! may return null
    //  <primary-expression> = <left-value-expression>
    // | <constant>
    // | "(" <expression> ")" ;
    private NnaryExprNode primaryExpression() {
        t = peek(1);
        if(t == null) {
            Failure.addFailure(filepath, peek(0).getLine(), peek(0).getEndpos() + 1, Failure.ERROR, "expected primary expression");
            return null;
        }
        if(Tag.isConstantVal(t.getTag())) {
            return constant();
        } else if(t.getTag() == Tag.IDT) {
            return leftValueExpression();
        } else if(t.getTag() == '(') {
            extract(1);
            NnaryExprNode expression = expression();
            t = peek(1);
            if(t.getTag() == ')') {
                extract(1);
            }
            return expression;
        }
        return null;
    }

    // <constant> = <integer-constant>
    // | <floating-constant>
    // | <boolean-constant> ;
    private NnaryExprNode constant() {
        NnaryExprNode constValNode = new NnaryExprNode(Tag.CONSTVAL);
        t = peek(1);
        constValNode.setConstVal(t);
        extract(1);
        return constValNode;
    }


}
