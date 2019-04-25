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
    private int tempIndex =0;
    
    
    
    public void addShift(Token token) {
        if (token.classid>5) {
            semStack.push(new Attribute(token.getStrValue()));
            return;
        }
        if (token.classid==1) {
            semStack.push(new Attribute("id","addr",""+token.getIntValue()));
            // TODO ������ű�
            return;
        }
        switch (token.classid) {
            case 2:
                semStack.push(new Attribute("int","value",""+token.getIntValue()));
                break;
                
            case 3:
                semStack.push(new Attribute("float","value",""+token.getDouValue()));
                break;
                
            case 4:
                semStack.push(new Attribute("bool","value",""+token.getStrValue()));
                break;
                
            case 5:
                semStack.push(new Attribute("str","value",""+token.getStrValue()));
                break;

            default:
                break;
        }
    }
    
    public void addReduce(Production production) {
        switch (production.semAction) {
            
            case "declare":
                // ��������������
                
                break;
                
            case "typedefine":
                // ���Ͷ���
                
                break;
            
            case "assign":
                // ��ֵ���
                
                break;
                
                
            case "expression":
                // ���ʽ����
                expression(production);
                
                break;
                
                
            case "bool":
                // �������ʽ����
                
                break;
                
                
            case "relop":
                // ��ϵ�����
                
                break;
                
            case "relation":
                // ��ϵ����
                
                break;
                
            case "if":
                
                break;
                
            case "ifelse":
                
                break;
                
            case "while":
                
                break;

            case "call":
                // ��������
                
                break;
                
            case "mktable":
                // ���ű�
                
                break;
                
            default:
                return;
        }
        
    }
    
    
    private void expression(Production production) {
        Attribute tmp1 = semStack.pop();
        if (production.semAttr.equals("id")) {
            tmp1.name = "E";
            semStack.push(tmp1);
            return;
        }
        if (production.semAttr.equals("const")) {
            Attribute tmp = new Attribute("E");
            tmp.attrs.put("type", tmp1.name );
            tmp.attrs.put("value", tmp1.attrs.get("value"));
            return;
        }
        Attribute tmp2 = semStack.pop();
        Attribute tmp3 = semStack.pop();
        if (production.semAttr.equals("combine")) {
            semStack.push(tmp2);
            return;
        }
        String type = tmp1.attrs.get("type");
        
       
        switch (production.semAttr) {
            case "+":
                
                break;
                
            case "*":
                break;

            default:
                break;
        }
        
    }
    
    private String newTemp() {
        return "t"+tempIndex++;
    }


}
