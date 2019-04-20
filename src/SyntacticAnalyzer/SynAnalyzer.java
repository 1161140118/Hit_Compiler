/**
 * 
 */
package SyntacticAnalyzer;

import java.util.List;
import java.util.Map;
import java.util.Stack;
import LexicalAnalyzer.LexAnalyzer;
import LexicalAnalyzer.Token;

/**
 * @author standingby
 *
 */
public class SynAnalyzer {
    /** �ʷ��������� */
    private static Map<Integer, String> tokenTable;
    private static List<Token> tokens;
    private static List<String> idList;
    private int tokenIndex = 0;

    private Stack<Integer> stateStack;
    private Stack<String> symbolStack;


    public SynAnalyzer() {
    }
    
    public SynAnalyzer(String sourceCode,String grammarPath,String lexicaloutput) {
        initTable(grammarPath);
        LRTable.output(lexicaloutput+"/table");
        LexAnalyzer.startLexicalAnalyzer(sourceCode, lexicaloutput);
        initLexicalMessage();
        initStack();
        processer();
    }

    private void processer() {
        System.out.println("\n Syntax Analysis Begin.");
        int curState = stateStack.peek();
        String token = getToken();
        while (true) {
            curState = stateStack.peek();
            Action action = LRTable.table.get(curState).get(token); // ���������ַ��ж϶���
            switch (action.type) {
                case LRTable.Shift:
                    // ������ջ
                    symbolStack.push(token);
                    // ָ��ǰ��
                    token = getToken();
                    stateStack.push(action.target);
                    System.out.println(curState + " : " + action.toString());
                    break;

                case LRTable.Red:
                    // pop
                    for (int i = 0; i < action.production.right.size(); i++) {
                        symbolStack.pop();
                        stateStack.pop();
                    }
                    symbolStack.push(action.production.left);
                    System.out.println(curState + " : " + action.toString());
                    
                    if (symbolStack.peek().equals(GrammarParser.startSymbol)) {
                        // ��Լ����ʼ����
                        System.out.println("SynAnalyzer Successfully Complete.");
                        return;
                    }
                    // ����GOTO

                case LRTable.Goto:
                    curState = stateStack.peek();
                    action = LRTable.table.get(curState).get(symbolStack.peek());
                    if (action == null) {
                        // TODO
                    }
                    stateStack.push(action.target);
                    System.out.println(curState + " : " + action.toString());

                    break; 
                default:
                    break;
            }
        }
    }

    private void initStack() {
        stateStack = new Stack<>();
        symbolStack = new Stack<>();
        stateStack.push(0);
        symbolStack.push("#");
    }

    /**
     * �����﷨������
     * @param grammarPath
     */
    private void initTable(String grammarPath) {
        GrammarParser.parseGrammar(grammarPath);
        System.out.println("Grammar Parse Complete.");
        ItemSet.startGenerateClosure(GrammarParser.START);
        System.out.println("Construct Table Complete.");
    }

    private void initLexicalMessage() {
        tokenTable = Token.getTokenTable();
        tokens = Token.getTokens();
        idList = Token.getIdList();
        if (tokenTable == null || tokens == null || idList ==null) {
            System.err.println("Failed to init lexical message!");
            System.exit(-1);
        }
    }

    public String getToken() {
        if (tokenIndex == tokens.size()) {
            return "#";
        }
        int id = tokens.get(tokenIndex++).getClassid();
        return tokenTable.get(id);
    }


}
