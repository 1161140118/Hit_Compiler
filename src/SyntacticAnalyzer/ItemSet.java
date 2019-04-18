package SyntacticAnalyzer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author standingby
 *  ������Ŀ���հ���ͨ��DFAת�����Table
 */
public class ItemSet {
    public static Map<Item, Integer> itemIds = new HashMap<>();
    static int maxId=0; // ����Ŀ�����
    
    /** ��Ŀ��������� */
    private final int id;
    private final Item prim;
    private Map<String, Item> prodStates = new LinkedHashMap<>();
    
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
     * �㷨
     * 1. �����հ�
     * 1.1 ��������Ŀ��next���ţ��Ƶ�
     * 1.2 �ϲ���ͬ��Ŀ����ͬչ������
     * 2. �Ƴ��±հ�
     * 2.1 ������Ŀ�������һ����Ŀ
     * 2.2 ������Ŀ�����Ѵ��ڣ���ֱ��ת��
     * 2.3 ������Ŀ���������ڣ����½���Ŀ��������1��ʼ
     **************/
    
    private static void generate(ItemSet thisSet) {
        // ��Լ
        if (thisSet.prim.production.right.size()==thisSet.prim.next) {
            Table.addReg(thisSet.id,thisSet.prim.production,thisSet.prim.look);
            return;
        }
        // ������Ŀ��
        thisSet.generateItems(thisSet.prim);
        // ����Ŀ������Ŀ����ת��
        // Prim��Ŀ
        generateItemSets(thisSet.prim,thisSet.id);
        // ������Ŀ
        for (Item item : thisSet.prodStates.values()) {
            generateItemSets(item,thisSet.id);
        }
        
    }
    
    /**
     * ���ڵ�ǰ�����ڵݹ飬����հ�
     * @param curItem
     */
    private void generateItems(Item curItem) {
//    	System.out.println("����հ���"+curItem.prodState());
        String next = curItem.getNext();
        if (next==null) { // ��Լ��Ŀ
			return;
		}
        if (GrammarParser.isTerminal(next)) { // ������Ŀ
			return;
		}
        // ������Ŀ������Ŀ
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = curItem.getLook(); // �̳�չ����
        for (Production production : productions) {
            Item item = new Item(production, 0, curLook);
            if (this.prodStates.keySet().contains(item.prodState())) {
                // ��Ŀ�Ѵ��ڣ��ϲ�look
                this.prodStates.get(item.prodState()).look.addAll(curLook);
                continue;
            }
            // ����Ŀ
            this.prodStates.put(item.prodState(), item);
            // �ݹ��������Ŀ�ɲ�����������Ŀ
            this.generateItems(item);
		}
    }
    
    private static void generateItemSets(Item curItem,int curId) {
        String next = curItem.getNext();
        if (next==null) { // ��Լ��Ŀ 
			return;
		}
        Item newItem = new Item(curItem.production, curItem.next+1, curItem.look);
        // ��Ŀ��ת��
        if (itemIds.containsKey(newItem)) {
            // Ŀ����Ŀ���Ѵ���
            int newId = itemIds.get(newItem);
            if (GrammarParser.isTerminal(next)) {
                // ����
                Table.addShift(curId,next,newId);
            }else {
                // Goto
//                System.out.println("ת���Ѵ��ڱհ���"+newItem.prodState()+" when "+next+" "+curId+"-"+newId);
                Table.addGoto(curId,next,newId);
            }
        }else {
            // ��������Ŀ��
            int newId = maxId++;
            ItemSet newSet = new ItemSet(newId, newItem.production, newItem.next, newItem.look);
            itemIds.put(newItem, newId);
            // ת�Ƶ�����Ŀ
            if (GrammarParser.isTerminal(next)) {
                // ����
                Table.addShift(curId,next,newId);
            }else {
                // Goto
//                System.out.println("ת���±հ�:"+newItem.prodState()+" when "+next+" "+curId+"-"+newId);
                Table.addGoto(curId,next,newId);
            }
            // �ݹ����
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
