package com.ztc;

import com.ztc.hanzi2pinyin.PinyinException;
import com.ztc.hanzi2pinyin.PinyinFormat;
import com.ztc.hanzi2pinyin.PinyinHelper;

import java.io.*;
import java.util.*;

public class Utils {

    private static String[] withTonePinyin = new String[] {"ā", "á", "ǎ", "à",
                                                "ē", "é", "ě", "è",
                                                "ī", "í", "ǐ", "ì",
                                                "ō", "ó", "ǒ", "ò",
                                                "ū", "ú", "ǔ", "ù",
                                                "ü", "ǖ", "ǘ", "ǚ", "ǜ",
                                                "ń", "ň", ""};

    private static String[] withoutTonePinyin = new String[] {"a", "a", "a", "a",
                                                   "e", "e", "e", "e",
                                                   "i", "i", "i", "i",
                                                   "o", "o", "o", "o",
                                                   "u", "u", "u", "u",
                                                   "v", "v", "v", "v", "v",
                                                   "n", "n", "m"};

    private static String[] shengmu = new String[] {"b","p","m","f","d","t","n","l","g","k","h","j","q","x","zh","ch","sh","r","z","c","s"};

    private static Map<String, String> removeToneMap = null;
    private static Set<String> shengmuSet = null;

    static {
        removeToneMap = new HashMap<String, String>();
        for (int i = 0; i < withoutTonePinyin.length; i++) {
            removeToneMap.put(withTonePinyin[i], withoutTonePinyin[i]);
        }

        shengmuSet = new HashSet<String>();
        for (int i = 0; i < shengmu.length; i++) {
            shengmuSet.add(shengmu[i]);
        }
    }


    public static void readFileByLine(String pathFile) {
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(pathFile));
            String temp = null;
            while ((temp = buf.readLine()) != null) {
                System.out.println(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (buf != null) {
                try {
                    buf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 删除拼音中的音调
     * lǔ -> lu
     */
    public static String removeTone(String onePinyin) {
        String str = "";
        for (int i = 0; i < onePinyin.length(); i++) {
            if (removeToneMap.containsKey(onePinyin.charAt(i) + "")) {
                str += removeToneMap.get(onePinyin.charAt(i) + "");
            } else {
                str += onePinyin.charAt(i);
            }
        }
        return str;
    }

    /**
     * 规范化
     * ue -> ve
     */
    public static String normalizePinyin(String onePinyin) {
        if (onePinyin.indexOf("ue") != -1) {
            return onePinyin.replace("ue", "ve");
        }
        if ("ng" == onePinyin) {
            return "en";
        }
        return onePinyin;
    }

    public static String simplifyPinyin(String onePinyin) {
        return normalizePinyin(removeTone(onePinyin.toLowerCase()));
    }

    public static boolean isShengmu(String v) {
        return shengmuSet.contains(v);
    }

    public static String getShengmu(String onePinyin) {
        if (onePinyin.length() == 0) {
            return null;
        } else if (onePinyin.length() == 1) {
            if (isShengmu(onePinyin)) {
                return onePinyin;
            } else {
                return null;
            }
        } else {
            if (isShengmu(onePinyin.substring(0, 2))) {
                return onePinyin.substring(0, 2);
            } else if (isShengmu(onePinyin.substring(0, 1))) {
                return onePinyin.substring(0, 1);
            } else {
                return null;
            }
        }
    }

    public static String joinLineBreak(String s) {
        StringBuilder builder = new StringBuilder("");
        for (int i = 0; i < s.length() - 1; i++) {
            builder.append(s.charAt(i));
            builder.append("\n");
        }
        builder.append(s.charAt(s.length() - 1));
        return new String(builder);
    }

    public static void print(String s) {
        System.out.print(s);
    }

    public static void println(String s) {
        System.out.println(s);
    }

    public static void printLine() {
        System.out.println("-----------------------------------------------------");
    }

    public static void printStringArray(String[] array) {
        for (String string : array) {
            println(string);
        }
    }

    public static void writeStringToFile(String string, String filePath) {
        File file = new File(filePath);
        BufferedWriter bufferedWriter = null;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(string);
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static ArrayList<String> getDirFileNames(String dirPath) {
        ArrayList<String> arrayList = new ArrayList<String>();

        File directory = new File(dirPath);
        File[] fileArray = directory.listFiles();
        for (File file : fileArray) {
            arrayList.add(dirPath + "/" + file.getName());
        }

        return arrayList;
    }

    /**
     * 一次性读取文件内容
     * @param filePath 文件路径
     * @return 文件内容
     */
    public static String readFileOneTime (String filePath) {
        File file = new File(filePath);
        long fileByteLength = file.length();
        byte[] content = new byte[(int)fileByteLength];
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            fileInputStream.read(content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String str = new String(content);
        return str;
    }

    public static boolean isChinese(char c) {
        return (c >= 0x4e00 && c <= 0x9fff) || c == '〇';
    }

    public static boolean isChinese(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!isChinese(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isEmptyString(String text) {
        if (text == null || text.length() == 0) {
            return true;
        }
        return false;
    }

    public static void readFile(List<String> data, String filePath) {
        if (data == null || isEmptyString(filePath)) {
            return;
        }
        File file = new File(filePath);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = null;
            while ((line = reader.readLine()) != null) {
                if (!isEmptyString(line)) {
                    data.add(line.trim());
                }
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void createDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public static void main(String[] args) {
        String hanzi = "绰绰有余";
        try {
            String pinyins = PinyinHelper.convertToPinyinString(hanzi, ",", PinyinFormat.WITHOUT_TONE);
            print(pinyins);
        } catch (PinyinException e) {
            e.printStackTrace();
        }
    }
}
