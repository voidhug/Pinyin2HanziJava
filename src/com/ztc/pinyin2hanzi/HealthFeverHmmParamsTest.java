package com.ztc.pinyin2hanzi;

import java.util.ArrayList;
import java.util.HashMap;

public class HealthFeverHmmParamsTest implements InterfaceHmmParams {
    private ArrayList<String> states = new ArrayList<String>();
    private HashMap<String, Double> start = new HashMap<String, Double>();
    private HashMap<String, HashMap<String, Double>> emission = new HashMap<String, HashMap<String, Double>>();
    private HashMap<String, HashMap<String, Double>> transition = new HashMap<String, HashMap<String, Double>>();

    public HealthFeverHmmParamsTest() {
        initStates();
        initStart();
        initEmission();
        initTransition();
    }

    private void initStates() {
        states.add("Healthy");
        states.add("Fever");
    }

    private void initStart() {
        start.put("Healthy", 0.6);
        start.put("Fever", 0.4);
    }

    private void initEmission() {
        HashMap<String, Double> map1 = new HashMap<>();
        map1.put("normal", 0.5);
        map1.put("cold", 0.4);
        map1.put("dizzy", 0.1);
        emission.put("Healthy", map1);

        HashMap<String, Double> map2 = new HashMap<>();
        map2.put("normal", 0.1);
        map2.put("cold", 0.3);
        map2.put("dizzy", 0.6);
        emission.put("Fever", map2);
    }

    private void initTransition() {
        HashMap<String, Double> map1 = new HashMap<>();
        map1.put("Healthy", 0.7);
        map1.put("Fever", 0.3);
        transition.put("Healthy", map1);

        HashMap<String, Double> map2 = new HashMap<>();
        map2.put("Healthy", 0.4);
        map2.put("Fever", 0.6);
        transition.put("Fever", map2);
    }

    @Override
    public Double start(String state) {
        return this.start.get(state);
    }

    @Override
    public Double emission(String state, String observation) {
        return this.emission.get(state).get(observation);
    }

    @Override
    public Double transition(String state1, String state2) {
        return this.transition.get(state1).get(state2);
    }

    @Override
    public ArrayList<String> getStates(String observation) {
        return states;
    }

    public static void main(String[] args) {
        HealthFeverHmmParamsTest hmmParams = new HealthFeverHmmParamsTest();
        ArrayList<String> observations = new ArrayList<String>();
        observations.add("normal");
        observations.add("cold");
        observations.add("dizzy");
        Viterbi.viterbi(hmmParams, observations, 3, false);
    }
}
