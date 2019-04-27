/**
 * 
 */
package LexicalAnalyzer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chen
 *
 */
public class LexAnalyzer {
    /** ���뻺�� */ 
    private List<String> content = new ArrayList<>();
    /** ��ǰ���� */
    private int rows=0;
    /** ��ǰ���� */
    private int cols=-1;
    /** ��ǰ�� �ַ��� */
    private String curline;
    
    private DFA wordDFA = new WordDFA();
    private DFA digitDFA = new DigitDFA();
    
    /**
     * 
     * @param input Դ�����ļ�
     * @param output    ����ļ�Ŀ¼
     *  output/id
     *  output/token
     */
    public LexAnalyzer(List<String> input,String output) {
        // ��ʼ�� DFA
        wordDFA.init("src/LexicalAnalyzer/word.dfa");
        digitDFA.init("src/LexicalAnalyzer/digit.dfa");
        wordDFA.reset();
        digitDFA.reset();
        Token.initTable("src/LexicalAnalyzer/sortcode");
        // ���ļ�
        content = input;
        // ��ʼ����ǰ��
        curline = content.get(rows);
        controller();
        Token.output(output);
    }
    
    /**
     * 
     * @param input Դ�����ļ�
     * @param output    ����ļ�Ŀ¼
     *  output/id
     *  output/token
     */
    public static void startLexicalAnalyzer(String input,String output) {
        new LexAnalyzer(inputFromFile(input), output);
    }
    
    // ����
    private void controller() {
        for(;rows!=content.size();) {
            char ch = next();
            sort(ch);
        }
    }
    
    // ���ʷ���
    private void sort(char ch) {
        if (ch==' ') {
            return;
        }
        if (DFA.isAlpha(ch)) {
            recogID(ch);
            return;
        }
        if (DFA.isDigit(ch)) {
            recogDIG(ch);
            return;
        }
        if (ch =='/') {
            recogCOM(ch);
            return;
        }
        if (ch =='"') {
            recogSTR(ch);
            return;
        }
        // TODO �����ַ�
        String sin = String.valueOf(ch);
        String dou = sin+next();
        if (Token.isKeyWord(dou)) {
            // ˫�ַ������
            Token.addToken(new Token(Token.getCode(dou), dou , rows+1));
            return;
        }
        back(); // �˻�
        if (Token.isKeyWord(sin)) {
            // ���ַ������
            Token.addToken(new Token(Token.getCode(sin), sin , rows+1));
            return;            
        }
        recordError(rows+1, "�Ƿ��ַ�: "+ch);
    }
    
    /***********************
     *         ʶ��
     ***********************/
    
    // ʶ���ʶ����ؼ���
    private void recogID(char ch) {
        wordDFA.reset();
        String word="";
        while(wordDFA.read(ch)>0) {
            word += ch; // ���
            ch = next(); // ��ȡ��һ��
        }
        back(); // ���������
        
        // �жϱ�ʶ����ؼ���
        if (Token.isKeyWord(word)) {
            // �ؼ���
            int code = Token.getCode(word);
            Token.addToken(new Token(code, word, rows+1));
        }else if (word.equals("false")||word.equals("true")) {
            // ��������
            Token.addToken(new Token(4, word.equals("false")? 0 :1,rows+1));
        }else {
            // ��ʶ��
            Token.addIDToken(word, rows+1);
        }
    } 
    
    
    // ʶ��ע��: /*......*/  
    private void recogCOM(char ch) {
        ch = next();
        if (ch=='*') {
            // ע�Ϳ�ʼ
            int i = cols;
            for(;i<curline.length()-1;i++) {
                if (curline.charAt(i)=='*' && curline.charAt(i+1)=='/') {
                    // ע�ͽ���
                    cols=i+1;
                    return;
                }
            }
            // err ע�Ͳ����
            recordError(rows+1, "ע�Ͳ����");
            // �ƶ��α�
            cols =i+1;
            return;
            
        }else {
            // ��Ϊ '/' �����
            // ����δ����'/' ���������Ϊ�Ƿ��ַ�����
            recordError(rows+1, " ���� '/' �Ƿ� ");
        }
    }
    
    // ʶ������
    private void recogDIG(char ch) {
        digitDFA.reset();
        String word="";
        while(digitDFA.read(ch)>0) {
            word += ch; // ���
            ch = next(); // ��ȡ��һ��
        }
        back(); // ���������
        
        if (word.contains(".") || word.contains("e")) {
            // ������
            Token.addToken(new Token(3, Double.valueOf(word), rows+1));
        }else {
            // ����
            Token.addToken(new Token(2, Integer.valueOf(word), rows+1));
        }
    }
    
    // ʶ���ַ���
    private void recogSTR(char ch) {
        String word="";
        while(true) {
            ch=next();
            if (ch=='"') {
                Token.addToken(new Token(5, word, rows+1));
                break;
            }
            if (cols==-1) {
                // �ѻ��У������
                recordError(rows, "�ַ��������"); //rows ����Ҫ��-1
                return;
            }
            word += ch;
        }
    }
    
    /**
     * �α�ǰ�ƣ�����������ַ�
     * @return ' '����һ�н���������
     */
    private char next() {
        cols++;
        if (cols >= curline.length()) {
            // ������ĩ����������
            cols=-1;
            rows++;
            if (rows!=content.size()) {
                // δ����β
                curline = content.get(rows);
            }
            return ' ';
        }
        return curline.charAt(cols);
    }
    
    private void back() {
        if (cols==-1) {
            rows--;
            cols = content.get(rows).length() -1;
            curline = content.get(rows);
            return;
        }else {
            cols--;
        }
    }
    
    public static String recordError(int linenum,String err) {
        String errmsg = "[Line "+linenum+" ] : " +err+ " .";
        System.err.println(errmsg);
        // TODO �������
        return errmsg;        
    }
    
    /**
     * ���ļ����룬�����ÿ����ĩ�޻��з�
     * @param inputFilePath
     */
    public static List<String> inputFromFile(String inputFilePath) {
        List<String> content = null;
        try {
             content = Files.readAllLines(Paths.get(inputFilePath));
        } catch (IOException e) {
            System.err.println("Failed to read inputfile");
            e.printStackTrace();
        }
        return content;
    }

}
