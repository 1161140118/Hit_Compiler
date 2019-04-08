/**
 * 
 */
package Parser;

/**
 * @author chen
 *  识别数字的DFA，无符号整数或浮点数，可用e表示科学记数法
 */
public class DigitDFA extends WordDFA implements DFA {

    /* (non-Javadoc)
     * @see Parser.DFA#read(char)
     */
    @Override
    public int read(char ch) {
        Character curch=null;
        if (ch=='.' || ch=='+' || ch=='-' || ch=='e') {
            curch = ch;
        }
        if (DFA.isDigit(ch)) {
            curch = 'd';
        }
        if (curch==null) {
            // TODO 错误处理
            return -1;
        }
        if (!transTable.get(curstate).containsKey(curch)) {
            return -1;
        }
        curstate = transTable.get(curstate).get(curch);
        return curstate; 
    }
    
    public static void main(String[] args) {
        DFA digit = new DigitDFA();
        digit.init("src/Parser/digit.dfa");
        String test = "10e-0.5";
        for (int i=0;i<test.length();i++) {
            System.out.println(digit.read(test.charAt(i)));
        }
        System.out.println(digit.end());
    }

}
