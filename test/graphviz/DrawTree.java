/**
 * 
 */
package graphviz;

import java.io.File;
import SyntacticAnalyzer.Production;

/**
 * @author standingby
 *
 */
public class DrawTree {
    private GraphViz graph;
    
    /**
     * 
     */
    public DrawTree() {
       graph = new GraphViz();
       graph.addln(graph.start_graph());
    }
    
    public void addProduction(Production production) {
        String lift = production.left;
        for (String string : production.right) {
            graph.addln(lift+"->"+string+";");
        }
    }
    
    public void draw() {
        graph.addln(graph.end_graph());
        graph.writeGraphToFile(graph.getGraph(graph.getDotSource(), "gif"), new File("test/graphviz/tree.gif") );
        System.out.println("Draw Parse Tree Complete.");
    }

}
