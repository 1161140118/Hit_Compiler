package SyntacticAnalyzer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Production {
	private static final Pattern PATTERN = Pattern.compile("(\\S+)");
	
	public final String left;
	public final List<String> right = new ArrayList<>();
	
	
	public Production(String line) {
		super();
		Matcher matcher = PATTERN.matcher(line);
		if (matcher.find()) {
			this.left = matcher.group(1);
		}else {
			this.left=null;
			System.err.println("Error: Illegal production.");
		}
		if (matcher.find()) {
			if (!matcher.group(1).equals("->")) {
				System.err.println("Error: Illegal production.");
				return;
			}
		}
		while(matcher.find()) {
			right.add(matcher.group(1));
		}
		
	}
	
	
	@Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((left == null) ? 0 : left.hashCode());
        result = prime * result + ((right == null) ? 0 : right.hashCode());
        return result;
    }



    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Production other = (Production) obj;
        if (left == null) {
            if (other.left != null)
                return false;
        } else if (!left.equals(other.left))
            return false;
        if (right == null) {
            if (other.right != null)
                return false;
        } else if (!right.equals(other.right))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return left + " -> " + right;
    }
    
    public static void main(String[] args) {
        int id = 1;
        System.out.println(id++);
        System.out.println(id);
    }
	
}
