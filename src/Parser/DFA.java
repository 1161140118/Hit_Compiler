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
     * ��ʼ��DFA
     * @param dfaFilePath DFA�洢�ļ�
     */
    public void init(String dfaFilePath);
    
    /**
     * ��ʼ����ʼ״̬
     */
    public void reset();
    
    /**
     * �����ַ�����ת����һ��״̬
     * @param ch    �����ַ�
     * @return  ��һ��״̬��-1��ʾ����
     */
    public int read(char ch) ;
    
    /**
     * �ж��Ƿ񵽴���ֹ״̬
     * @return 
     *  null ����
     *  string ����
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
