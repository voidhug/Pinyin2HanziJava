package com.huilian.pinyin2hanzi;

import com.huilian.Utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DefaultHmmParams implements InterfaceHmmParams {
    private Map<String, String> pinyin2hanziMap = new HashMap<String, String>();
    // {"你" : 2, "号" : 1}
    private HashMap<String, Double> start = new HashMap<String, Double>();
    // {"泥" : {"ni" : 1.0}}, }
    private HashMap<String, HashMap<String, Double>> emission = new HashMap<String, HashMap<String, Double>>();
    // {"你": {"好" : 10, "们" : 2}}
    private HashMap<String, HashMap<String, Double>> transition = new HashMap<String, HashMap<String, Double>>();

    private static final String FINAL_PINYIN2HANZI_FILE = "./res/output/data/hmm_pinyin2hanzi.txt";
    private static final String FINAL_START_FILE = "./res/output/data/hmm_start.txt";
    private static final String FINAL_EMISSION_FILE = "./res/output/data/hmm_emission.txt";
    private static final String FINAL_TRANSITION_FILE = "./res/output/data/hmm_transition.txt";

    public DefaultHmmParams() {
        initPinyin2HanziMap();
        initStart();
        initEmission();
        initTransition();
    }

    private void initPinyin2HanziMap() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String pinyin = null;
        String hanziList = null;
        try {
            buf = new BufferedReader(new FileReader(FINAL_PINYIN2HANZI_FILE));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitResult = line.split("=");
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

    private void initStart() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String hanzi = null;
        Double num = null;
        try {
            buf = new BufferedReader(new FileReader(FINAL_START_FILE));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitResult = line.split("=");
                hanzi = lineSplitResult[0];
                num = Double.valueOf(lineSplitResult[1]);
                start.put(hanzi, num);
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

    private void initEmission() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String hanzi = null;
        String pinyinNum = null;
        String[] pinyinNumSplitResult = null;
        String[] pinyinNumSplitSingleResult = null;
        HashMap<String, Double> pinyinNumMap = null;

        try {
            buf = new BufferedReader(new FileReader(FINAL_EMISSION_FILE));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitResult = line.split("=");
                hanzi = lineSplitResult[0];
                pinyinNum = lineSplitResult[1];
                pinyinNumSplitResult = pinyinNum.split(",");
                pinyinNumMap = new HashMap<String, Double>();
                for (String string : pinyinNumSplitResult) {
                    pinyinNumSplitSingleResult = string.split(":");
                    pinyinNumMap.put(pinyinNumSplitSingleResult[0], Double.valueOf(pinyinNumSplitSingleResult[1]));
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

    private void initTransition() {
        BufferedReader buf = null;
        String line = null;
        String[] lineSplitResult = null;
        String hanzi1 = null;
        String hanzi2String = null;
        String[] hanzi2StringSplitResult = null;
        String[] hanzi2StringSplitResultSingle = null;
        HashMap<String, Double> hanzi2NumMap = null;

        try {
            buf = new BufferedReader(new FileReader(FINAL_TRANSITION_FILE));
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                lineSplitResult = line.split("=");
                hanzi1 = lineSplitResult[0];
                hanzi2String = lineSplitResult[1];
                hanzi2StringSplitResult = hanzi2String.split(",");
                hanzi2NumMap = new HashMap<String, Double>();
                for (String string : hanzi2StringSplitResult) {
                    hanzi2StringSplitResultSingle = string.split(":");
                    hanzi2NumMap.put(hanzi2StringSplitResultSingle[0], Double.valueOf(hanzi2StringSplitResultSingle[1]));
                }
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

    @Override
    public Double start(String state) {
        if (start.get(state) == null) {
            return 3.3108977785289716e-09;
        }
        return start.get(state);
    }

    @Override
    public Double emission(String state, String observation) {
        if (emission.get(state).get(observation) == null) {
            return 3.3108977785289716e-09;
        }
        return emission.get(state).get(observation);
    }

    @Override
    public Double transition(String state1, String state2) {
        if (transition.get(state1) == null) {
            return 3.3108977785289716e-09;
        }
        if (transition.get(state1).get(state2) == null) {
            return 3.3108977785289716e-09;
        }
        return transition.get(state1).get(state2);
    }

    @Override
    public ArrayList<String> getStates(String observation) {
        String hanziListString = pinyin2hanziMap.get(observation);
        ArrayList<String> hanziList = new ArrayList<String>();
        for (int i = 0; i < hanziListString.length(); i++) {
            hanziList.add(hanziListString.charAt(i) + "");
        }
        return hanziList;
    }

    public static void main(String[] args) {
        DefaultHmmParams hmmParams = new DefaultHmmParams();
        ArrayList<String> observations = new ArrayList<String>();
        observations.add("zhi");
        observations.add("ye");
        observations.add("wan");
        observations.add("jia");
        Viterbi.viterbi(hmmParams, observations, 3, false);
    }
}
