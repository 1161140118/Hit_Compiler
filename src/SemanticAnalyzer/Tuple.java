/**
 * 
 */
package SemanticAnalyzer;

import java.util.LinkedList;
import java.util.List;
import com.sun.security.ntlm.Client;

/**
 * @author standingby
 * ��Ԫ����ʽ���棬��תΪ����ַ�����Ԫ����ʽ
 *
 */
public class Tuple {
//    public static List<Tuple> tupleList = new LinkedList<>();
    // ��һ�� Ԫ����ʽ�ĵ�ַ
    public static int Address = 0;

    public final String op;
    private String arg1;
    private String arg2;
    private String result;


    public Tuple(String op, String arg1, String arg2, String result) {
        super();
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }

    public Tuple(String op, String arg1, String arg2, int result) {
        super();
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = "" + result;
    }

    public Tuple(String op, int result) {
        this.op = op;
        this.arg1 = "-";
        this.arg2 = "-";
        this.result = "" + result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setResult(int result) {
        setResult(result + "");
    }
    
    public void resultOffset(int offset) {
        setResult(Integer.valueOf(result) + offset);
    }

//    public static void patchResult(int index, int result) {
//        if (index >= tupleList.size()) {
//            return;
//        }
//        tupleList.get(index).setResult(result);
//    }
//
//    public static int addTuple(Tuple tuple) {
//        if (tupleList.add(tuple)) {
//            // System.err.println(" " + Address + " : " + tuple.toTuple4());
//            Address++;
//            return tupleList.size();
//        }
//        return -1;
//    }

    public String toTuple3() {
        if (op.charAt(0) == 'j') {
            if (op.length() == 1) {
                return "goto " + result;
            } else {
                return "if " + arg1 + op.substring(1) + arg2 + "  goto " + result;
            }
        } else {
            if (arg2.equals("-")) {
                return result + " = " + arg1;
            }
            return result + " = " + arg1 + op + arg2;
        }
    }

    public String toTuple4() {
        return "( " + op + " , " + arg1 + " , " + arg2 + " , " + result + " )";
    }

    public static void output() {
        int i=0;
        System.out.println("  Tuples:");
        for (Tuple tuple : SymbolTable.symbolTables.get("main").tupleList) {
            System.out.println("    " + i + " : " + tuple.toTuple4()+ "   "
                    + tuple.toTuple3());
            gui.Client.addTuple(i, tuple);
            i++;            
        }
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return toTuple4();
    }

}
