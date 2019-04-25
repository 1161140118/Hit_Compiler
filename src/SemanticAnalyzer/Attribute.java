/**
 * 
 */
package SemanticAnalyzer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author standingby
 *
 */
public class Attribute {
    public String name;
    public Map<String, String> attrs;

    public Attribute(String name) {
        super();
        this.name = name;
    }
    
    public Attribute(String name, String type, String value) {
        this.name = name;
        attrs = new HashMap<>();
        attrs.put(type, value);
    }
    
    

}
