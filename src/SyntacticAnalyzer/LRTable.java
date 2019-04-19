/**
 * 
 */
package SyntacticAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author standingby
 * Action table, Goto table
 */
public class LRTable {
    public static final int Shift=1;
    public static final int Reg=2;
    public static final int Goto=3;
    static Map<Integer, Map<String,Action>> table = new HashMap<>();
    
    public static void addShift(int src,String string,int target) {
        Action action = new Action(Shift, target);
        System.out.println(src+" ���룺"+string+":"+action.toString());
        if (table.keySet().contains(src)) {
            // Դ״̬�Ѵ���
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
    }
    
    public static void addReg(int src,Production production,Set<String> strings) {
        Action action = new Action(Reg,production);
        System.out.println(src+" ��Լ��"+strings+":"+action.toString());
        Map<String, Action> map = new HashMap<>();
        for (String string : strings) {
            map.put(string, action);
        }
        table.put(src, map);
    }
    
    public static void addGoto(int src, String string, int target) {
        Action action = new Action(Goto, target);
        System.out.println(src+" ת�ƣ�"+string+":"+action.toString());
        if (table.keySet().contains(src) ){
            // Դ״̬�Ѵ���
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
    }
    
    public static void output(String filepath) {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filepath))));
            for (Integer integer : table.keySet()) {
                Map<String, Action> map = table.get(integer);
                writer.write(integer+" : ");
                for (String string : map.keySet()) {
                    Action action = map.get(string);
                    writer.write("["+string+":"+action.toString()+"]");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}

class Action{
    int type;
    int target; // ״̬ת��
    Production production;  // ��Լ
    
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