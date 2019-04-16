package SyntacticAnalyzer;

import java.util.ArrayList;
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
    public String toString() {
        return "Production [" + left + " -> " + right + "]";
    }

    public static void main(String[] args) {
		Production production = new Production("iteration_stmt -> for ( isnull_expr ; isnull_expr ; isnull_expr ) block_stmt");
		System.out.println(production.left);
		System.out.println(production.right);
		
	}
	
}
