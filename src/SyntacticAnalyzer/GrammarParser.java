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
 * ���ļ��л��grammer,��������ʽ��first��<br>
 * 
 */
public class GrammarParser {
    /** ����ʽ */
    // Set<Production> productions = new HashSet<>();
    public static final Map<String, List<Production>> productions = new HashMap<>();
    /** �ս���� */
    public static final Set<String> terminals = new HashSet<>();
    /** ���ս���� */
    public static final Set<String> nonTerminals = new HashSet<>();
    /** First�� */
    public static final Map<String, Set<String>> firstSet = new HashMap<>();
    /** ��ʼ����ʽ */
    public static Production START;
    /** ��ʼ���� */
    public static String startSymbol;


    public static void parseGrammar(String filepath) {
        // ���ļ��л���ķ�
        setGrammerFromFile(filepath);
        // ���� First ��
        setFirstSet();
        // ȥ�� $
        dropEmpty();
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
        // �����ս��,firstSet Ϊ����
        for (String string : terminals) {
            firstSet.put(string, new HashSet<>(Arrays.asList(string)));
        }
        // �������ս��
        for (String string : nonTerminals) {
            if (firstSet.keySet().contains(string)) {
                continue;
            }
            firstSet.put(string, getFirst(string));
        }
    }

    private static Set<String> getFirst(String string) {
        if (firstSet.containsKey(string)) {
            // �ݹ鵽�ս��������ȷ���ķ��ս����ֹͣ
            return firstSet.get(string);
        }
        Set<String> result = new HashSet<>();
        List<Production> list = productions.get(string);
        for (Production production : list) {
            // ���������Ƶ������Ҳ�
            if (production.right.get(0).equals(string)) {
                // ��ݹ�
                continue;
            }
            for (String r : production.right) {
                Set<String> first = getFirst(r);
                result.addAll(first);
                if (!first.contains("$")) {
                    // �пղ���ʽ���������һ��
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
                    continue; // ���л�ע��
                }
                Production production = new Production(string);
                nonTerminals.add(production.left);
                terminals.addAll(production.right);

                // ���� productions
                if (productions.keySet().contains(production.left)) {
                    productions.get(production.left).add(production);
                } else {
                    List<Production> list = new ArrayList<>();
                    list.add(production);
                    productions.put(production.left, list);
                }

                if (start) { // ���ÿ�ʼ����ʽ
                    START = production;
                    startSymbol = production.left;
                    start = false;
                }

            }
            // ȥ�����з��ս�����õ��ս��
            terminals.removeAll(nonTerminals);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isTerminal(String string) {
        return terminals.contains(string);
    }


}


