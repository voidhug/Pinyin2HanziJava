package com.ztc.pinyinSyncopate;

import com.ztc.Utils;

import java.util.ArrayList;
import java.util.List;

public class PinyinSyncopate {
    private static TrieTree tree = new TrieTree("root");

    static {
        initSpells();
    }

    public static ArrayList<String> splitSpell(String spell) {
        String[] pinyinArrays = null;
        pinyinArrays = tree.splitSpell(spell).split(" ");
        ArrayList<String> pinyinList = new ArrayList<String>();
        for (String string : pinyinArrays) {
            pinyinList.add(string);
        }
        return pinyinList;
    }

    private static void initSpells() {
        String filePath = "./res/output/all_observations.txt";
        List<String> spells = new ArrayList<String>();
        Utils.readFile(spells, filePath);
        for (int i = 0; i < spells.size(); i++) {
            tree.insert(spells.get(i));
        }
    }
}
