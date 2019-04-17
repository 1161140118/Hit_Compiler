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
 *  ������Ŀ���հ���ͨ��DFAת�����Table
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
     * �㷨
     * 1. �����հ�
     * 1.1 ��������Ŀ��next���ţ��Ƶ�
     * 1.2 �ϲ���ͬ��Ŀ����ͬչ������
     * 2. �Ƴ��±հ�
     * 2.1 ������Ŀ�������һ����Ŀ
     * 2.2 ������Ŀ�����Ѵ��ڣ���ֱ��ת��
     * 2.3 ������Ŀ���������ڣ����½���Ŀ��������1��ʼ
     **************/
    
    public void generate() {
        // ��Լ
        if (prim.production.right.size()<=prim.next) {
            Table.addReg(id,prim.production,prim.look);
            return;
        }
        // ������Ŀ��
        generateItems(prim);
        // ����Ŀ������Ŀ����ת��
        // Key��Ŀ
        generateItemSets(prim);
        // ������Ŀ
        for (Item item : prodStates.values()) {
            generateItemSets(item);
        }
        
    }
    
    private void generateItems(Item curItem) {
        String next = curItem.getNext();
        if (GrammarParser.isTerminal(next)) {
			return;
		}
        // ������Ŀ������Ŀ
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = curItem.getLook();
        for (Production production : productions) {
            Item item = new Item(production, 0, curLook);
            if (prodStates.keySet().contains(item.prodState())) {
                // ��Ŀ�Ѵ��ڣ��ϲ�look
                prodStates.get(item.prodState()).look.addAll(curLook);
                continue;
            }
            // ����Ŀ
            prodStates.put(item.prodState(), item);
            // �ݹ��������Ŀ�ɲ�����������Ŀ
            generateItems(item);
		}
    }
    
    private void generateItemSets(Item curItem) {
        String next = curItem.getNext();
        Item item = new Item(curItem.production, curItem.next+1, curItem.look);
        // ��Ŀ��ת��
        int newId;
        if (itemIds.containsKey(item)) {
            // Ŀ����Ŀ���Ѵ���
            newId = itemIds.get(item);
            if (GrammarParser.isTerminal(next)) {
                // ����
                Table.addShift(id,next,newId);
            }else {
                // Goto
                Table.addGoto(id,next,newId);
            }
        }else {
            // ��������Ŀ��
            newId = curId++;
            ItemSet newSet = new ItemSet(newId, item.production, item.next, item.look);
            itemIds.put(item, newId);
            // ת�Ƶ�����Ŀ
            if (GrammarParser.isTerminal(next)) {
                // ����
                Table.addShift(id,next,newId);
            }else {
                // Goto
                Table.addGoto(id,next,newId);
            }
            // �ݹ����
            newSet.generate();
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
        return production.right.get(next);
    }
    
    Set<String> getLook() {
    	if (production.right.size()<=next+1) { // next����һ����ĩβ
			return look;
		}
    	String follow = production.right.get(next+1);
    	if (GrammarParser.isTerminal(follow)) { // �ս��
			return new HashSet<>(Arrays.asList(follow));
		}
    	// ���ս��
    	Set<String> first = GrammarParser.firstSet.get(follow);
    	if (!first.contains("$")) { // �޿�
            return first;
        }
    	// ���ղ���ʽ
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
