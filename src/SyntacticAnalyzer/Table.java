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
        if (table.keySet().contains(src)) {
            // 源状态已存在
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
    }
    
    public static void addReg(int id,Production production,Set<String> strings) {
        Action action = new Action(Reg,production);
        Map<String, Action> map = new HashMap<>();
        for (String string : strings) {
            map.put(string, action);
        }
        table.put(id, map);
    }
    
    public static void addGoto(int src, String string, int target) {
        Action action = new Action(Goto, target);
        if (table.keySet().contains(src)) {
            // 源状态已存在
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
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
    
}