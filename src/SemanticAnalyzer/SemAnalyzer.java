/**
 * 
 */
package SemanticAnalyzer;

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
            semStack.push(id);
            // ��Լʱ ������ű�
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
                // ��������
                declare();
                break;

            case "declareFunc":
                // ��������
                declareFunc(production);
                break;

            case "typedefine":
                // ���Ͷ���
                typedefine(production);
                break;

            case "assign":
                // ��ֵ���
                assign(production);
                break;

            case "expression":
                // ���ʽ����
                expression(production);
                break;

            case "bool":
                // �������ʽ����
                boolExp(production);
                break;

            case "boolvalue":
                // ������ֵ
                boolvalue();
                break;

            case "boolmark":
                boolmark();
                break;

            case "relop":
                // ��ϵ�����
                relop(production);
                break;

            case "relation":
                // ��ϵ����
                relation(production);
                break;

            case "if":
                ifStatement(production);
                break;

            case "ifelse":
                ifelseStatement(production);
                break;

            case "while":
                whileStatement(production);
                break;

            case "call":
                // ��������

                break;

            case "mktable":
                // ���ű�
                mktable();
                break;

            default:
                // �����壬������Լ
                for (int i = 0; i < production.right.size(); i++) {
                    semStack.pop();
                }
                semStack.push(new Attribute(production.left));
                return;
        }

    }

    /*********************
     *      �������
     *********************/

    private void declare() {
        semStack.pop(); // pop ';'
        Attribute id = semStack.pop();
        Attribute type = semStack.pop();
        if (curTable.idStrings.contains(id.getAttr("id"))) {
            // �ظ�����
            System.err.println(
                    "Error at Line[" + id.getAttr("line") + "]: �ظ��������� " + id.getAttr("id") + " .");
            semStack.push(new Attribute("D"));
            return;
        }
        curTable.addSymbol(id.getAttr("id"), id.getAttr("classid"), type.getAttr("type"),
                type.getAttr("width"));
        semStack.push(new Attribute("D"));
        // TODO D ��Ҫ������ԣ�
    }

    /**
     * ����������������ת�ⲿ��
     * @param production
     */
    private void declareFunc(Production production) {
        while (!semStack.pop().name.equals("func")) {
            // ����ջ
        }
        semStack.push(new Attribute(production.left));
        stableStack.pop();
        curTable = stableStack.peek();
    }

    /**
     * ��¼���������������±��л����±�
     */
    private void mktable() {
        Attribute id = semStack.get(semStack.size() - 1); // ȡ id
        if (curTable.idStrings.contains(id.getAttr("id"))) {
            // �ظ�����
            System.err.println(
                    "Error at Line[" + id.getAttr("line") + "]: �ظ��������� " + id.getAttr("id") + " .");
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
     *      �������
     *************************/

    /**
     * 
     */
    private void ifStatement(Production production) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    private void ifelseStatement(Production production) {
        // TODO Auto-generated method stub

    }

    /**
     * 
     */
    private void whileStatement(Production production) {
        Attribute p = semStack.pop();
        Attribute b = semStack.pop();
        semStack.pop(); // pop 'while'



    }

    private void boolvalue() {
        Attribute value = semStack.peek();
        value.name = "B";
        value.putAttr("addr", newTemp());
        Tuple.addTuple(new Tuple("=", value.getAttr("value"), "-", value.getAttr("addr")));
    }

    /**
     * BM -> $
     * BM.quad = nextquad
     * BM.truelist = B.truelist
     * BM.falselist = B.falselist
     */
    private void boolmark() {
        Attribute b = semStack.get(semStack.size() - 3);
        Attribute bm = new Attribute("BM");
        bm.putAttr("quad", Tuple.Address);
        bm.putAttr("turelist", b.getAttr("truelist"));
        bm.putAttr("falselist", b.getAttr("falselist"));
        semStack.push(bm);
    }

    /**
     * bool :
     * ��������
     */
    private void boolExp(Production production) {
        Attribute b1 = semStack.pop();
        Attribute b2 = semStack.pop();

        if (production.semAttr.equals("!")) {
            Attribute b = new Attribute("B");
            b.putAttr("falselist", b1.getAttr("truelist"));
            b.putAttr("truelist", b1.getAttr("falselist"));
            semStack.push(b);
            return;
        }

        Attribute b3 = semStack.pop();
        if (production.semAttr.equals("combine")) {
            semStack.push(b2);
            return;
        }

        Attribute b4 = semStack.pop();
        Attribute b = new Attribute("B");
        if (production.semAttr.equals("||")) {
            /**
             * backpatch(M.falselist,M.quad)
             * B.truelist = merge(M.truelist,B2.truelist)
             * B.falselist = B2.falselist
             */
            // backpatch
            Tuple.tupleList.get(b2.getIntAttr("falselist")).setResult(b2.getAttr("quad"));
            // merge
            Tuple.tupleList.get(b2.getIntAttr("truelist")).setResult(b1.getAttr("truelist"));
            b.putAttr("truelist", b1.getAttr("truelist"));

            b.putAttr("falselist", b1.getAttr("truelist"));
        } else {
            /**
             * bachpatch(M.truelist,M.quad)
             * B.truelist = B2.truelist
             * B.falselist = merge(M.falselist,B2.falselist)
             */
            // backpatch
            Tuple.tupleList.get(b2.getIntAttr("truelist")).setResult(b2.getAttr("quad"));

            b.putAttr("truelist", b1.getAttr("truelist"));
            // merge
            Tuple.tupleList.get(b2.getIntAttr("falselist")).setResult(b1.getAttr("falselist"));
            b.putAttr("falselist", b1.getAttr("falselist"));
        }

    }


    private void relation(Production production) {
        Attribute exp1 = semStack.pop();
        Attribute relop = semStack.pop();
        Attribute exp2 = semStack.pop();
        // j,e1,e2,addr
        Tuple.addTuple(new Tuple("j" + relop.getAttr("op"), exp2.getAttr("addr"),
                exp1.getAttr("addr"), Tuple.Address + 3));
        // temp = false
        Tuple.addTuple(new Tuple("=", "false", "-", newTemp()));
        // goto
        Tuple.addTuple(new Tuple("j", "-", "-", Tuple.Address + 2));
        // temp = true
        Tuple.addTuple(new Tuple("=", "true", "-", newTemp()));
        semStack.push(new Attribute("B", "addr", "" + (Tuple.Address - 4)));
    }

    private void relop(Production production) {
        semStack.peek().putAttr("op", semStack.peek().name);
        semStack.peek().name = "relop";
    }


    /***********************
     *      ��ֵ���
     ***********************/

    private void assign(Production production) {
        semStack.pop(); // pop ';'
        Attribute E = semStack.pop();
        semStack.pop(); // pop '='
        Attribute id = semStack.pop();
        // �����������
        if (!curTable.hasDefine(id.getAttr("id"))) {
            System.err.println(
                    "Error at Line[" + id.getAttr("line") + "]: ����δ���� " + id.getAttr("id") + " .");
        }
        semStack.push(new Attribute("S"));
        Tuple.addTuple(new Tuple("=", E.getAttr("addr"), "-", id.getAttr("id")));
    }

    private void expression(Production production) {
        Attribute tmp1 = semStack.pop();
        if (production.semAttr.equals("id")) {
            tmp1.name = "E";
            semStack.push(tmp1);
            // �����������
            if (!curTable.hasDefine(tmp1.getAttr("id"))) {
                System.err.println("Error at Line[" + tmp1.getAttr("line") + "]: ����δ���� "
                        + tmp1.getAttr("id") + " .");
            }
            return;
        }
        if (production.semAttr.equals("const")) {
            Attribute tmp = new Attribute("E");
            tmp.putAttr("type", tmp1.name);
            tmp.putAttr("addr", tmp1.getAttr("value"));
            semStack.push(tmp);
            // System.err.println("Const : "+tmp.toString());
            return;
        }
        Attribute tmp2 = semStack.pop();
        Attribute tmp3 = semStack.pop();
        if (production.semAttr.equals("combine")) {
            semStack.push(tmp2);
            return;
        }
        String type = tmp1.getAttr("type");
        // TODO ���ͼ��

        Attribute tmp = new Attribute("E", "addr", newTemp());
        Tuple.addTuple(new Tuple(production.semAttr, tmp3.getAttr("addr"), tmp1.getAttr("addr"),
                tmp.getAttr("addr")));
        semStack.push(tmp);

    }

    private String newTemp() {
        return "@" + tempIndex++;
    }

}
