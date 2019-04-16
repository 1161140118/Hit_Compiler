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
    
    public static void addShift() {
        
    }
    
    public static void addReg(int id,Production production,Set<String> strings) {
        Action action = new Action(Reg,production);
        Map<String, Action> map = new HashMap<>();
        for (String string : strings) {
            map.put(string, action);
        }
        table.put(id, map);
    }
    
    public static void addGoto() {
        
    }

}

class Action{
    int type;
    int target; // ×´Ì¬×ªÒÆ
    Production production;  // ¹æÔ¼
    
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