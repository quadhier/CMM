package SymTable;

import Lexer.Identifer;
import java.util.Hashtable;


public class Env {

    private Hashtable<String, Symbol> symbols;
    private Env prev;
    private boolean isInLoop;
    private boolean continueLoop;
    private boolean breakLoop;

    public Env(Env prev) {
        symbols = new Hashtable<>();
        this.prev = prev;
        if(prev == null) {
            isInLoop = false;
            continueLoop = false;
            breakLoop = false;
        } else {
            isInLoop = prev.isInLoop();
            continueLoop = prev.continueLoop;
            breakLoop = prev.breakLoop;
        }
    }

    public void put(String s, Symbol symbol) {
        symbols.put(s, symbol);
    }

    public Symbol get(String s) {
        for(Env t = this; t != null; t = t.prev) {
            Symbol symbol = t.symbols.get(s);
            if(symbol != null) {
                return symbol;
            }
        }
        return null;
    }

    public boolean isInLoop() {
        return this.isInLoop;
    }

    public Symbol getFromCurrentScope(String s) {
        return symbols.get(s);
    }

    public void setInLoop(Boolean inLoop) {
        this.isInLoop = inLoop;
    }

    public boolean isContinueLoop() {
        boolean a = false;
        Env t = this;
        while (t.prev!=null && t.prev.isInLoop()){
            //a=true if the two are the same
            a = t.continueLoop == t.prev.continueLoop;
            t = t.prev;
        }
        if (a==false)
            continueLoop = t.continueLoop;
        return continueLoop;
    }

    public void setContinueLoop(boolean continueLoop) {
        this.continueLoop = continueLoop;
    }

    public boolean isBreakLoop() {
        return breakLoop;
    }

    public void setBreakLoop(boolean breakLoop) {
        this.breakLoop = breakLoop;
    }

    public Env getPrev() {
        return prev;
    }
}
