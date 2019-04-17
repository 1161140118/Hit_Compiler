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
 *  构造项目集闭包，通过DFA转移填充Table
 */
public class ItemSet {
    public static Map<Item, Integer> itemIds = new HashMap<>();
    static int curId=0;
    private final int id;
    private final Item prim;
    private Map<String, Item> prodStates = new HashMap<>();
    
    public ItemSet(int id,Production production, int next, Set<String> look) {
        super();
        this.id = id;
        prim = new Item(production, next, look);
    }
    
    public static void startGenerateClosure(Production start) {
        ItemSet first = new ItemSet(curId++, start, 0, new HashSet<>(Arrays.asList("#")));
        first.generate();
    }
    
    /**************
     * 算法
     * 1. 产生闭包
     * 1.1 根据主项目的next符号，推导
     * 1.2 合并相同项目（不同展望符）
     * 2. 推出新闭包
     * 2.1 遍历项目，获得下一个项目
     * 2.2 对新项目，若已存在，则直接转移
     * 2.3 对新项目，若不存在，则新建项目集，并从1开始
     **************/
    
    public void generate() {
        // 规约
        if (prim.production.right.size()==id) {
            Table.addReg(id,prim.production,prim.look);
            return;
        }
        // 产生项目集
        generateItems(prim);
        // 对项目集内项目进行转移
        // Key项目
        generateItemSets(prim);
        // 其他项目
        for (Item item : prodStates.values()) {
            generateItemSets(item);
        }
        
    }
    
    private void generateItems(Item curItem) {
        String next = curItem.getNext();
        if (GrammarParser.isTerminal(next)) {
			return;
		}
        // 产生项目集内项目
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = curItem.getLook();
        for (Production production : productions) {
            Item item = new Item(production, 0, curLook);
            if (prodStates.keySet().contains(item.prodState())) {
                // 项目已存在，合并look
                prodStates.get(item.prodState()).look.addAll(curLook);
                continue;
            }
            // 新项目
            prodStates.put(item.prodState(), item);
            // 递归添加新项目可产生的所有项目
            generateItems(item);
		}
    }
    
    private void generateItemSets(Item curItem) {
        String next = curItem.getNext();
        Item item = new Item(curItem.production, curItem.next++, curItem.look);
        // 项目集转移
        int newId;
        if (itemIds.containsKey(item)) {
            // 目标项目集已存在
            newId = itemIds.get(item);
            if (GrammarParser.isTerminal(next)) {
                // 移入
                Table.addShift(id,next,newId);
            }else {
                // Goto
                Table.addGoto(id,next,newId);
            }
        }else {
            // 产生新项目集
            newId = curId++;
            ItemSet newSet = new ItemSet(newId, item.production, item.next, item.look);
            itemIds.put(item, newId);
            // 递归产生
            newSet.generate();
            // 转移到新项目
            if (GrammarParser.isTerminal(next)) {
                // 移入
                Table.addShift(id,next,newId);
            }else {
                // Goto
                Table.addGoto(id,next,newId);
            }
        }
        
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
    	if (production.right.size()==next-1) { // 末尾
			return look;
		}
    	String follow = production.right.get(next+1);
    	if (GrammarParser.isTerminal(follow)) { // 终结符
			return new HashSet<>(Arrays.asList(follow));
		}
    	// 非终结符
    	Set<String> first = GrammarParser.firstSet.get(follow);
    	if (!first.contains("$")) { // 无空
            return first;
        }
    	// 含空产生式
    	Set<String> result = new HashSet<>();
    	result.addAll(look);
    	result.addAll(first);
    	result.remove("$");
    	return result;
    }
    
    String prodState() {
        return production.toString()+next;
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
