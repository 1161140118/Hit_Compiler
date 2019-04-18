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
    static int maxId=0; // 新项目集序号
    
    /** 项目集标记属性 */
    private final int id;
    private final Item prim;
    private Map<String, Item> prodStates = new HashMap<>();
    
    public ItemSet(int id,Production production, int next, Set<String> look) {
        super();
        this.id = id;
        this.prim = new Item(production, next, look);
    }
    
    public static void startGenerateClosure(Production start) {
        ItemSet first = new ItemSet(maxId++, start, 0, new HashSet<>(Arrays.asList("#")));
        itemIds.put(first.prim, first.id);
        generate(first);
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
    
    private static void generate(ItemSet thisSet) {
        // 规约
        if (thisSet.prim.production.right.size()==thisSet.prim.next) {
            Table.addReg(thisSet.id,thisSet.prim.production,thisSet.prim.look);
            return;
        }
        // 产生项目集
        thisSet.generateItems(thisSet.prim);
        // 对项目集内项目进行转移
        // Prim项目
        generateItemSets(thisSet.prim,thisSet.id);
        // 其他项目
        for (Item item : thisSet.prodStates.values()) {
            generateItemSets(item,thisSet.id);
        }
        
    }
    
    /**
     * 仅在当前对象内递归，计算闭包
     * @param curItem
     */
    private void generateItems(Item curItem) {
    	System.out.println("计算闭包："+curItem.prodState());
        String next = curItem.getNext();
        if (next==null) { // 规约项目
			return;
		}
        if (GrammarParser.isTerminal(next)) { // 移入项目
			return;
		}
        // 产生项目集内项目
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = curItem.getLook(); // 继承展望符
        for (Production production : productions) {
            Item item = new Item(production, 0, curLook);
            if (this.prodStates.keySet().contains(item.prodState())) {
                // 项目已存在，合并look
                this.prodStates.get(item.prodState()).look.addAll(curLook);
                continue;
            }
            // 新项目
            this.prodStates.put(item.prodState(), item);
            // 递归添加新项目可产生的所有项目
            this.generateItems(item);
		}
    }
    
    private static void generateItemSets(Item curItem,int curId) {
        String next = curItem.getNext();
        if (next==null) { // 规约项目 
			return;
		}
        Item newItem = new Item(curItem.production, curItem.next+1, curItem.look);
        // 项目集转移
        if (itemIds.containsKey(newItem)) {
            // 目标项目集已存在
            int newId = itemIds.get(newItem);
            if (GrammarParser.isTerminal(next)) {
                // 移入
                Table.addShift(curId,next,newId);
            }else {
                // Goto
                Table.addGoto(curId,next,newId);
            }
        }else {
            // 产生新项目集
            int newId = maxId++;
            ItemSet newSet = new ItemSet(newId, newItem.production, newItem.next, newItem.look);
            itemIds.put(newItem, newId);
            // 转移到新项目
            if (GrammarParser.isTerminal(next)) {
                // 移入
                Table.addShift(curId,next,newId);
            }else {
                // Goto
                Table.addGoto(curId,next,newId);
            }
            // 递归产生
            generate(newSet);
        }
        
    }
    

}

class Item{
    final Production production;
    final int next;
    Set<String> look;
    
    public Item(Production production, int next, Set<String> look) {
        super();
        this.production = production;
        this.next = next;
        this.look = look;
    }
    
    String getNext() {
    	if (next<production.right.size()) {
    		return production.right.get(next);
		}else {
			return null;
		}
    }
    
    Set<String> getLook() {
    	if (production.right.size()<=next+1) { // next的下一个，末尾
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
