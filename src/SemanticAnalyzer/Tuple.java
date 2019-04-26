/**
 * 
 */
package SemanticAnalyzer;

import java.util.LinkedList;
import java.util.List;

/**
 * @author standingby
 * 以元组形式缓存，可转为三地址码或四元组形式
 *
 */
public class Tuple {
    public static List<Tuple> tupleList = new LinkedList<>();
    String op;
    String arg1;
    String arg2;
    String result;
    
    
    public Tuple(String op, String arg1, String arg2, String result) {
        super();
        this.op = op;
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.result = result;
    }
    
    public static int addTuple(Tuple tuple) {
        if (tupleList.add(tuple)) {
            System.err.println(tuple.toTuple4());
            return tupleList.size();
        }
        return -1;
    }

    public String toTuple3() {
        return null;
    }
    
    public String toTuple4() {
        return  "( "+op+" , "+arg1+" , "+arg2+" , "+result+" )"  ;
    }
    
}
