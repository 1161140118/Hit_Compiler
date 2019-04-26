/**
 * 
 */
package SemanticAnalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author standingby
 *
 */
public class SymbolTable {
    private static List<SymbolTable> symbolTables = new LinkedList<>();
    private int OFFSET = 0;
    public final String proName;
    public List<Symbol> Table = new ArrayList<>();
    public Set<String> idStrings = new HashSet<>();
    public SymbolTable pre;

    public SymbolTable(String proName) {
        this.proName = proName;
        symbolTables.add(this);
    }

    public Symbol addSymbol(String name, String classId, String type, String offset) {
        Symbol newsymbol = new Symbol(name, classId, type, OFFSET);
        Table.add(newsymbol);
        idStrings.add(name);
        OFFSET += Integer.valueOf(offset);
//        System.err.println("New Symbol" + newsymbol.toString());
        return newsymbol;
    }

    public boolean hasDefine(String string) {
        if (idStrings.contains(string)) {
            return true;
        }
        if (pre == null) {
            return false;
        }
        return pre.hasDefine(string);
    }
    
    public static void output() {
        for (SymbolTable symbolTable : symbolTables) {
            System.out.println("  Table:"+symbolTable.proName);
            for (Symbol symbol : symbolTable.Table) {
                System.out.println("    "+symbol.toString());
            }
        }
    }

}


class Symbol {
    String name;
    String classId;

    String type;
    // String value;
    int offset;

    public SymbolTable next;


    public Symbol(String name, String classId, String type, int offset) {
        super();
        this.name = name;
        this.classId = classId;
        this.type = type;
        this.offset = offset;
    }


    public SymbolTable mktable(SymbolTable curTable, String proName) {
        this.next = new SymbolTable(proName);
        this.next.pre = curTable;
        return next;
    }

    @Override
    public String toString() {
        return "Symbol [name=" + name + ", classId=" + classId + ", type=" + type + ", offset="
                + offset + ", next=" + next + "]";
    }

}

