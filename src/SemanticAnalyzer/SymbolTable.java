/**
 * 
 */
package SemanticAnalyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author standingby
 *
 */
public class SymbolTable {
    private static List<SymbolTable> symbolTables = new LinkedList<>();
    private int OFFSET = 0;
    public final String proName;
    public Map<String, Symbol> Table = new LinkedHashMap<>();
    public SymbolTable pre;

    public SymbolTable(String proName) {
        this.proName = proName;
        symbolTables.add(this);
    }

    public Symbol addSymbol(String name, String classId, String type, String offset) {
        Symbol newsymbol = new Symbol(name, classId, type, OFFSET);
        Table.put(name,newsymbol);
        OFFSET += Integer.valueOf(offset);
        return newsymbol;
    }

    public boolean hasDefine(String string) {
        if (Table.containsKey(string)) {
            return true;
        }
        if (pre == null) {
            return false;
        }
        return pre.hasDefine(string);
    }
    
    public String getType(String id) {
        if (!hasDefine(id)) {
            return null;
        }
        return Table.get(id).type;
    }
    
    public static void output() {
        for (SymbolTable symbolTable : symbolTables) {
            System.out.println("  Table:"+symbolTable.proName);
            for (Symbol symbol : symbolTable.Table.values()) {
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

