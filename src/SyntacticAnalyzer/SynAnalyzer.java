/**
 * 
 */
package SyntacticAnalyzer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import LexicalAnalyzer.LexAnalyzer;
import LexicalAnalyzer.Token;
import SemanticAnalyzer.SemAnalyzer;
import SemanticAnalyzer.SymbolTable;
import graphviz.DrawTree;

/**
 * @author standingby
 *
 */
public class SynAnalyzer {
    /** 词法分析输入 */
    private static Map<Integer, String> tokenTable;
    private static List<Token> tokens;
    private static List<String> idList;
    private int tokenIndex = 0;

    private Stack<Integer> stateStack;
    private Stack<String> symbolStack;
    /** 异常处理 */
    private Set<String> recover = new HashSet<>(Arrays.asList("D", "S", "Decs", "Sens", "{"));

    private DrawTree draw;
    private SemAnalyzer semAnalyzer;

    public SynAnalyzer() {}

    public SynAnalyzer(String sourceCode, String grammarPath, String lexicaloutput) {
        initTable(grammarPath);
        LRTable.output(lexicaloutput + "/table");
        LexAnalyzer.startLexicalAnalyzer(sourceCode, lexicaloutput);
        initLexicalMessage();
        initStack();
        draw = new DrawTree(GrammarParser.terminals, GrammarParser.nonTerminals);
        semAnalyzer = new SemAnalyzer(new SymbolTable("Global"));
        processer();
        draw.draw();
    }

    private void processer() {
        System.out.println("\n Syntax Analysis Begin.");
        int curState = stateStack.peek();
        String token = getTokenString();
        Action action;
        while (true) {
            curState = stateStack.peek();
            action = LRTable.table.get(curState).get(token); // 根据输入字符判断动作

            // 错误处理
            while (action == null) {
                int num = getLineNum();
                if (num == -1) {
                    System.err.println("SynAnalyzer Failed!");
                    return;
                }
                System.err.println("Error at Line [" + num + "]: " + "Unexpected symbol '" + token
                        + "'. Expect :" + LRTable.table.get(curState).keySet());
                // 弹出，直到预定义非终结符
                while (!recover.contains(symbolStack.peek())) {
                    symbolStack.pop();
                    stateStack.pop();
                }
                curState = stateStack.peek();
                token = getTokenString();
                while (!(token.equals(";") || token.equals("}"))) {
                    token = getTokenString();
                }
                token = getTokenString();
                action = LRTable.table.get(curState).get(token);
            }

            // 处理
            switch (action.type) {
                case LRTable.Shift:
                    // 符号入栈
                    symbolStack.push(token);
                    stateStack.push(action.target);
                    System.out.println(curState + " : " + action.toString() + ":" + token);
                    draw.addTerminals(token);
                    semAnalyzer.addShift(getCurToken());
                    // 指针前移
                    token = getTokenString();
                    break;

                case LRTable.Red:
                    // pop
                    for (int i = 0; i < action.production.right.size(); i++) {
                        symbolStack.pop();
                        stateStack.pop();
                    }
                    symbolStack.push(action.production.left);
                    System.out.println(curState + " : " + action.toString());
                    draw.addProduction(action.production);
                    semAnalyzer.addReduce(action.production);
                    if (symbolStack.peek().equals(GrammarParser.startSymbol)) {
                        // 规约出开始符号
                        System.out.println("SynAnalysis Successfully Complete.");
                        return;
                    }
                    // 进行GOTO

                case LRTable.Goto:
                    curState = stateStack.peek();
                    action = LRTable.table.get(curState).get(symbolStack.peek());
                    stateStack.push(action.target);
                    // System.out.println(curState + " : " + action.toString());

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
     * 构建语法分析表
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
        if (tokenTable == null || tokens == null || idList == null) {
            System.err.println("Failed to init lexical message!");
            System.exit(-1);
        }
    }

    private String getTokenString() {
        if (tokenIndex == tokens.size()) {
            return "#";
        }
        int id = tokens.get(tokenIndex++).getClassid();
        return tokenTable.get(id);
    }
    
    private Token getCurToken() {
        return tokens.get(tokenIndex-1);
    }

    private int getLineNum() {
        if (tokenIndex == tokens.size()) {
            return -1;
        }
        return tokens.get(tokenIndex).line;
    }


}
