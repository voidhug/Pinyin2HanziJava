package com.ztc.pinyin2hanzi;

import java.util.ArrayList;
import java.util.HashMap;

public class Viterbi {
    public static void viterbi(InterfaceHmmParams hmmParams, ArrayList<String> observations, int pathNum, boolean isLog) {
        double minProb = 3.14e-200;
        ArrayList<HashMap<String, PriorityQueueWrapper>> V = new ArrayList<HashMap<String, PriorityQueueWrapper>>();
        int t = 0;
        String curObs = observations.get(0);

        ArrayList<String> prevStates, curStates;
        prevStates = curStates = hmmParams.getStates(curObs);

        Double score = 0.0;
        ArrayList<String> path = null;
        V.add(new HashMap<String, PriorityQueueWrapper>());
        for (String state : curStates) {
            if (isLog) {
                score = Math.log(Math.max(hmmParams.start(state), minProb)) + Math.log(Math.max(hmmParams.emission(state, curObs), minProb));
            } else {
                score = Math.max(hmmParams.start(state), minProb) * Math.max(hmmParams.emission(state, curObs), minProb);
            }
            path = new ArrayList<>();
            path.add(state);

            if (!V.get(0).containsKey(state)) {
                V.get(0).put(state, new PriorityQueueWrapper(pathNum));
            }
            V.get(0).get(state).put(score, path);
        }

        for (int i = 1; i < observations.size(); i++) {
            curObs = observations.get(i);

            if (V.size() == 2) {
                V.remove(0);
            }

            V.add(new HashMap<String, PriorityQueueWrapper>());

            prevStates = curStates;
            curStates = hmmParams.getStates(curObs);

            for (String state : curStates) {
                if (!V.get(1).containsKey(state)) {
                    V.get(1).put(state, new PriorityQueueWrapper(pathNum));
                }
                ArrayList<String> newPath = null;
                for (String state0 : prevStates) {
                    for (PriorityQueueWrapper.Item item : V.get(0).get(state0).getPriorityQueue()) {
                        if (isLog) {
                            score = item.getScore()
                                    + Math.log(Math.max(hmmParams.transition(state0, state), minProb))
                                    + Math.log(Math.max(hmmParams.emission(state, curObs), minProb));
                        } else {
                            score = item.getScore()
                                    * Math.max(hmmParams.transition(state0, state), minProb)
                                    * Math.max(hmmParams.emission(state, curObs), minProb);
                        }
                        newPath = new ArrayList<String>();
                        newPath.addAll(item.getPath());
                        newPath.add(state);
                        V.get(1).get(state).put(score, newPath);
                    }
                }
            }
        }

        PriorityQueueWrapper priorityQueueWrapperResult = new PriorityQueueWrapper(pathNum);
        for (String lastState : V.get(1).keySet()) {
            for (PriorityQueueWrapper.Item item : V.get(1).get(lastState).getPriorityQueue()) {
                priorityQueueWrapperResult.put(item.getScore(), item.getPath());
            }
        }

        for (PriorityQueueWrapper.Item item : priorityQueueWrapperResult.getPriorityQueue()) {
            System.out.println(item.getPath());
        }
    }
}
