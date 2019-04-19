package SyntacticAnalyzer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

/**
 * @author standingby
 *  构造项目集闭包，通过DFA转移填充Table
 */
public class ItemSet {
    private static Map<ItemSet, Integer> itemSetIds = new HashMap<>();
    private static Queue<ItemSet> queue = new LinkedList<>();
    static int maxId=0; // 新项目集序号
    
    /** 项目集标记属性 */
    
    private final int id;
    private final Set<Item> prim = new HashSet<>();
    /** 记录相同项目 */
    private Map<String, Item> prodStates = new LinkedHashMap<>();
    private Map<String, ItemSet> nextItemSet = new LinkedHashMap<>();
    
    public ItemSet(int id,Item item) {
        super();
        this.id = id;
        this.prim.add(item);
    }
    
    public ItemSet(int id,Production production, int next, Set<String> look) {
        super();
        this.id = id;
        this.prim.add(new Item(production, next, look));
    }
    
    public static void startGenerateClosure(Production start) {
        ItemSet first = new ItemSet(maxId++, start, 0, new HashSet<>(Arrays.asList("#")));
        itemSetIds.put(first, first.id);
        generate(first);
        queue.add(first);
        while(!queue.isEmpty()) {
            ItemSet top = queue.poll();
            top.generateClosure();
            if (top.nextItemSet.size()==0) {
                // 规约 TODO
                continue;
            }
            for (String string : top.nextItemSet.keySet()) {
                ItemSet nextSet = top.nextItemSet.get(start);
                if (itemSetIds.containsKey(nextSet)) {
                    // prim 已存在 跳转 TODO
                }else {
                    // 新集，生成闭包，入队
                }
                
            }
            
        }
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
        // 遍历prim集，产生项目
        for (Item item : thisSet.prim) {
            if (item.production.right.size()==item.next) {
                // 规约项目，在generateItemSets中处理
//                LRTable.addReg(thisSet.id,item.production,item.look);
                return;
            }
            // 产生项目集
            thisSet.generateItems(item);
        }
    }
    
    private void generateClosure() {
        // Prim项目
        for (Item item : prim) {
            generateItemSets(item,id);
        }
        // 其他项目
        for (Item item : prodStates.values()) {
            generateItemSets(item,id);
        }
    }
    
    /**
     * 在当前项目内，计算闭包
     * 由Prim或Prim所产生项目调用，仅在当前对象内递归，计算闭包
     * @param curItem 产生闭包的项目
     */
    private void generateItems(Item curItem) {
//    	System.out.println("计算闭包："+curItem.prodState());
        String next = curItem.getNext();
        if (next==null) { // 闭包内 空产生式 规约项目
            LRTable.addReg(this.id, curItem.production, curItem.look);
			return;
		}
        if (GrammarParser.isTerminal(next)) { // 移入项目,产生项目集时处理
			return;
		}
        // 产生项目集内项目
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = curItem.getLook(); // 继承展望符
        for (Production production : productions) {
            Item item = new Item(   production, 0, curLook);
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
    
    private void generateItemSets(Item curItem,int curId) {
        String next = curItem.getNext();
        if (next==null) { // 规约项目 
            LRTable.addReg(curId,curItem.production,curItem.look);
			return;
		}
        Item newItem = new Item(curItem.production, curItem.next+1, curItem.look);
        // 项目集转移
        if (nextItemSet.containsKey(next)) { // 根据转移符标志闭包
            // 目标项目集已存在
            nextItemSet.get(next).prim.add(newItem);
//            if (GrammarParser.isTerminal(next)) {
//                // 移入
//                LRTable.addShift(curId,next,newId);
//            }else {
//                // Goto
////                System.out.println("转移已存在闭包："+newItem.prodState()+" when "+next+" "+curId+"-"+newId);
//                LRTable.addGoto(curId,next,newId);
//            }
        }else {
            // 产生新项目集
            int newId = maxId++;
            ItemSet newSet = new ItemSet(newId, newItem.production, newItem.next, newItem.look);
//            itemIds.put(newItem, newId);
            // 转移到新项目
            nextItemSet.put(next, newSet);
            if (GrammarParser.isTerminal(next)) {
                // 移入
                LRTable.addShift(curId,next,newId);
            }else {
                // Goto
//                System.out.println("转移新闭包:"+newItem.prodState()+" when "+next+" "+curId+"-"+newId);
                LRTable.addGoto(curId,next,newId);
            }
            // 递归产生
//            generate(newSet);
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((prim == null) ? 0 : prim.hashCode());
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
        ItemSet other = (ItemSet) obj;
        if (prim == null) {
            if (other.prim != null)
                return false;
        } else if (!prim.equals(other.prim))
            return false;
        return true;
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
