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
    public static final int Shift = 1;
    public static final int Red = 2;
    public static final int Goto = 3;
    static Map<Integer, Map<String, Action>> table = new HashMap<>();

    public static void addShift(int src, String string, int target) {
        Action action = new Action(Shift, target);
//        System.out.println(src + " 移入：" + string + ":" + action.toString());
        if (table.containsKey(src)) {
            // 源状态已存在
            if (table.get(src).containsKey(string)) {
//                System.err.println("Shift conflict : " + src + " , " + string);
//                System.err.println(table.get(src).get(string) + "  <==  " + action);
//                System.err.println("已忽略移入.");
                return;
            }
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
    }

    public static void addRed(int src, Production production, Set<String> strings) {
        Action action = new Action(Red, production);
//        System.out.println(src + " 规约：" + strings + ":" + action.toString());
        Map<String, Action> map = new HashMap<>();
        for (String string : strings) {
            if (table.containsKey(src)) {
                if (table.get(src).containsKey(string)) {
//                    System.err.println("Reduce conflict : " + src + " , " + string);
//                    System.err.println(table.get(src).get(string) + "  <==  " + action);
                }
            }
            map.put(string, action);
        }
        if (table.containsKey(src)) {
            table.get(src).putAll(map);
        } else {
            table.put(src, map);
        }
    }

    public static void addGoto(int src, String string, int target) {
        Action action = new Action(Goto, target);
//        System.out.println(src + " 转移：" + string + ":" + action.toString());
        if (table.keySet().contains(src)) {
            // 源状态已存在
            if (table.get(src).containsKey(string)) {
                System.err.println("Goto conflict : " + src + " , " + string);
                System.err.println(table.get(src).get(string) + "  <==  " + action);
            }
            table.get(src).put(string, action);
            return;
        }
        Map<String, Action> map = new HashMap<>();
        map.put(string, action);
        table.put(src, map);
    }

    public static void output(String filepath) {
        try {
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(new File(filepath))));
            for (Integer integer : table.keySet()) {
                Map<String, Action> map = table.get(integer);
                writer.write(integer + " : ");
                for (String string : map.keySet()) {
                    Action action = map.get(string);
                    writer.write("[" + string + ":" + action.toString() + "]");
                }
                writer.write("\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


class Action {
    int type;
    int target; // 状态转移
    Production production; // 规约

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
                act = "Shift " + target;
                break;
            case 3:
                act = "Goto " + target;
                break;
            case 2:
                act = "Reduce " + production.toString();
                break;
            default:
                break;
        }
        return act;
    }

}
