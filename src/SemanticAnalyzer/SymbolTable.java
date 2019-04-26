/**
 * 
 */
package SemanticAnalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author standingby
 *
 */
public class SymbolTable {
    private int OFFSET = 0;
    public final String proName;
    public List<Symbol> Table = new ArrayList<>();
    public Set<String> idStrings = new HashSet<>();
    public SymbolTable pre;
    
    public SymbolTable(String proName) {
    	this.proName = proName;
	}
    
    public void addSymbol(String name, String classId, String type, String offset) {
        Table.add(new Symbol(name, classId, type, OFFSET));
        idStrings.add(name);
        OFFSET += Integer.valueOf(offset);
    }
    
    public boolean hasDefine(String string) {
    	if (idStrings.contains(string)) {
			return true;
		}
    	if (pre==null) {
			return false;
		}
    	return pre.hasDefine(string);
    }
    
}


class Symbol{
    String name;
    String classId;
    
    String type;
    //String value;
    
    int offset;
    

    public Symbol(String name, String classId, String type, int offset) {
        super();
        this.name = name;
        this.classId = classId;
        this.type = type;
        this.offset = offset;
    }


    public SymbolTable next;
    
    
    /**
     * 
     */
    public SymbolTable mktable(SymbolTable curTable,String proName) {
        this.next = new SymbolTable(proName);
        this.next.pre = curTable;
        return next;
    }
    
    
    
    
    
    
}

