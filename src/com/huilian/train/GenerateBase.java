package com.huilian.train;

import com.huilian.Utils;
import com.huilian.hanzi2pinyin.PinyinException;
import com.huilian.hanzi2pinyin.PinyinFormat;
import com.huilian.hanzi2pinyin.PinyinHelper;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GenerateBase {
    private static final String HANZIPINYIN_FILE = "./res/input/hanzipinyin.txt";
    private static final String WORD_FILE = "./res/input/word.txt";
    private static final String SENTENCES_FILE = "./res/output/sentences.txt";

    private static final String BASE_START = "./res/output/base_start.txt";
    private static final String BASE_EMISSION = "./res/output/base_emission.txt";
    private static final String BASE_TRANSITION = "./res/output/base_transition.txt";

    // {"你" : 2, "号" : 1}
    private static HashMap<String, Double> start = new HashMap<String, Double>();
    // {"泥" : {"ni" : 1.0}}, }
    private static HashMap<String, HashMap<String, Double>> emission = new HashMap<String, HashMap<String, Double>>();
    // {"你": {"好" : 10, "们" : 2}}
    private static HashMap<String, HashMap<String, Double>> transition = new HashMap<String, HashMap<String, Double>>();

    private static String[] hanzi2pinyin(String hanzi) {
        try {
            String pinyins = PinyinHelper.convertToPinyinString(hanzi, ",", PinyinFormat.WITHOUT_TONE);
            String[] pinyinList = pinyins.split(",");
            for (int i = 0; i < pinyinList.length; i++) {
                if (pinyinList[i].equals("〇")) {
                    pinyinList[i] = "ling";
                } else {
                    pinyinList[i] = Utils.simplifyPinyin(pinyinList[i]);
                }
            }
            return pinyinList;
        } catch (PinyinException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static void processHanziPinyin() {
        Utils.println("read from hanzipinyin.txt");
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(HANZIPINYIN_FILE));
            String line = null;
            String[] lineSplitResult = null;
            String hanzi = null;
            String pinyinList = null;
            String[] pinyinListSplit = null;

            while ((line = buf.readLine()) != null) {
                line = line.trim();
                if ((line.indexOf("=")) == -1) {
                    continue;
                }
                lineSplitResult = line.split("=");
                hanzi = lineSplitResult[0];
                pinyinList = lineSplitResult[1];
                pinyinListSplit = pinyinList.split(",");
                for (int i = 0; i < pinyinListSplit.length; i++) {
                    pinyinListSplit[i] = Utils.simplifyPinyin(pinyinListSplit[i].trim());
                }
                for (int i = 0; i < pinyinListSplit.length; i++) {
                    if (!emission.containsKey(hanzi)) {
                        emission.put(hanzi, new HashMap<String, Double>());
                    }
                    if (!emission.get(hanzi).containsKey(pinyinListSplit[i])) {
                        emission.get(hanzi).put(pinyinListSplit[i], (double) 0);
                    }
                    double value = emission.get(hanzi).get(pinyinListSplit[i]);
                    emission.get(hanzi).put(pinyinListSplit[i], value + 1);
                }
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

    private static void readFromSentenceTxt() {
        Utils.println("read from sentence.txt");
        BufferedReader buf = null;
        try {
            buf = new BufferedReader(new FileReader(SENTENCES_FILE));
            String line = null;
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                if (line.length() < 2) {
                    continue;
                }
                if (!Utils.isChinese(line)) {
                    continue;
                }
                if (!start.containsKey(line.charAt(0) + "")) {
                    start.put(line.charAt(0) + "", 0.0);
                }
                double startValue = start.get(line.charAt(0) + "");
                start.put(line.charAt(0) + "", startValue + 1);

                String[] pinyinList = hanzi2pinyin(line);
                String[] hanziList = new String[line.length()];

                for (int i = 0; i < line.length(); i++) {
                    hanziList[i] = line.charAt(i) + "";
                }

                for (int i = 0; i < line.length(); i++) {
                    if (!emission.containsKey(hanziList[i])) {
                        emission.put(hanziList[i], new HashMap<String, Double>());
                    }
                    if (!emission.get(hanziList[i]).containsKey(pinyinList[i])) {
                        emission.get(hanziList[i]).put(pinyinList[i], (double) 0);
                    }
                    double value = emission.get(hanziList[i]).get(pinyinList[i]);
                    emission.get(hanziList[i]).put(pinyinList[i], value + 1);
                }

                String[] linePassTop = new String[line.length() - 1];
                String[] linePassButton = new String[line.length() - 1];
                for (int i = 0; i < line.length() - 1; i++) {
                    linePassButton[i] = hanziList[i];
                }
                for (int i = 1; i < line.length(); i++) {
                    linePassTop[i - 1] = hanziList[i];
                }

                for (int i = 0; i < linePassTop.length; i++) {
                    if (!transition.containsKey(linePassButton[i])) {
                        transition.put(linePassButton[i], new HashMap<String, Double>());
                    }
                    if (!transition.get(linePassButton[i]).containsKey(linePassTop[i])) {
                        transition.get(linePassButton[i]).put(linePassTop[i], (double) 0);
                    }
                    double value = transition.get(linePassButton[i]).get(linePassTop[i]);
                    transition.get(linePassButton[i]).put(linePassTop[i], value + 1);
                }
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

    private static void readFromWordTxt() {
        Utils.println("read from word.txt");
        int base = 1000;
        int minValue = 2;
        BufferedReader buf = null;
        String[] lineSplitResult = null;
        String word = null;
        String num = null;
        Double numDouble = 0.0;

        try {
            buf = new BufferedReader(new FileReader(WORD_FILE));
            String line = null;
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                if ((line.indexOf("=")) == -1) {
                    continue;
                }
                if (line.length() < 3) {
                    continue;
                }
                lineSplitResult = line.split("=");
                if (lineSplitResult.length != 2) {
                    continue;
                }
                word = lineSplitResult[0].trim();
                num = lineSplitResult[1].trim();
                if (num.length() == 0) {
                    continue;
                }
                numDouble = Double.valueOf(num);
                numDouble = Math.max(numDouble / base, minValue);

                if (!Utils.isChinese(word)) {
                    continue;
                }

                if (!start.containsKey(word.charAt(0) + "")) {
                    start.put(word.charAt(0) + "", 0.0);
                }
                double startValue = start.get(word.charAt(0) + "");
                start.put(word.charAt(0) + "", startValue + numDouble);

                String[] pinyinList = hanzi2pinyin(word);
                String[] hanziList = new String[word.length()];
                for (int i = 0; i < word.length(); i++) {
                    hanziList[i] = word.charAt(i) + "";
                }

                for (int i = 0; i < word.length(); i++) {
                    if (!emission.containsKey(hanziList[i])) {
                        emission.put(hanziList[i], new HashMap<String, Double>());
                    }
                    if (!emission.get(hanziList[i]).containsKey(pinyinList[i])) {
                        emission.get(hanziList[i]).put(pinyinList[i], (double) 0);
                    }
                    double value = emission.get(hanziList[i]).get(pinyinList[i]);
                    emission.get(hanziList[i]).put(pinyinList[i], value + numDouble);
                }

                String[] linePassTop = new String[word.length() - 1];
                String[] linePassButton = new String[word.length() - 1];
                for (int i = 0; i < word.length() - 1; i++) {
                    linePassButton[i] = hanziList[i];
                }
                for (int i = 1; i < word.length(); i++) {
                    linePassTop[i - 1] = hanziList[i];
                }

                for (int i = 0; i < linePassTop.length; i++) {
                    if (!transition.containsKey(linePassButton[i])) {
                        transition.put(linePassButton[i], new HashMap<String, Double>());
                    }
                    if (!transition.get(linePassButton[i]).containsKey(linePassTop[i])) {
                        transition.get(linePassButton[i]).put(linePassTop[i], (double) 0);
                    }
                    double value = transition.get(linePassButton[i]).get(linePassTop[i]);
                    transition.get(linePassButton[i]).put(linePassTop[i], value + numDouble);
                }
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

    private static void writeStartToFile () {
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, Double> entry : start.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        String str = new String(builder);
        Utils.writeStringToFile(str, BASE_START);
    }

    private static void writeEmissionToFile() {
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, HashMap<String, Double>> entry : emission.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            for (Map.Entry<String, Double> entry1 : entry.getValue().entrySet()) {
                builder.append(entry1.getKey());
                builder.append(":");
                builder.append(entry1.getValue());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        String str = new String(builder);
        Utils.writeStringToFile(str, BASE_EMISSION);
    }

    private static void writeTrasitionToFile() {
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, HashMap<String, Double>> entry : transition.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            for (Map.Entry<String, Double> entry1 : entry.getValue().entrySet()) {
                builder.append(entry1.getKey());
                builder.append(":");
                builder.append(entry1.getValue());
                builder.append(",");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        String str = new String(builder);
        Utils.writeStringToFile(str, BASE_TRANSITION);
    }

    public static void main(String[] args) {
        processHanziPinyin();
        readFromSentenceTxt();
        readFromWordTxt();
        writeStartToFile();
        writeEmissionToFile();
        writeTrasitionToFile();
//        for (Map.Entry<String, HashMap<String, Double>> entry : transition.entrySet()) {
//            Utils.println(entry.getKey());
//            for (Map.Entry<String, Double> entry1 : entry.getValue().entrySet()) {
//                Utils.println(entry1.getKey() + "  " + entry1.getValue() + "  ");
//            }
//        }
    }
}
