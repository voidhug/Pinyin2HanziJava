package com.ztc.pinyinSyncopate;

import com.ztc.Utils;

import java.util.ArrayList;
import java.util.List;

public class PinyinSyncopate {
    private static TrieTree tree = new TrieTree("root");

    static {
        initSpells();
    }

    public static String splitSpell(String spell) {
        return tree.splitSpell(spell);
    }

    private static void initSpells() {
        String filePath = "./res/output/all_observations.txt";
        List<String> spells = new ArrayList<String>();
        Utils.readFile(spells, filePath);
        for (int i = 0; i < spells.size(); i++) {
            tree.insert(spells.get(i));
        }
    }

    public static void main(String[] args) {
        String spell = "nuli";
        Utils.println(splitSpell(spell));
    }
}
