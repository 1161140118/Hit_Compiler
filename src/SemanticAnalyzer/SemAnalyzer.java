/**
 * 
 */
package SemanticAnalyzer;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import LexicalAnalyzer.Token;
import SyntacticAnalyzer.Production;

/**
 * @author standingby
 *
 */
public class SemAnalyzer {
    private static Stack<Attribute> semStack = new Stack<>();
    private static Stack<SymbolTable> stableStack = new Stack<>();
    private int tempIndex = 0;
    private SymbolTable curTable;

    /**
     * 
     */
    public SemAnalyzer(SymbolTable symbolTable) {
        curTable = symbolTable;
        stableStack.push(curTable);
    }

    public void addShift(Token token) {
        if (token.classid > 5) {
            semStack.push(new Attribute(token.getStrValue()));
            return;
        }
        if (token.classid == 1) {
            Attribute id = new Attribute("id");
            id.putAttr("id", token.getID());
            id.putAttr("classid", token.classid);
            id.putAttr("line", token.line);
            id.putAttr("addr", token.getID());
            semStack.push(id);
            // 规约时 查添符号表
            return;
        }
        switch (token.classid) {
            case 2:
                semStack.push(new Attribute("int", "value", "" + token.getIntValue()));
                break;

            case 3:
                semStack.push(new Attribute("float", "value", "" + token.getDouValue()));
                break;

            case 4:
                semStack.push(new Attribute("bool", "value", "" + token.getStrValue()));
                break;

            case 5:
                semStack.push(new Attribute("str", "value", "" + token.getStrValue()));
                break;

            default:
                break;
        }
    }

    public void addReduce(Production production) {
        if (production.semAction == null) {
            return;
        }

        switch (production.semAction) {

            case "declare":
                // 声明变量
                declare();
                break;

            case "declareFunc":
                // 声明函数
                declareFunc(production);
                break;

            case "typedefine":
                // 类型定义
                typedefine(production);
                break;

            case "assign":
                // 赋值语句
                assign(production);
                break;

            case "expression":
                // 表达式运算
                expression(production);
                break;

            case "bool":
                // 布尔表达式运算
                boolExp(production);
                break;

            case "boolvalue":
                // 布尔赋值
                boolvalue();
                break;

            case "boolmark":
                boolmark();
                break;

            case "relop":
                // 关系运算符
                relop(production);
                break;

            case "relation":
                // 关系运算
                relation(production);
                break;

            case "nextlist":
                nextlist();
                break;

            case "sNextlist":
                sNextlist(production.semAttr);
                break;

            case "if":
                ifStatement();
                break;

            case "ifelse":
                ifelseStatement();
                break;

            case "while":
                whileStatement();
                break;

            case "call":
                // 函数调用

                break;

            case "mktable":
                // 符号表
                mktable();
                break;

            default:
                // 无语义，正常规约
                for (int i = 0; i < production.right.size(); i++) {
                    semStack.pop();
                }
                semStack.push(new Attribute(production.left));
                return;
        }

    }

    /*********************
     *      声明语句
     *********************/

    private void declare() {
        semStack.pop(); // pop ';'
        Attribute id = semStack.pop();
        Attribute type = semStack.pop();
        if (curTable.Table.containsKey(id.getAttr("id"))) {
            // 重复声明
            System.err.println(
                    "Error at Line[" + id.getAttr("line") + "]: 重复声明变量 " + id.getAttr("id") + " .");
            semStack.push(new Attribute("D"));
            return;
        }
        curTable.addSymbol(id.getAttr("id"), id.getAttr("classid"), type.getAttr("type"),
                type.getAttr("width"));
        semStack.push(new Attribute("D"));
        // TODO D 需要添加属性？
    }

    /**
     * 结束函数声明，跳转外部表
     * @param production
     */
    private void declareFunc(Production production) {
        while (!semStack.pop().name.equals("func")) {
            // 弹出栈
        }
        semStack.push(new Attribute(production.left));
        stableStack.pop();
        curTable = stableStack.peek();
    }

    /**
     * 记录函数声明，创建新表，切换到新表
     */
    private void mktable() {
        Attribute id = semStack.get(semStack.size() - 1); // 取 id
        if (curTable.Table.containsKey(id.getAttr("id"))) {
            // 重复声明
            System.err.println(
                    "Error at Line[" + id.getAttr("line") + "]: 重复声明函数 " + id.getAttr("id") + " .");
        }
        semStack.push(new Attribute("MT"));
        Symbol idSymbol =
                curTable.addSymbol(id.getAttr("id"), id.getAttr("classid"), "func", 1 + "");
        stableStack.push(idSymbol.mktable(curTable, id.getAttr("id")));
        curTable = stableStack.peek();
    }

    private void typedefine(Production production) {
        int width = Integer.valueOf(production.semAttr);
        semStack.peek().putAttr("width", width);
        semStack.peek().putAttr("type", semStack.peek().name);
        semStack.peek().name = "Type";
    }



