/**
 * 
 */
package SemanticAnalyzer;

/**
 * @author standingby
 * 以元组形式缓存，可转为三地址码或四元组形式
 *
 */
public class Tuple {
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

    public String toTuple3() {
        return null;
    }
    
    public String toTuple4() {
        return null;
    }
    
}
