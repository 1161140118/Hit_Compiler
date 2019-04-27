/**
 * 
 */
package gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import LexicalAnalyzer.LexAnalyzer;
import SemanticAnalyzer.SemAnalyzer;
import SemanticAnalyzer.SymbolTable;
import SemanticAnalyzer.Tuple;
import SyntacticAnalyzer.GrammarParser;
import SyntacticAnalyzer.LRTable;
import SyntacticAnalyzer.SynAnalyzer;
import graphviz.DrawTree;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * @author standingby
 *
 */
public class Client extends Application {



    /*
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button buttonRun = new Button("½øÐÐ·ÖÎö");
        
        CodeArea codeArea = new CodeArea();
        codeArea.setStyle("-fx-font-family:consolas;" + "-fx-font-size:16;");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        VirtualizedScrollPane<CodeArea> codePane = new VirtualizedScrollPane<>(codeArea);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(buttonRun);
        mainPane.setCenter(codePane);
        
        Scene scene = new Scene(mainPane, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Standingby's Analyzer");
        primaryStage.show();
        
        buttonRun.setOnAction(event->{
            String code = codeArea.getText();
            List<String> input = new ArrayList<>(Arrays.asList(code.split("\n")));
            new SynAnalyzer(input);
        });

    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

}
