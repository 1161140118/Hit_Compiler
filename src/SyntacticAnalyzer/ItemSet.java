package SyntacticAnalyzer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author standingby
 *
 */
public class ItemSet {
    public static Map<Item, Integer> itemId = new HashMap<>();
    int id;
    Item prim;
    List<Item> items= new LinkedList<>();
    
    public ItemSet(int id,Production production, int next, Set<String> look) {
        super();
        this.id = id;
        prim = new Item(production, next, look);
    }
    
    public static void startGenerateClosure(Production start) {
        ItemSet first = new ItemSet(0, start, 0, new HashSet<>(Arrays.asList("#")));
        first.generate();
    }
    
    public void generate() {
        // 规约
        if (prim.production.right.size()==id) {
            Table.addReg(id,prim.production,prim.look);
            return;
        }
      
        
        // 终结符，移入
        String next = prim.getNext();
        if (GrammarParser.isTerminal(next)) {
            
            
        }else {// 非终结符，转移
            
        }
    }
    
    private void generateItems() {
        String next = prim.getNext();
        if (GrammarParser.isTerminal(next)) {
			return;
		}
        List<Production> productions = GrammarParser.productions.get(next);
        for (Production production : productions) {
			
        	items.add(new Item(production, 0, prim.look));
		}
        
        
    }
    
    public void shift() {
        
    }
    

}

class Item{
    Production production;
    int next;
    Set<String> look;
    
    public Item(Production production, int next, Set<String> look) {
        super();
        this.production = production;
        this.next = next;
        this.look = look;
    }
    
    String getNext() {
        return production.right.get(next);
    }
    
    Set<String> getLook() {
    	if (production.right.size()==next-1) {
			return look;
		}
    	String follow = production.right.get(next+1);
    	if (GrammarParser.isTerminal(follow)) {
			
		}
    	return null;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((look == null) ? 0 : look.hashCode());
        result = prime * result + next;
        result = prime * result + ((production == null) ? 0 : production.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (look == null) {
            if (other.look != null)
                return false;
        } else if (!look.equals(other.look))
            return false;
        if (next != other.next)
            return false;
        if (production == null) {
            if (other.production != null)
                return false;
        } else if (!production.equals(other.production))
            return false;
        return true;
    }

    
}
