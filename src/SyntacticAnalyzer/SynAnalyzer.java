/**
 * 
 */
package SyntacticAnalyzer;

/**
 * @author standingby
 *
 */
public class SynAnalyzer {
    
	
	public void initTable(String grammarPath) {
		GrammarParser.parseGrammar(grammarPath);
		System.out.println("Grammar Parse Complete.");
		ItemSet.startGenerateClosure(GrammarParser.START);
		Table.output();
		System.out.println("Construct Table Complete.");
	}
    
    
    

    /**
     * @param args
     */
    public static void main(String[] args) {
//    	new SynAnalyzer().initTable("src/SyntacticAnalyzer/grammar");;
//    	new SynAnalyzer().initTable("src/SyntacticAnalyzer/test1");
    	new SynAnalyzer().initTable("src/SyntacticAnalyzer/test2");
    }

}
