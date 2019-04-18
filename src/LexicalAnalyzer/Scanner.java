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
public class Scanner {
    /** 输入缓冲 */ 
    private List<String> content = new ArrayList<>();
    /** 当前行数 */
    private int rows=0;
    /** 当前列数 */
    private int cols=-1;
    /** 当前行 字符串 */
    private String curline;
    
    private DFA wordDFA = new WordDFA();
    private DFA digitDFA = new DigitDFA();
    
    /**
     * 
     * @param input 源程序文件
     * @param output    输出文件目录
     *  output/id
     *  output/token
     */
    public Scanner(String input,String output) {
        // 初始化 DFA
        wordDFA.init("src/LexicalAnalyzer/word.dfa");
        digitDFA.init("src/LexicalAnalyzer/digit.dfa");
        wordDFA.reset();
        digitDFA.reset();
        Token.initTable("src/LexicalAnalyzer/sortcode");
        // 读文件
        content = inputFromFile(input);
        // 初始化当前行
        curline = content.get(rows);
        controller();
        Token.output(output);
    }
    
    /**
     * 
     * @param input 源程序文件
     * @param output    输出文件目录
     *  output/id
     *  output/token
     */
    public static void startLexicalAnalyzer(String input,String output) {
        new Scanner(input, output);
    }
    
    // 主控
    private void controller() {
        for(;rows!=content.size();) {
            char ch = next();
            sort(ch);
        }
    }
    
    // 单词分类
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
        // TODO 其他字符
        String sin = String.valueOf(ch);
        String dou = sin+next();
        if (Token.isKeyWord(dou)) {
            // 双字符运算符
            Token.addToken(new Token(Token.getCode(dou), dou , rows+1));
            return;
        }
        back(); // 退回
        if (Token.isKeyWord(sin)) {
            // 单字符运算符
            Token.addToken(new Token(Token.getCode(sin), sin , rows+1));
            return;            
        }
        recordError(rows+1, "非法字符: "+ch);
    }
    
    /***********************
     *         识别
     ***********************/
    
    // 识别标识符或关键字
    private void recogID(char ch) {
        wordDFA.reset();
        String word="";
        while(wordDFA.read(ch)>0) {
            word += ch; // 添加
            ch = next(); // 读取下一个
        }
        back(); // 多读，回退
        
        // 判断标识符或关键字
        if (Token.isKeyWord(word)) {
            // 关键字
            int code = Token.getCode(word);
            Token.addToken(new Token(code, word, rows+1));
        }else if (word.equals("false")||word.equals("true")) {
            // 布尔变量
            Token.addToken(new Token(4, word.equals("false")? 0 :1,rows+1));
        }else {
            // 标识符
            Token.addIDToken(word, rows+1);
        }
    } 
    
    
    // 识别注释: /*......*/  
    private void recogCOM(char ch) {
        ch = next();
        if (ch=='*') {
            // 注释开始
            int i = cols;
            for(;i<curline.length()-1;i++) {
                if (curline.charAt(i)=='*' && curline.charAt(i+1)=='/') {
                    // 注释结束
                    cols=i+1;
                    return;
                }
            }
            // err 注释不封闭
            recordError(rows+1, "注释不封闭");
            // 移动游标
            cols =i+1;
            return;
            
        }else {
            // 作为 '/' 运算符
            // 由于未定义'/' 运算符，作为非法字符处理
            recordError(rows+1, " 符号 '/' 非法 ");
        }
    }
    
    // 识别数字
    private void recogDIG(char ch) {
        digitDFA.reset();
        String word="";
        while(digitDFA.read(ch)>0) {
            word += ch; // 添加
            ch = next(); // 读取下一个
        }
        back(); // 多读，回退
        
        if (word.contains(".") || word.contains("e")) {
            // 浮点数
            Token.addToken(new Token(3, Double.valueOf(word), rows+1));
        }else {
            // 整数
            Token.addToken(new Token(2, Integer.valueOf(word), rows+1));
        }
    }
    
    // 识别字符串
    private void recogSTR(char ch) {
        String word="";
        while(true) {
            ch=next();
            if (ch=='"') {
                Token.addToken(new Token(5, word, rows+1));
                break;
            }
            if (cols==-1) {
                // 已换行，不封闭
                recordError(rows, "字符串不封闭"); //rows 不需要再-1
                return;
            }
            word += ch;
        }
    }
    
    /**
     * 游标前移，并获得所在字符
     * @return ' '：上一行结束，换行
     */
    private char next() {
        cols++;
        if (cols >= curline.length()) {
            // 到达行末，换行重置
            cols=-1;
            rows++;
            if (rows!=content.size()) {
                // 未到行尾
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
        // TODO 错误输出
        return errmsg;        
    }
    
    /**
     * 从文件读入，读入后每行行末无换行符
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
    
    public static void main(String[] args) {
        new Scanner("src/source", "src/result");
        
    }

}
