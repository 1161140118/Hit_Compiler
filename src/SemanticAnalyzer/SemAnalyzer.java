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
	private int tempIndex = 0;
	private SymbolTable curTable;

	/**
	 * 
	 */
	public SemAnalyzer(SymbolTable symbolTable) {
		curTable = symbolTable;
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
			declare(production);
			break;

		case "declareFunc":
			// 声明函数
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

	private void declare(Production production) {
		semStack.pop(); // pop ';'
		Attribute id = semStack.pop();
		Attribute type = semStack.pop();
		if (curTable.idStrings.contains(id.getAttr("id"))) {
			// 重复声明
			System.err.println("Error at Line[" + id.getAttr("line") + "]: 重复声明 " + id.getAttr("id") + " .");
			semStack.push(new Attribute("D"));
			return;
		}
		curTable.addSymbol(id.getAttr("id"), id.getAttr("classid"), type.getAttr("type"), type.getAttr("width"));
		semStack.push(new Attribute("D"));
		// TODO  D 需要添加属性？
	}

	private void declareFunc(Production production) {
		

	}

	private void typedefine(Production production) {
		int width = Integer.valueOf(production.semAttr);
		semStack.peek().putAttr("width", width);
		semStack.peek().putAttr("type", semStack.peek().getAttr("name"));
		semStack.peek().name = "Type";
	}

	private void assign(Production production) {
		semStack.pop(); // pop ';'
		Attribute E = semStack.pop();
		semStack.pop(); // pop '='
		Attribute id = semStack.pop();
		// 变量声明检查
		if (!curTable.hasDefine(id.getAttr("id"))) {
			System.err.println("Error at Line[" + id.getAttr("line") + "]: 变量未声明 " + id.getAttr("id") + " .");
		}
		semStack.push(new Attribute("S"));
		Tuple.addTuple(new Tuple("=", E.getAttr("addr"), "-", id.getAttr("id")));
	}

	private void relop(Production production) {
		semStack.peek().putAttr("op", semStack.peek().name);
		semStack.peek().name = "relop";
	}

	private void boolExp(Production production) {

	}

	private void expression(Production production) {
		Attribute tmp1 = semStack.pop();
		if (production.semAttr.equals("id")) {
			tmp1.name = "E";
			semStack.push(tmp1);
			// 变量声明检查
			if (!curTable.hasDefine(tmp1.getAttr("id"))) {
				System.err.println("Error at Line[" + tmp1.getAttr("line") + "]: 变量未声明 " + tmp1.getAttr("id") + " .");
			}
			return;
		}
		if (production.semAttr.equals("const")) {
			Attribute tmp = new Attribute("E");
			tmp.putAttr("type", tmp1.name);
			tmp.putAttr("addr", tmp1.getAttr("value"));
			semStack.push(tmp);
			return;
		}
		Attribute tmp2 = semStack.pop();
		Attribute tmp3 = semStack.pop();
		if (production.semAttr.equals("combine")) {
			semStack.push(tmp2);
			return;
		}
		String type = tmp1.getAttr("type");
		// TODO 类型检查

		Attribute tmp = new Attribute("E", "addr", newTemp());
		Tuple.addTuple(new Tuple(production.semAttr, tmp3.getAttr("addr"), tmp1.getAttr("addr"), tmp.getAttr("addr")));
		semStack.push(tmp);

	}

	private String newTemp() {
		return "t" + tempIndex++;
	}

}
