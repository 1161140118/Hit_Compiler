package SyntacticAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author standingby
 * 从文件中获得grammer,构建产生式和first集<br>
 * 
 */
public class GrammarParser {
    /** 产生式 */
    // Set<Production> productions = new HashSet<>();
    public static final Map<String, List<Production>> productions = new HashMap<>();
    /** 终结符集 */
    public static final Set<String> terminals = new HashSet<>();
    /** 非终结符集 */
    public static final Set<String> nonTerminals = new HashSet<>();
    /** First集 */
    public static final Map<String, Set<String>> firstSet = new HashMap<>();
    /** 开始产生式 */
    public static Production START;
    /** 开始符号 */
    public static String startSymbol;


    public static void parseGrammar(String filepath) {
        setGrammerFromFile(filepath);
        setFirstSet();
        dropEmpty();
        startSymbol = START.left;
    }

    /**
     * delete $ in production.
     */
    private static void dropEmpty() {
        for (List<Production> list : productions.values()) {
            for (Production production : list) {
                production.right.remove("$");
            }
        }
    }

    private static void setFirstSet() {
        // 遍历终结符,firstSet 为自身
        for (String string : terminals) {
            firstSet.put(string, new HashSet<>(Arrays.asList(string)));
        }
        // 遍历非终结符
        for (String string : nonTerminals) {
            if (firstSet.keySet().contains(string)) {
                continue;
            }
            firstSet.put(string, getFirst(string));
        }
    }

    private static Set<String> getFirst(String string) {
        if (firstSet.containsKey(string)) {
            // 递归到终结符，或已确定的非终结符，停止
            return firstSet.get(string);
        }
        Set<String> result = new HashSet<>();
        List<Production> list = productions.get(string);
        for (Production production : list) {
            // 遍历可能推导出的右部
            for (String r : production.right) {
                Set<String> first = getFirst(r);
                result.addAll(first);
                if (!first.contains("$")) {
                    // 有空产生式，则继续下一个
                    break;
                }
            }
        }
        return result;
    }

    private static void setGrammerFromFile(String filepath) {
        boolean start = true;
        try {
            List<String> strings = Files.readAllLines(Paths.get(filepath));
            for (String string : strings) {
                if (string.length() == 0 || string.charAt(0) == '#') {
                    continue; // 空行或注释
                }
                Production production = new Production(string);
                nonTerminals.add(production.left);
                terminals.addAll(production.right);

                // 加入 productions
                if (productions.keySet().contains(production.left)) {
                    productions.get(production.left).add(production);
                } else {
                    List<Production> list = new ArrayList<>();
                    list.add(production);
                    productions.put(production.left, list);
                }

                if (start) { // 设置开始产生式
                    START = production;
                    start = false;
                }

            }
            // 去除所有非终结符，得到终结符
            terminals.removeAll(nonTerminals);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTerminal(String string) {
        return terminals.contains(string);
    }


}


