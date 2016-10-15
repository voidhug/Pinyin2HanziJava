package com.ztc.train;

import com.ztc.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ProcessHanziPinYin {
    private static final String INPUT_PATH = "./res/input";
    private static final String OUTPUT_PATH  = "./res/output";
    private static final String HANZIPINYIN_FILE = "./res/input/hanzipinyin.txt";
    private static final String ALL_STATES_FILE = "./res/output/all_states.txt"; // 汉字（隐藏状态）
    private static final String ALL_OBSERVATIONS_FILE = "./res/output/all_observations.txt"; // 拼音（观测值）
    private static final String PINYIN2HANZI_FILE = "./res/output/pinyin2hanzi.txt";

    private static Set<String> states = new HashSet<String>();
    private static Set<String> observations = new HashSet<String>();
    private static Map<String, HashSet<String>> pinyin2hanzi = new HashMap<String, HashSet<String>>();
    
    private static void generator() {
        BufferedReader buf = null;

        try {
            buf = new BufferedReader(new FileReader(HANZIPINYIN_FILE));
            String line = null;
            String[] lineSplitResult = null;
            String hanzi = null;
            String pinyinList = null;
            String[] pinyinListSplit = null;
            String shengmu = null;

            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitResult = line.split("=");
                hanzi = lineSplitResult[0];
                pinyinList = lineSplitResult[1];
                pinyinListSplit = pinyinList.split(",");
                for (int i = 0; i < pinyinListSplit.length; i++) {
                    pinyinListSplit[i] = Utils.simplifyPinyin(pinyinListSplit[i].trim());
                }
                states.add(hanzi);
                for (int i = 0; i < pinyinListSplit.length; i++) {
                    if (!pinyinListSplit[i].equals("null")) {
                        observations.add(pinyinListSplit[i]);
                    }
                    if (!pinyin2hanzi.containsKey(pinyinListSplit[i])) {
                        pinyin2hanzi.put(pinyinListSplit[i], new HashSet<String>());
                    }
                    pinyin2hanzi.get(pinyinListSplit[i]).add(hanzi);

                    shengmu = Utils.getShengmu(pinyinListSplit[i]);
                    if (shengmu != null) {
                        if (!pinyin2hanzi.containsKey(shengmu)) {
                            pinyin2hanzi.put(shengmu, new HashSet<>());
                        }
                        pinyin2hanzi.get(shengmu).add(hanzi);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeStateToFile() {
        StringBuilder builder = new StringBuilder("");
        for (String string : states) {
            builder.append(string);
        }
        String str = Utils.joinLineBreak(new String(builder));

        Utils.writeStringToFile(str, ALL_STATES_FILE);
    }

    private static void writeObservationToFile() {
        StringBuilder builder = new StringBuilder("");
        for (String string : observations) {
            builder.append(string);
            builder.append("\n");
        }
        String str = new String(builder);
        str = str.substring(0, str.length() - 1);

        Utils.writeStringToFile(str, ALL_OBSERVATIONS_FILE);
    }

    private static void writePinyin2HanziToFile() {
        StringBuilder builder = new StringBuilder("");
        for (Map.Entry<String, HashSet<String>> entry : pinyin2hanzi.entrySet()) {
            builder.append(entry.getKey());
            builder.append("=");
            for (String string : entry.getValue()) {
                builder.append(string);
            }
            builder.append("\n");
        }
        String str = new String(builder);

        Utils.writeStringToFile(str, PINYIN2HANZI_FILE);
    }

    private static void initDir() {
        File input_path = new File(INPUT_PATH);
        File output_path = new File(OUTPUT_PATH);
        if (!input_path.exists()) {
            input_path.mkdirs();
        }
        if (!output_path.exists()) {
            output_path.mkdirs();
        }
    }

    public static void main(String[] args) {
        initDir();
        generator();
        writeStateToFile();
        writeObservationToFile();
        writePinyin2HanziToFile();
    }
}


