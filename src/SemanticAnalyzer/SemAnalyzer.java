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
            // TODO 查添符号表
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
                // 声明变量，函数
                
                break;
                
            case "typedefine":
                // 类型定义
                
                break;
            
            case "assign":
                // 赋值语句
                
                break;
                
                
            case "expression":
                // 表达式运算
                expression(production);
                
                break;
                
                
            case "bool":
                // 布尔表达式运算
                
                break;
                
                
            case "relop":
                // 关系运算符
                
                break;
                
            case "relation":
                // 关系运算
                
                break;
                
            case "if":
                
                break;
                
            case "ifelse":
                
                break;
                
            case "while":
                
                break;

            case "call":
                // 函数调用
                
                break;
                
            case "mktable":
                // 符号表
                
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
