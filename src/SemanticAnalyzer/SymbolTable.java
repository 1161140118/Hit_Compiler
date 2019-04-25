/**
 * 
 */
package SemanticAnalyzer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author standingby
 *
 */
public class SymbolTable {
    public static int offset = 0;
    public List<Symbol> Table = new ArrayList<>();
    public SymbolTable pre;
    public SymbolTable next;
    
    
    /**
     * 
     */
    public SymbolTable mktable() {
        this.next = new SymbolTable();
        this.next.pre = this;
        return next;
    }
    
}


class Symbol{
    String name;
    String classId;
    
    String type;
    String value;
    
    int offset;
    
    
    
    
    
    
    
}

