/**
 * 
 */
package LexicalAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author chen
 * ʶ�𵥴ʵ�DFA������ĸ���»��߿�ͷ
 */
public class WordDFA implements DFA {
    protected Map<Integer, Map<Character,Integer>> transTable = new HashMap<>();
    protected Map<Integer, String> result = new HashMap<>();
    protected int curstate=0; // ��ǰ״̬����ʼ��Ϊ��ʼ״̬0

    /* (non-Javadoc)
     * @see Parser.DFA#read(char)
     */
    @Override
    public int read(char ch) {
        Character curch=null;
        if (ch=='_') {
            curch = ch;
        }
        if (DFA.isAlpha(ch)) {
            curch = 'a';
        }
        if (DFA.isDigit(ch)) {
            curch = 'd';
        }
        if (curch==null) {
            // TODO ������
            return -1;
        }
        curstate = transTable.get(curstate).get(curch);
        return curstate; 
    }

    /* (non-Javadoc)
     * @see Parser.DFA#end()
     */
    @Override
    public String end() {
        return result.get(curstate);
    }

    /**
     * ��ʼ��ת�Ʊ���н�״̬
     * @see LexicalAnalyzer.DFA#init(java.lang.String)
     */
    @Override
    public void init(String dfaFilePath) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(dfaFilePath));
            int sum = lines.size();
            int i=0;
            // ״̬ת�Ʊ�
            for (;i<sum;i++) {
                String string = lines.get(i);
                if (string.length()==0) {
                    break;
                }
                // ����������
                String[] strings = string.split(" ");
                Integer src = Integer.valueOf(strings[0]);
                Character ch = strings[1].charAt(0);
                Integer tar = Integer.valueOf(strings[2]);
                
                // ���
                if (transTable.containsKey(src)) {
                    // ���е�ǰ״̬
                    transTable.get(src).put(ch, tar);
                }else { // �޵�ǰ״̬
                    Map<Character, Integer> map = new HashMap<>();
                    map.put(ch, tar);
                    transTable.put(src, map);
                }
            }
            // �ս�״̬��
            for(;i<sum;i++) {
                String string = lines.get(i);
                if (string.length()==0) {
                    continue;
                }
                // ����������
                String[] strings = string.split(" ");
                Integer src = Integer.valueOf(strings[0]);
                String str = strings[1];
                
                // ���
                result.put(src, str);                
            }
            
        } catch (IOException e) {
            System.err.println("Failed to parse "+dfaFilePath);
            e.printStackTrace();
        }
    }

    /* (non-Javadoc)
     * @see Parser.DFA#reset()
     */
    @Override
    public void reset() {
        curstate = 0;
    }
    
    public static void main(String[] args) {
        DFA word = new WordDFA();
        word.init("src/Parser/word.dfa");
        word.reset();
        String test = "_ab1";
        for (int i=0;i<test.length();i++) {
            word.read(test.charAt(i));
        }
        System.out.println(word.end());
        
    }
    
}