    /*************************
     *      控制语句
     *************************/

    /**
     * 
     */
    private void ifStatement() {
        /**
         * S -> if B then BM { Sens }
         */
        semStack.pop(); // pop '}'
        Attribute sens = semStack.pop();
        semStack.pop(); // pop '{'
        Attribute bm = semStack.pop();
        semStack.pop(); // pop 'then'
        Attribute b = semStack.pop();
        semStack.pop(); // pop 'if'

        backpatch(b.truelist, bm.getIntAttr("quad"));
        backpatch(b.falselist, nextquad());
        Attribute s = new Attribute("S");
        s.nextlist = merge(b.falselist, sens.nextlist);
        semStack.push(s);
    }

    /**
     * 
     */
    private void ifelseStatement() {
        /**
         * S -> if B then BM { Sens } N else BM { Sens }
         */
        semStack.pop(); // }
        Attribute sens2 = semStack.pop();
        semStack.pop(); // {
        Attribute bm2 = semStack.pop();
        semStack.pop(); // pop 'else'
        Attribute n = semStack.pop();
        semStack.pop(); // }
        Attribute sens1 = semStack.pop();
        semStack.pop(); // {
        Attribute bm1 = semStack.pop();
        semStack.pop(); // then
        Attribute b = semStack.pop();
        semStack.pop(); // pop 'if'

        backpatch(b.truelist, bm1.getIntAttr("quad"));
        backpatch(b.falselist, bm2.getIntAttr("quad"));

        Attribute s = new Attribute("S");
        s.nextlist = merge(sens1.nextlist, merge(n.nextlist, sens2.nextlist));
        semStack.push(s);
    }

    /**
     * 
     */
    private void whileStatement() {
        /**
         * S -> while BM B do BM { Sens }  
         */
        semStack.pop(); // '}'
        Attribute sens = semStack.pop();
        semStack.pop(); // '{'
        Attribute bm2 = semStack.pop();
        semStack.pop(); // pop 'do'
        Attribute b = semStack.pop();
        Attribute bm1 = semStack.pop();
        semStack.pop(); // pop 'while'
        backpatch(sens.nextlist, bm1.getIntAttr("quad"));
        backpatch(b.truelist, bm2.getIntAttr("quad"));

        Attribute s = new Attribute("S");
        s.nextlist = b.falselist;
        gencode("j", bm1.getIntAttr("quad"));
        semStack.push(s);
        backpatch(b.falselist, nextquad());
    }


    /*************************
     *      布尔表达式
     *************************/


    private void boolvalue() {
        Attribute value = semStack.peek();
        value.name = "B";
        if (value.getAttr("value").equals("true")) {
            value.truelist = makelist(nextquad());
            gencode("j", -1);
        } else {
            value.falselist = makelist(nextquad());
            gencode("j", -1);
        }
    }

    /**
     * BM -> $
     * BM.quad = nextquad
     * BM.truelist = B.truelist
     * BM.falselist = B.falselist
     */
    private void boolmark() {
        Attribute bm = new Attribute("BM");
        bm.putAttr("quad", nextquad());
        semStack.push(bm);
    }

    /**
     * N -> $   \nextlist
     */
    private void nextlist() {
        Attribute next = new Attribute("N");
        next.nextlist = makelist(nextquad());
        gencode("j", -1);
        semStack.push(next);
    }



    /**
     * bool :
     * 布尔运算
     */
    private void boolExp(Production production) {
        /**
         * b  b1 op bm b2
         * B -> B || BM B     @! @&&  \bool@||
         * B -> B && BM B     @!      \bool@&&
         * B -> ! B                \bool@!
         * B -> ( B ) 
         */
        Attribute b2 = semStack.pop();
        Attribute bm = semStack.pop();

        if (production.semAttr.equals("!")) {
            Attribute b = new Attribute("B");
            b.truelist = b2.falselist;
            b.falselist = b2.truelist;
            semStack.push(b);
            return;
        }

        semStack.pop(); // pop op
        if (production.semAttr.equals("combine")) {
            semStack.push(bm);
            return;
        }

        Attribute b1 = semStack.pop();
        Attribute b = new Attribute("B");
        if (production.semAttr.equals("||")) {
            /**
             * backpatch(B1.falselist,M.quad)
             * B.truelist = merge(B1.truelist,B2.truelist)
             * B.falselist = B2.falselist
             */
            // backpatch
            backpatch(b1.falselist, bm.getIntAttr("quad"));
            // merge
            b.truelist = merge(b1.truelist, b2.truelist);
            b.falselist = copylist(b2.falselist);
        } else {
            /**
             * bachpatch(B1.truelist,M.quad)
             * B.truelist = B2.truelist
             * B.falselist = merge(B1.falselist,B2.falselist)
             */
            // backpatch
            backpatch(b1.truelist, bm.getIntAttr("quad"));
            b.truelist = copylist(b2.truelist);
            // merge
            b.falselist = merge(b1.falselist, b2.falselist);
        }
    }


