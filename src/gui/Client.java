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
import SemanticAnalyzer.Tuple;
import SyntacticAnalyzer.SynAnalyzer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * @author standingby
 *
 */
public class Client extends Application {
    private static final ObservableList<TupleData> tupleData = FXCollections.observableArrayList();
    private static final ObservableList<AlertData> alertData = FXCollections.observableArrayList();

    /*
     * (non-Javadoc)
     * 
     * @see javafx.application.Application#start(javafx.stage.Stage)
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        Button buttonRun = new Button("进行分析");

        /** 代码编辑区 */
        CodeArea codeArea = new CodeArea();
        codeArea.setStyle("-fx-font-family:consolas;" + "-fx-font-size:16;");
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        VirtualizedScrollPane<CodeArea> codePane = new VirtualizedScrollPane<>(codeArea);
        codePane.setMinWidth(400);


        /** 输出表 */
        TableView<TupleData> tupleTable = new TableView<>(tupleData);
        tupleTable.setEditable(true);
        TableColumn<TupleData, Number> tupleAddr = new TableColumn<>("地址");
        TableColumn<TupleData, String> tuple4Column = new TableColumn<>("四元组");
        TableColumn<TupleData, String> tuple3Column = new TableColumn<>("三地址码");

        tupleAddr.setMinWidth(20);
        tupleAddr.setCellValueFactory(new PropertyValueFactory<>("addr"));

        tuple4Column.setMinWidth(100);
        tuple4Column.setCellValueFactory(new PropertyValueFactory<>("tuple4"));

        tuple3Column.setMinWidth(100);
        tuple3Column.setCellValueFactory(new PropertyValueFactory<>("tuple3"));

        tupleTable.setItems(tupleData);
        tupleTable.getColumns().addAll(tupleAddr, tuple4Column, tuple3Column);

        /** 错误处理 */
        TableView<AlertData> alertTable = new TableView<>(alertData);
        alertTable.setEditable(true);
        TableColumn<AlertData, Number> alertAddr = new TableColumn<>("行号");
        alertAddr.setMinWidth(20);
        alertAddr.setCellValueFactory(new PropertyValueFactory<>("addr"));
        TableColumn<AlertData, String> alertMsg = new TableColumn<>("错误信息");
        alertMsg.setMinWidth(150);
        alertMsg.setCellValueFactory(new PropertyValueFactory<>("msg"));
        alertTable.getColumns().addAll(alertAddr,alertMsg);

        /*******************
         *      布局
         *******************/
        HBox hBox = new HBox();
        hBox.getChildren().addAll(codePane, tupleTable, alertTable);

        BorderPane mainPane = new BorderPane();
        mainPane.setTop(buttonRun);
        mainPane.setCenter(hBox);

        Scene scene = new Scene(mainPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Standingby's Analyzer");
        primaryStage.show();

        buttonRun.setOnAction(event -> {
            String code = codeArea.getText();
            List<String> input = new ArrayList<>(Arrays.asList(code.split("\n")));
            new SynAnalyzer(input);
        });

    }

    public static void addTuple(int i, Tuple tuple) {
        tupleData.add(new TupleData(i, tuple.toTuple4(), tuple.toTuple3()));
    }

    public static class TupleData {
        private final int addr;
        private final String tuple4;
        private final String tuple3;

        public TupleData(int addr, String tuple4, String tuple3) {
            super();
            this.addr = addr;
            this.tuple4 = tuple4;
            this.tuple3 = tuple3;
        }

        public int getAddr() {
            return addr;
        }

        public String getTuple4() {
            return tuple4;
        }

        public String getTuple3() {
            return tuple3;
        }
    }

    public static void addAlert(int i, String string) {
        alertData.add(new AlertData(i, string));
    }

    public static class AlertData {
        private final int addr;
        private final String msg;

        public AlertData(int addr, String msg) {
            super();
            this.addr = addr;
            this.msg = msg;
        }

        public int getAddr() {
            return addr;
        }

        public String getMsg() {
            return msg;
        }

    }



    /**
     * @param args
     */
    public static void main(String[] args) {
        launch();
    }

}
