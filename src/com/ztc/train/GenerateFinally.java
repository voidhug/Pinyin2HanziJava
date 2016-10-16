package com.ztc.train;

import com.ztc.InvalidFormatException;
import com.ztc.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class GenerateFinally {
    private static final String PINYIN2HANZI_FILE = "./res/output/pinyin2hanzi.txt";
    private static final String BASE_START = "./res/output/base_start.txt";
    private static final String BASE_EMISSION = "./res/output/base_emission.txt";
    private static final String BASE_TRANSITION = "./res/output/base_transition.txt";

    private static final String DATA_PATH = "./res/output/data";
    private static final String FINAL_PINYIN2HANZI_FILE = "./res/output/data/hmm_pinyin2hanzi.txt";
    private static final String FINAL_START_FILE = "./res/output/data/hmm_start.txt";
    private static final String FINAL_EMISSION_FILE = "./res/output/data/hmm_emission.txt";
    private static final String FINAL_TRANSITION_FILE = "./res/output/data/hmm_transition.txt";

    private static final Double PINYIN_NUM = 411.;
    private static final Double HANZI_NUM  = 20903.;

    private static Map<String, String> pinyin2hanziMap = new HashMap<String, String>();
    // {"你" : 2, "号" : 1}
    private static HashMap<String, Double> start = new HashMap<String, Double>();
    // {"泥" : {"ni" : 1.0}}, }
    private static HashMap<String, HashMap<String, Double>> emission = new HashMap<String, HashMap<String, Double>>();
    // {"你": {"好" : 10, "们" : 2}}
    private static HashMap<String, HashMap<String, Double>> transition = new HashMap<String, HashMap<String, Double>>();

    private static void generatePinyin2Hanzi() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String pinyin = null;
        String hanziList = null;
        try {
            buf = new BufferedReader(new FileReader(PINYIN2HANZI_FILE));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitResult = line.split("=");
                if (lineSplitResult.length != 2) {
                    try {
                        throw new InvalidFormatException("invalid format");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                pinyin = lineSplitResult[0];
                hanziList = lineSplitResult[1];
                pinyin = pinyin.trim();
                hanziList = hanziList.trim();
                if (pinyin.length() > 0 && hanziList.length() > 0) {
                    pinyin2hanziMap.put(pinyin, hanziList);
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

    private static void generateStart() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String hanzi = null;
        String numString = null;
        Double count = HANZI_NUM;
        Double num = 0.0;

        try {
            buf = new BufferedReader(new FileReader(BASE_START));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitResult = line.split("=");
                hanzi = lineSplitResult[0];
                numString = lineSplitResult[1];
                num = Double.valueOf(numString);
                count += num;
                start.put(hanzi, num);
            }
            for (Map.Entry<String, Double> entry : start.entrySet()) {
                start.put(entry.getKey(), entry.getValue() / count);
            }
            start.put("default", 1 / count);
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

    private static void generateEmission() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String hanzi = null;
        String pinyinNum = null;
        String[] pinyinNumSplitResult = null;
        String[] pinyinNumSplitSingleResult = null;
        HashMap<String, Double> pinyinNumMap = null;

        try {
            buf = new BufferedReader(new FileReader(BASE_EMISSION));
            while ((line = buf.readLine()) != null) {
                Double numSum = 0.0;
                line = line.trim();
                lineSplitResult = line.split("=");
                hanzi = lineSplitResult[0];
                pinyinNum = lineSplitResult[1];
                pinyinNumSplitResult = pinyinNum.split(",");
                pinyinNumMap = new HashMap<String, Double>();
                for (String string : pinyinNumSplitResult) {
                    pinyinNumSplitSingleResult = string.split(":");
                    numSum += Double.valueOf(pinyinNumSplitSingleResult[1]);
                    pinyinNumMap.put(pinyinNumSplitSingleResult[0], Double.valueOf(pinyinNumSplitSingleResult[1]));
                }
                for (Map.Entry<String, Double> entry : pinyinNumMap.entrySet()) {
                    pinyinNumMap.put(entry.getKey(), entry.getValue() / numSum);
                }
                emission.put(hanzi, pinyinNumMap);
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

    private static void generateTransition() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String hanzi1 = null;
        String hanzi2String = null;
        String[] hanzi2StringSplitResult = null;
        String[] hanzi2StringSplitResultSingle = null;
        HashMap<String, Double> hanzi2NumMap = null;

        try {
            buf = new BufferedReader(new FileReader(BASE_TRANSITION));
            while ((line = buf.readLine()) != null) {
                Double numSum = HANZI_NUM;
                line = line.trim();
                lineSplitResult = line.split("=");
                hanzi1 = lineSplitResult[0];
                hanzi2String = lineSplitResult[1];
                hanzi2StringSplitResult = hanzi2String.split(",");
                hanzi2NumMap = new HashMap<String, Double>();
                for (String string : hanzi2StringSplitResult) {
                    hanzi2StringSplitResultSingle = string.split(":");
                    numSum += Double.valueOf(hanzi2StringSplitResultSingle[1]);
                    hanzi2NumMap.put(hanzi2StringSplitResultSingle[0], Double.valueOf(hanzi2StringSplitResultSingle[1]));
                }
                for (Map.Entry<String, Double> entry : hanzi2NumMap.entrySet()) {
                    hanzi2NumMap.put(entry.getKey(), (entry.getValue() + 1) / numSum);
                }
                hanzi2NumMap.put("default", 1 / numSum);
                transition.put(hanzi1, hanzi2NumMap);
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

    private static void writePinyin2HanziMapToFile() {
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, String> entry : pinyin2hanziMap.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        String str = new String(builder);
        Utils.writeStringToFile(str, FINAL_PINYIN2HANZI_FILE);
    }

    public static void writeStartToFile() {
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, Double> entry : start.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            builder.append(entry.getValue());
            builder.append("\n");
        }
        builder.deleteCharAt(builder.length() - 1);
        String str = new String(builder);
        Utils.writeStringToFile(str, FINAL_START_FILE);
    }

    public static void writeEmissionToFile() {
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
        Utils.writeStringToFile(str, FINAL_EMISSION_FILE);
    }

    public static void writeTransitionToFile() {
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
        Utils.writeStringToFile(str, FINAL_TRANSITION_FILE);
    }

    private static void initDir() {
        Utils.createDir(DATA_PATH);
    }

    public static void main(String[] args) {
        initDir();
        generatePinyin2Hanzi();
        generateStart();
        generateEmission();
        generateTransition();
        writePinyin2HanziMapToFile();
        writeStartToFile();
        writeEmissionToFile();
        writeTransitionToFile();
    }
}
