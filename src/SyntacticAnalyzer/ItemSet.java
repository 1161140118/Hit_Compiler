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
    static int maxId=0; // ����Ŀ�����
    
    /** ��Ŀ��������� */
    
    private final int id;
    private final Set<Item> prim = new HashSet<>();
    /** ��¼��ͬ��Ŀ */
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
                // ��Լ TODO
                continue;
            }
            for (String string : top.nextItemSet.keySet()) {
                ItemSet nextSet = top.nextItemSet.get(start);
                if (itemSetIds.containsKey(nextSet)) {
                    // prim �Ѵ��� ��ת TODO
                }else {
                    // �¼������ɱհ������
                }
                
            }
            
        }
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
        // ����prim����������Ŀ
        for (Item item : thisSet.prim) {
            if (item.production.right.size()==item.next) {
                // ��Լ��Ŀ����generateItemSets�д���
//                LRTable.addReg(thisSet.id,item.production,item.look);
                return;
            }
            // ������Ŀ��
            thisSet.generateItems(item);
        }
    }
    
    private void generateClosure() {
        // Prim��Ŀ
        for (Item item : prim) {
            generateItemSets(item,id);
        }
        // ������Ŀ
        for (Item item : prodStates.values()) {
            generateItemSets(item,id);
        }
    }
    
    /**
     * �ڵ�ǰ��Ŀ�ڣ�����հ�
     * ��Prim��Prim��������Ŀ���ã����ڵ�ǰ�����ڵݹ飬����հ�
     * @param curItem �����հ�����Ŀ
     */
    private void generateItems(Item curItem) {
//    	System.out.println("����հ���"+curItem.prodState());
        String next = curItem.getNext();
        if (next==null) { // �հ��� �ղ���ʽ ��Լ��Ŀ
            LRTable.addReg(this.id, curItem.production, curItem.look);
			return;
		}
        if (GrammarParser.isTerminal(next)) { // ������Ŀ,������Ŀ��ʱ����
			return;
		}
        // ������Ŀ������Ŀ
        List<Production> productions = GrammarParser.productions.get(next);
        Set<String> curLook = curItem.getLook(); // �̳�չ����
        for (Production production : productions) {
            Item item = new Item(   production, 0, curLook);
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
    
    private void generateItemSets(Item curItem,int curId) {
        String next = curItem.getNext();
        if (next==null) { // ��Լ��Ŀ 
            LRTable.addReg(curId,curItem.production,curItem.look);
			return;
		}
        Item newItem = new Item(curItem.production, curItem.next+1, curItem.look);
        // ��Ŀ��ת��
        if (nextItemSet.containsKey(next)) { // ����ת�Ʒ���־�հ�
            // Ŀ����Ŀ���Ѵ���
            nextItemSet.get(next).prim.add(newItem);
//            if (GrammarParser.isTerminal(next)) {
//                // ����
//                LRTable.addShift(curId,next,newId);
//            }else {
//                // Goto
////                System.out.println("ת���Ѵ��ڱհ���"+newItem.prodState()+" when "+next+" "+curId+"-"+newId);
//                LRTable.addGoto(curId,next,newId);
//            }
        }else {
            // ��������Ŀ��
            int newId = maxId++;
            ItemSet newSet = new ItemSet(newId, newItem.production, newItem.next, newItem.look);
//            itemIds.put(newItem, newId);
            // ת�Ƶ�����Ŀ
            nextItemSet.put(next, newSet);
            if (GrammarParser.isTerminal(next)) {
                // ����
                LRTable.addShift(curId,next,newId);
            }else {
                // Goto
//                System.out.println("ת���±հ�:"+newItem.prodState()+" when "+next+" "+curId+"-"+newId);
                LRTable.addGoto(curId,next,newId);
            }
            // �ݹ����
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
