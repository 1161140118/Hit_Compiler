/**
 * 
 */
package SemanticAnalyzer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author standingby
 *
 */
public class Attribute {
    public String name;
    private Map<String, String> attrs = new HashMap<>();
    
    public List<Integer> truelist;
    public List<Integer> falselist;
    public List<Integer> nextlist;

    public Attribute(String name) {
        super();
        this.name = name;
//        System.err.println("Add Attribute : "+name);
    }
    
    public Attribute(String name, String type, String value) {
        this.name = name;
        attrs.put(type, value);
//        System.err.println("Add Attribute : "+name+" , "+type+" , "+value);
    }
    
    public String getAttr(String key) {
        return attrs.get(key);
    }
    
    public int getIntAttr(String key) {
        return Integer.valueOf(getAttr(key));
    }
    
    public void putAttr(String key, String value) {
        attrs.put(key, value);
    }
    
    public void putAttr(String key, int value) {
        attrs.put(key, ""+value);
    }

    @Override
    public String toString() {
        return "Attribute [name=" + name + ", attrs=" + attrs.toString() + "]";
    }

}
