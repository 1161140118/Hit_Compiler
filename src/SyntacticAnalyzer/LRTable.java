/**
 * 
 */
package SyntacticAnalyzer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author standingby
 * Action table, Goto table
 */
public class Table {
    public static final int Shift=1;
    public static final int Reg=2;
    public static final int Goto=3;
    static Map<Integer, Map<String,Action>> table = new HashMap<>();
    
    public static void addShift(int src,String string,int target) {
        Action action = new Action(Shift, target);
        System.out.println(src+" 记录移入S："+string+":"+action.toString());
        if (table.keySet().contains(src)) {
            // 源状态已存在
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
    }
    
    public static void addReg(int src,Production production,Set<String> strings) {
        Action action = new Action(Reg,production);
        System.out.println(src+" 记录规约R："+strings+":"+action.toString());
        Map<String, Action> map = new HashMap<>();
        for (String string : strings) {
            map.put(string, action);
        }
        table.put(src, map);
    }
    
    public static void addGoto(int src, String string, int target) {
        Action action = new Action(Goto, target);
        System.out.println(src+" 记录转移G："+string+":"+action.toString());
        if (table.keySet().contains(src)) {
            // 源状态已存在
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
    }
    
    public static void output() {
    	for (Integer integer : table.keySet()) {
			Map<String, Action> map = table.get(integer);
			
			System.out.print(integer+" : ");
			for (String string : map.keySet()) {
				Action action = map.get(string);
				System.out.print("["+string+":"+action.toString()+"]");
			}
			System.out.println();
		}
    }
}

class Action{
    int type;
    int target; // 状态转移
    Production production;  // 规约
    
    public Action(int type, int target) {
        super();
        this.type = type;
        this.target = target;
    }
    
    public Action(int type, Production production) {
        super();
        this.type = type;
        this.production = production;
    }
    
    @Override
    public String toString() {
    	String act = "";
    	switch (type) {
		case 1:
			act = "S"+target;
			break;
		case 3:
			act = ""+target;
			break;
		case 2:
			act = "R "+production.toString();
			break;
		default:
			break;
		}
    	return act;
    }
    
}