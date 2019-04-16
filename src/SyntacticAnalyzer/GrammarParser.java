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
 * 从文件中获得grammer,构建产生式和first集<br>
 * 
 */
public class GrammarParser {
    /** 产生式 */
    Set<Production> productions = new HashSet<>();
    //Map<String,Production> productions = new HashMap<>();
    /** 终结符集 */
    Set<String> terminals = new HashSet<>();
    /** 非终结符集 */
    Set<String> nonTerminals = new HashSet<>();
    /** First集 */
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
