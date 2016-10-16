package com.ztc.correct;

import com.ztc.Utils;
import com.ztc.hanzi2pinyin.PinyinException;
import com.ztc.hanzi2pinyin.PinyinFormat;
import com.ztc.hanzi2pinyin.PinyinHelper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class TextProcessing {
    private static final String INPUT_CHINESE = "./res/input/correction/ref_word.txt";
    private static final String INPUT_ERROR_PINYIN = "./res/input/correction/ref_phone.txt";

    private static final String CORRECTION_OUTPUT_DIR = "./res/output/correction";
    private static final String OUTPUT_CHINESE = "./res/output/correction/chinese.txt";
    private static final String OUTPUT_TRUE_PINYIN = "./res/output/correction/true_pinyin.txt";
    private static final String OUTPUT_ERROR_PINYIN = "./res/output/correction/error_pinyin.txt";


    private static void initDir() {
        Utils.createDir(CORRECTION_OUTPUT_DIR);
    }

    private static void generatorChinese() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitArray = null;
        StringBuilder builder = new StringBuilder("");
        try {
            buf = new BufferedReader(new FileReader(INPUT_CHINESE));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitArray = line.split(" ");
                String[] lineSplitArray1 = new String[lineSplitArray.length - 1];
                System.arraycopy(lineSplitArray, 1, lineSplitArray1, 0, lineSplitArray1.length);
                for (String string : lineSplitArray1) {
                    builder.append(string);
                }
                builder.append("\n");
            }
            builder.deleteCharAt(builder.length() - 1);
            Utils.writeStringToFile(new String(builder), OUTPUT_CHINESE);
            buf.close();
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

    private static void generatorErrorPinyin() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitArray = null;
        ArrayList<String> pinyinList = new ArrayList<String>();
        String single = null;
        StringBuilder builder = new StringBuilder("");

        try {
            buf = new BufferedReader(new FileReader(INPUT_ERROR_PINYIN));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitArray = line.split(" ");
                String[] lineSplitArray1 = new String[lineSplitArray.length - 1];
                System.arraycopy(lineSplitArray, 1, lineSplitArray1, 0, lineSplitArray1.length);
                for (int i = 0; i < lineSplitArray1.length; i++) {
                    if (i % 2 == 0) {
                        single = lineSplitArray1[i] + lineSplitArray1[i + 1].substring(0, lineSplitArray1[i + 1].length() - 1);
                        pinyinList.add(single);
                    }
                }
                for (String str : pinyinList) {
                    builder.append(str);
                    builder.append(" ");
                }
                builder.deleteCharAt(builder.length() - 1);
                builder.append("\n");
                pinyinList.clear();
            }
            builder.deleteCharAt(builder.length() - 1);
            Utils.writeStringToFile(new String(builder), OUTPUT_ERROR_PINYIN);
            buf.close();
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

    private static void generatorTruePinyin() {
        BufferedReader buf = null;
        String line = null;
        String pinyinsStr = null;
        String[] pinyinsArray = null;
        StringBuilder builder = new StringBuilder("");

        try {
            buf = new BufferedReader(new FileReader(OUTPUT_CHINESE));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                try {
                    pinyinsStr = PinyinHelper.convertToPinyinString(line, ",", PinyinFormat.WITHOUT_TONE);
                    pinyinsArray = pinyinsStr.trim().split(",");
                    for (String pinyin : pinyinsArray) {
                        builder.append(pinyin);
                        builder.append(" ");
                    }
                    builder.deleteCharAt(builder.length() - 1);
                    builder.append("\n");
                } catch (PinyinException e) {
                    e.printStackTrace();
                }
            }
            builder.deleteCharAt(builder.length() - 1);
            Utils.writeStringToFile(new String(builder), OUTPUT_TRUE_PINYIN);
            buf.close();
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

    public static void main(String[] args) {
        initDir();
        generatorChinese();
        generatorErrorPinyin();
        generatorTruePinyin();
    }
}
