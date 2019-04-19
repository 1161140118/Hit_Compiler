package SyntacticAnalyzer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
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
    static int maxId = 0; // 新项目集序号

    /** 项目集标记属性 */
    private int id;
    private final Set<Item> prim = new HashSet<>();

    /** 记录相同项目，以合并look集合  */
    private Map<String, Item> prodStates = new LinkedHashMap<>();
    /** 产生闭包时，已遍历的Item */
    private Set<Item> visitedItem = new HashSet<>();
    /** 当前ItemSet所能推出的ItemSet */
    private Map<String, ItemSet> nextItemSet = new LinkedHashMap<>();

    public ItemSet(Item item) {
        this.prim.add(item);
    }

    public ItemSet(Production production, int next, Set<String> look) {
        this.prim.add(new Item(production, next, look));
    }

    public static void startGenerateClosure(Production start) {
        ItemSet first = new ItemSet(start, 0, new HashSet<>(Arrays.asList("#")));
        first.id = maxId++;
        itemSetIds.put(first, first.id);

//        // first 产生闭包
//        first.generate();
        // first 入队
        queue.add(first);
        while (!queue.isEmpty()) {
            ItemSet top = queue.poll();
            top.generate();
            top.generateClosure();
            for (String string : top.nextItemSet.keySet()) {
                ItemSet nextSet = top.nextItemSet.get(string);
                if (itemSetIds.containsKey(nextSet)) {
                    // prim 已存在 跳转
                    if (GrammarParser.isTerminal(string)) {
                        LRTable.addShift(top.id, string, itemSetIds.get(nextSet));
                    } else {
                        LRTable.addGoto(top.id, string, itemSetIds.get(nextSet));
                    }
                } else {
                    // 新集，生成闭包，入队，跳转
                    nextSet.id = maxId++;
                    itemSetIds.put(nextSet, nextSet.id);
                    queue.add(nextSet);
                    if (GrammarParser.isTerminal(string)) {
                        LRTable.addShift(top.id, string, nextSet.id);
                    } else {
                        LRTable.addGoto(top.id, string, nextSet.id);
                    }
//                    nextSet.generate();
                }
            }
        }
    }
    
    public static void showItemSet(ItemSet itemSet) {
        System.out.println("    ItemSet : "+itemSet.id);
        System.out.println("        prim:");
        for (Item item : itemSet.prim) {
            System.out.println("    "+item.prodState()+" "+item.look);
        }
        System.out.println("        generate:");
        for (Item item : itemSet.prodStates.values()) {
            System.out.println("    "+item.prodState()+" "+item.look);
        }
        System.out.println();
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

    private void generate() {
        // 遍历prim集，产生项目
        for (Item item : prim) {
            if (item.production.right.size() == item.next) {
                // 规约项目
                LRTable.addReg(id, item.production, item.look);
                continue;
            }
            // 产生项目闭包
            generateItems(item);
        }
//        showItemSet(this);
    }

    /**
     * 在当前项目内，计算闭包
     * 由Prim直接调用或Prim所产生项目递归调用，仅在当前对象内递归，计算闭包
     * @param curItem 产生闭包的项目
     */
    private void generateItems(Item curItem) {
        if (visitedItem.contains(curItem)) {
            return;
        }
        visitedItem.add(curItem);
        // System.out.println("计算闭包："+curItem.prodState());
        String next = curItem.getNext();
        if (next == null) { // 闭包内 空产生式 规约项目
            LRTable.addReg(this.id, curItem.production, curItem.look);
            return;
        }
        if (GrammarParser.isTerminal(next)) { // 移入项目,产生项目集时处理
            return;
        }
        // 产生项目集内项目
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = new HashSet<>();
        curLook.addAll(curItem.inhLook()); // 继承展望符
        for (Production production : productions) {
            Item item = new Item(production, 0, curLook);
            if (prodStates.keySet().contains(item.prodState())) {
                // 项目已存在，合并look
                prodStates.get(item.prodState()).look.addAll(curLook);
            }else {
                // 新项目
                prodStates.put(item.prodState(), item);
            }
            // 递归添加新项目可产生的所有项目
            generateItems(item);
        }
    }

    /**
     * 遍历当前项目集项目
     * 调用generateItemSets 产生 nextItemSet
     */
    private void generateClosure() {
        for (Item item : prim) {
            generateItemSets(item, id);
        }
        for (Item item : prodStates.values()) {
            generateItemSets(item, id);
        }
    }

    private void generateItemSets(Item curItem, int curId) {
        String next = curItem.getNext();
        if (next == null) { // 规约项目
            // LRTable.addReg(curId, curItem.production, curItem.look);
            return;
        }
        Item newItem = new Item(curItem.production, curItem.next + 1, curItem.look);
        // 项目集转移
        if (nextItemSet.containsKey(next)) { // 根据转移符标志闭包
            // 目标项目集已存在
            nextItemSet.get(next).prim.add(newItem);
        } else {
            // 产生新项目集
            ItemSet newSet = new ItemSet(newItem.production, newItem.next, newItem.look);
            nextItemSet.put(next, newSet);
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


class Item {
    final Production production;
    final int next;
    Set<String> look;

    public Item(Production production, int next, Set<String> look) {
        super();
        this.production = production;
        this.next = next;
        this.look = new HashSet<>();
        this.look.addAll(look);
    }

    String getNext() {
        if (next < production.right.size()) {
            return production.right.get(next);
        } else {
            return null;
        }
    }

    Set<String> inhLook() {
        Set<String> result = new HashSet<>();
        if (production.right.size() <= next + 1) { // next的下一个，末尾
            result.addAll(look);
            return result;
        }
        String follow = production.right.get(next + 1);
        if (GrammarParser.isTerminal(follow)) { // 终结符
            result.add(follow);
            return result;
        }
        // 非终结符
        Set<String> first = GrammarParser.firstSet.get(follow);
        if (!first.contains("$")) { // 无空
            return first;
        }
        // 含空产生式
        result.addAll(look);
        result.addAll(first);
        result.remove("$");
        return result;
    }

    String prodState() {
        return production.toString() + next;
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
