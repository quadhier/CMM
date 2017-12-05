package SymTable;

import Lexer.Identifer;
import java.util.Hashtable;


public class Env {

    private Hashtable<String, Symbol> symbols;
    private Env prev;
    private boolean isInLoop;

    public Env(Env prev) {
        symbols = new Hashtable<>();
        this.prev = prev;
        if(prev == null) {
            isInLoop = false;
        } else {
            isInLoop = prev.isInLoop();
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

    public void setInLoop() {
        this.isInLoop = true;
    }

}
