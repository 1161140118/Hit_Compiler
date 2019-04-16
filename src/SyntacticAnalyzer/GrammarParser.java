/**
 * 
 */
package SyntacticAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    Set<Production> productions = new HashSet<>();
    //Map<String,Production> productions = new HashMap<>();
    /** �ս���� */
    Set<String> terminals = new HashSet<>();
    /** ���ս���� */
    Set<String> nonTerminals = new HashSet<>();
    /** First�� */
    Map<String, Set<String>> firstSet = new HashMap<>();
    
    public GrammarParser(String filepath) {
        getGrammerFromFile(filepath);
        for (Production production : productions) {
            System.out.println(production);
        }
    }
    
    
    
    private void getGrammerFromFile(String filepath) {
        try {
            List<String> strings = Files.readAllLines(Paths.get(filepath));
            for (String string : strings) {
                productions.add(new Production(string));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
    
    

    /**
     * @param args
     */
    public static void main(String[] args) {
        new GrammarParser("src/SyntacticAnalyzer/grammar");
    }

}
