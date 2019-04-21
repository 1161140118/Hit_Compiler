/**
 * 
 */
package graphviz;

import java.io.File;

/**
 * @author standingby
 *
 */
public class DrawTest {
    
    /**
     * 
     */
    public DrawTest() {
        GraphViz graph = new GraphViz();
        graph.addln(graph.start_graph());
        graph.addln("\"P\"->\"{\" ");
        graph.addln("P->Decs ");
        graph.addln("P->Sens ");
        graph.addln("P->\"}\"");
        graph.addln(graph.end_graph());
        graph.writeGraphToFile(graph.getGraph(graph.getDotSource(), "gif"), new File("test/graphviz/test.gif") );
        System.out.println("Draw Parse Test Complete.");
        
    }
    
    public static void main(String[] args) {
        new DrawTest();
    }

}
