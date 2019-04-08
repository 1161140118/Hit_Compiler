/**
 * 
 */
package Parser;

/**
 * @author chen
 *
 */
public interface DFA {
    
    
    /**
     * 初始化DFA
     * @param dfaFilePath DFA存储文件
     */
    public void init(String dfaFilePath);
    
    /**
     * 初始化开始状态
     */
    public void reset();
    
    /**
     * 读入字符，跳转到下一个状态
     * @param ch    读入字符
     * @return  下一个状态，-1表示错误
     */
    public int read(char ch) ;
    
    /**
     * 判断是否到达终止状态
     * @return 
     *  null 错误
     *  string 类型
     */
    public String end();
    
    /**
     * @param ch
     * @return
     */
    public static boolean isAlpha(char ch) {
        if (ch>=65 && ch<=90) {
            return true;
        }
        if (ch>=97 && ch<=122) {
            return true;
        }
        return false;
    }
    
    public static boolean isDigit(char ch) {
        if (ch>=48 && ch<=57) {
            return true;
        }
        return false;
    }
    
    public static void main(String[] args) {
        new Scanner("src/source", "src/result");
        
    }

}
