/**
 * 
 */
package graphviz;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import SyntacticAnalyzer.Production;

/**
 * @author standingby
 *
 */
public class DrawTree {
    private GraphViz graph;
    private Map<String, Stack<Integer>> nonTerminalsIndexs = new HashMap<>();
    private Map<String, Stack<Integer>> terminalIndexs = new HashMap<>();
    private int index = 0;

    /**
     * 
     */
    public DrawTree(Set<String> terminals, Set<String> nonTerminals) {
        graph = new GraphViz();
        graph.addln(graph.start_graph());
        for (String string : terminals) {
            terminalIndexs.put(string, new Stack<>());
        }
        for (String string : nonTerminals) {
            nonTerminalsIndexs.put(string, new Stack<>());
        }
    }

    public void addTerminals(String string) {
        graph.addln(index + " [label=\"" + string + " (" + index + ")"
                + "\" shape=none fontcolor = blue]");
        terminalIndexs.get(string).push(index);
        index++;
    }

    public void addProduction(Production production) {
        List<String> strings = new LinkedList<>();
        String lift = production.left;
        strings.add(" [label=\"");
        for (String string : production.right) {
            if (!nonTerminalsIndexs.containsKey(string)) {
                // ÖÕ½á·ûÀ¶É«¿ò
                strings.add("-> " + terminalIndexs.get(string).pop());
            } else if (nonTerminalsIndexs.get(string).isEmpty()) {
                strings.add(index + " [label=\"" + string + "\"]");
//                strings.add(index + " [label=\"" + string + " (" + index + ")" + "\"]");
                strings.add("-> " + index);
                index++;
            } else {
                strings.add("-> " + nonTerminalsIndexs.get(string).pop());
            }
        }
        // ¿Õ²úÉúÊ½
        if (production.right.size() == 0) {
            strings.add(index + " [label=\" $ " + "\" shape=none fontcolor = blue]");
//            strings.add(index + " [label=\" $ (" + index + ")" + "\" shape=none fontcolor = blue]");
            strings.add("-> " + index);
            index++;
        }
        // ²¹³ä lift ±àºÅ
        strings.set(0, index + strings.get(0) + lift  + "\"]");
//        strings.set(0, index + strings.get(0) + lift + " (" + index + ")" + "\"]");
        for (int i = 1; i < strings.size(); i++) {
            if (strings.get(i).charAt(0) == '-') {
                strings.set(i, index + strings.get(i));
            }
        }
        nonTerminalsIndexs.get(lift).push(index++);
        // Ìí¼Ó
        for (String string : strings) {
            graph.addln(string);
        }
    }

    public void draw() {
        graph.addln(graph.end_graph());
        graph.writeGraphToFile(graph.getGraph(graph.getDotSource(), "gif"),
                new File("test/graphviz/tree.gif"));
        System.out.println("Draw Parse Tree Complete.");
    }

}
