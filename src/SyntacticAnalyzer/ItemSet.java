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
 *  ������Ŀ���հ���ͨ��DFAת�����Table
 */
public class ItemSet {
    private static Map<ItemSet, Integer> itemSetIds = new HashMap<>();
    private static Queue<ItemSet> queue = new LinkedList<>();
    static int maxId = 0; // ����Ŀ�����

    /** ��Ŀ��������� */
    private int id;
    private final Set<Item> prim = new HashSet<>();

    /** ��¼��ͬ��Ŀ���Ժϲ�look����  */
    private Map<String, Item> prodStates = new LinkedHashMap<>();
    /** �����հ�ʱ���ѱ�����Item */
    private Set<Item> visitedItem = new HashSet<>();
    /** ��ǰItemSet�����Ƴ���ItemSet */
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

        queue.add(first);
        while (!queue.isEmpty()) {
            ItemSet top = queue.poll();
            top.generate();
            top.generateClosure();
            for (String string : top.nextItemSet.keySet()) {
                ItemSet nextSet = top.nextItemSet.get(string);
                if (itemSetIds.containsKey(nextSet)) {
                    // prim �Ѵ��� ��ת
                    if (GrammarParser.isTerminal(string)) {
                        LRTable.addShift(top.id, string, itemSetIds.get(nextSet));
                    } else {
                        LRTable.addGoto(top.id, string, itemSetIds.get(nextSet));
                    }
                } else {
                    // �¼�����ӣ���ת
                    nextSet.id = maxId++;
                    itemSetIds.put(nextSet, nextSet.id);
                    queue.add(nextSet);
                    if (GrammarParser.isTerminal(string)) {
                        LRTable.addShift(top.id, string, nextSet.id);
                    } else {
                        LRTable.addGoto(top.id, string, nextSet.id);
                    }
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
     * �㷨
     * 1. �����հ�
     * 1.1 ��������Ŀ��next���ţ��Ƶ�
     * 1.2 �ϲ���ͬ��Ŀ����ͬչ������
     * 2. �Ƴ��±հ�
     * 2.1 ������Ŀ�������һ����Ŀ
     * 2.2 ������Ŀ�����Ѵ��ڣ���ֱ��ת��
     * 2.3 ������Ŀ���������ڣ����½���Ŀ��������1��ʼ
     **************/

    private void generate() {
        // ����prim����������Ŀ
        for (Item item : prim) {
            if (item.production.right.size() == item.next) {
                // ��Լ��Ŀ
                // �������ȼ�������������
                Set<String> redset = new HashSet<>();
                redset.addAll(item.look);
                redset.removeAll(item.production.priority);
                LRTable.addRed(id, item.production, redset);
                continue;
            }
            // ������Ŀ�հ�
            generateItems(item);
        }
//        showItemSet(this);
    }

    /**
     * �ڵ�ǰ��Ŀ�ڣ�����հ�
     * ��Primֱ�ӵ��û�Prim��������Ŀ�ݹ���ã����ڵ�ǰ�����ڵݹ飬����հ�
     * @param curItem �����հ�����Ŀ
     */
    private void generateItems(Item curItem) {
        if (visitedItem.contains(curItem)) {
            return;
        }
        visitedItem.add(curItem);
        // System.out.println("����հ���"+curItem.prodState());
        String next = curItem.getNext();
        if (next == null) { // �հ��� �ղ���ʽ ��Լ��Ŀ
            LRTable.addRed(this.id, curItem.production, curItem.look);
            return;
        }
        if (GrammarParser.isTerminal(next)) { // ������Ŀ,������Ŀ��ʱ����
            return;
        }
        // ������Ŀ������Ŀ
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = new HashSet<>();
        curLook.addAll(curItem.inhLook()); // �̳�չ����
        for (Production production : productions) {
            Item item = new Item(production, 0, curLook);
            if (prodStates.keySet().contains(item.prodState())) {
                // ��Ŀ�Ѵ��ڣ��ϲ�look
                prodStates.get(item.prodState()).look.addAll(curLook);
            }else {
                // ����Ŀ
                prodStates.put(item.prodState(), item);
            }
            // �ݹ��������Ŀ�ɲ�����������Ŀ
            generateItems(item);
        }
    }

    /**
     * ������ǰ��Ŀ����Ŀ
     * ����generateItemSets ���� nextItemSet
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
        if (next == null) { // ��Լ��Ŀ
            // LRTable.addReg(curId, curItem.production, curItem.look);
            return;
        }
        Item newItem = new Item(curItem.production, curItem.next + 1, curItem.look);
        // ��Ŀ��ת��
        if (nextItemSet.containsKey(next)) { // ����ת�Ʒ���־�հ�
            // Ŀ����Ŀ���Ѵ���
            nextItemSet.get(next).prim.add(newItem);
        } else {
            // ��������Ŀ��
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
        if (production.right.size() <= next + 1) { // next����һ����ĩβ
            result.addAll(look);
            return result;
        }
        String follow = production.right.get(next + 1);
        if (GrammarParser.isTerminal(follow)) { // �ս��
            result.add(follow);
            return result;
        }
        // ���ս��
        Set<String> first = GrammarParser.firstSet.get(follow);
        if (!first.contains("$")) { // �޿�
            return first;
        }
        // ���ղ���ʽ
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
