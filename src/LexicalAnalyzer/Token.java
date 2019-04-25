
package LexicalAnalyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author chen
 *
 */
public class Token {
    /** token table ��¼�Ϸ������ֱ��� */
    private static Map<Integer, String> tokenTable = new HashMap<>();
    /** �������/������ ת���� */
    private static Map<String, Integer> keyTable = new HashMap<>();
    /** ��ʶ�� */
    private static List<String> idList = new LinkedList<>();
    /** ��¼token�� */
    private static List<Token> tokens = new LinkedList<>();

    public final int classid;
    private int intValue = -1;
    private double douValue = -1;
    private String strValue = "";
    public final int line; // ��ӦԴ�����к�


    public Token(int classid, int intValue, int line) {
        super();
        this.classid = classid;
        this.intValue = intValue;
        this.line = line;
    }

    public Token(int classid, double douValue, int line) {
        super();
        this.classid = classid;
        this.douValue = douValue;
        this.line = line;
    }

    public Token(int classid, String strValue, int line) {
        super();
        this.classid = classid;
        this.strValue = strValue;
        this.line = line;
    }

    
    
    public int getIntValue() {
        return intValue;
    }

    public double getDouValue() {
        return douValue;
    }

    public String getStrValue() {
        return strValue;
    }

    @Override
    public String toString() {
        String string = String.valueOf(classid);
        string = "< " + string + " , ";
        if (intValue >= 0) {
            string = string + intValue + " >  ";
        } else if (douValue >= 0) {
            string = string + douValue + " >  ";
        } else {
            string = string + strValue + " >  ";
        }
        return string;
    }

    public static boolean isKeyWord(String word) {
        return keyTable.keySet().contains(word);
    }

    /**
     * ���������
     * @param word
     * @return  -1���ǹؼ���
     */
    public static int getCode(String word) {
        return isKeyWord(word) ? keyTable.get(word) : -1;
    }

    public static void addToken(Token token) {
        tokens.add(token);
    }

    /**
     * ��ӱ�ʶ��
     * @param word ��ʶ��
     * @param line ��ʶ����Դ�����к�
     */
    public static void addIDToken(String word, int line) {
        int value = idList.indexOf(word);
        if (value == -1) {
            value = idList.size();
            idList.add(word);
        }
        addToken(new Token(1, value, line));
    }

    /********************
     *  SynAnalyzer ����
     ********************/

    public int getClassid() {
        return classid;
    }

    public static Map<Integer, String> getTokenTable() {
        return tokenTable;
    }

    public static List<Token> getTokens() {
        return tokens;
    }

    public static List<String> getIdList() {
        return idList;
    }

    /**
     * ��ʼ�� token table
     * @param filepath �ļ���ʽ�� 
     */
    public static void initTable(String filepath) {
        try {
            List<String> table = Files.readAllLines(Paths.get(filepath));
            for (int i = 0; i < table.size(); i++) {
                String[] strings = table.get(i).split(" ");
                if (strings[0].equals("_")) {
                    tokenTable.put(i + 1, strings[1]);
                    // System.out.println(Arrays.toString(strings));
                } else {
                    tokenTable.put(i + 1, strings[0]);
                    keyTable.put(strings[0], i + 1);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to parse file " + filepath);
            e.printStackTrace();
        }
    }

    public static void output(String filepath) {
        try {
            File dir = new File(filepath);
            if (!dir.mkdirs()) {
                if (!dir.isDirectory()) {
                    System.out.println("����Ŀ¼ " + filepath + " ʧ�ܣ�");
                    return;
                }
            }
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filepath + "/token")));
            int line = 1;
            for (Token token : tokens) {
                if (line != token.line) {
                    System.out.println();
                    line = token.line;
                    writer.write("\n");
                }
//                System.out.print(token);
                writer.write(token.toString());
            }
            writer.flush();
            writer = new BufferedWriter(
                    new OutputStreamWriter(new FileOutputStream(filepath + "/id")));
            for (String string : idList) {
                writer.write(string + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