    private void relation(Production production) {
        Attribute exp1 = semStack.pop();
        Attribute relop = semStack.pop();
        Attribute exp2 = semStack.pop();
        Attribute b = new Attribute("B");
        b.truelist = makelist(nextquad());
        b.falselist = makelist(nextquad() + 1);

        // j,e1,e2,addr
        gencode("j" + relop.getAttr("op"), exp2.getAttr("addr"), exp1.getAttr("addr"), -1);
        // goto
        gencode("j", -1);
        semStack.push(b);
    }

    private void relop(Production production) {
        semStack.peek().putAttr("op", semStack.peek().name);
        semStack.peek().name = "relop";
    }


    /***********************
     *      赋值语句
     ***********************/

    private void assign(Production production) {
        semStack.pop(); // pop ';'
        Attribute E = semStack.pop();
        semStack.pop(); // pop '='
        Attribute id = semStack.pop();
        // 变量声明检查
        if (!curTable.hasDefine(id.getAttr("id"))) {
            System.err.println(
                    "Error at Line[" + id.getAttr("line") + "]: 变量未声明 " + id.getAttr("id") + " .");
        }
        semStack.push(new Attribute("S"));
        gencode("=", E.getAttr("addr"), "-", id.getAttr("id"));
        // 记录 nextlist
        semStack.peek().nextlist = makelist(nextquad());
    }


    private void sNextlist(String attr) {
        Attribute s = semStack.pop();

        if (attr.equals("1")) {
            s.name = "Sens";
            semStack.push(s);
            return;
        }

        Attribute bm = semStack.pop();
        Attribute sens = semStack.pop();
        Attribute Sens = new Attribute("Sens");

        // backpatch
        backpatch(sens.nextlist, bm.getIntAttr("quad"));
        Sens.nextlist = s.nextlist;
        semStack.push(Sens);
    }

    private void expression(Production production) {
        Attribute tmp1 = semStack.pop();

        /**
         * E -> ID
         */
        if (production.semAttr.equals("id")) {
            tmp1.name = "E";
            semStack.push(tmp1);
            // 变量声明检查
            if (!curTable.hasDefine(tmp1.getAttr("id"))) {
                System.err.println("Error at Line[" + tmp1.getAttr("line") + "]: 变量未声明 "
                        + tmp1.getAttr("id") + " .");
            }
            return;
        }

        /**
         * E -> Const
         */
        if (production.semAttr.equals("const")) {
            Attribute tmp = new Attribute("E");
            tmp.putAttr("type", tmp1.name);
            tmp.putAttr("addr", tmp1.getAttr("value"));
            semStack.push(tmp);
            return;
        }
        Attribute tmp2 = semStack.pop();
        Attribute tmp3 = semStack.pop();

        /**
         * E -> ( E )
         */
        if (production.semAttr.equals("combine")) {
            semStack.push(tmp2);
            return;
        }

        // TODO 类型检查x
//        String type1 = tmp1.getAttr("type");
//        String type3 = tmp3.getAttr("type");
//        
//        if (!type1.equals(type3)) {
//            System.err.println("Error at Line[" + tmp1.getAttr("line") + "]: 变量未声明 "
//                    + tmp1.getAttr("id") + " .");            
//        }
        

        /**
         * E -> E op E 
         */

        Attribute tmp = new Attribute("E", "addr", newTemp());
        gencode(production.semAttr, tmp3.getAttr("addr"), tmp1.getAttr("addr"),
                tmp.getAttr("addr"));
        semStack.push(tmp);

    }

    /*******************************
     *      辅助方法
     *******************************/

    private void gencode(String op, String arg1, String arg2, String result) {
        Tuple.addTuple(new Tuple(op, arg1, arg2, result));
    }

    private void gencode(String op, String arg1, String arg2, int result) {
        Tuple.addTuple(new Tuple(op, arg1, arg2, result));
    }

    private void gencode(String op, int result) {
        Tuple.addTuple(new Tuple(op, result));
    }

    private List<Integer> copylist(List<Integer> list) {
        List<Integer> newlist = new LinkedList<>();
        newlist.addAll(list);
        return newlist;
    }

    private List<Integer> makelist(int quad) {
        List<Integer> newlist = new LinkedList<>();
        newlist.add(quad);
        return newlist;
    }

    private List<Integer> merge(List<Integer> list1, List<Integer> list2) {
        List<Integer> newlist = new LinkedList<>();
        newlist.addAll(list1);
        newlist.addAll(list2);
        return newlist;
    }

    private void backpatch(List<Integer> list, int quad) {
        for (Integer integer : list) {
            Tuple.patchResult(integer, quad);
        }
    }
    

    /**
     * 获得下一个四元组地址
     * @return
     */
    private int nextquad() {
        return Tuple.Address;
    }

    private String newTemp() {
        return "@" + tempIndex++;
    }

}
