package com.ztc.pinyinSyncopate;

import com.ztc.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PinyinSyncopate {
    private static TrieTree tree = new TrieTree("root");

    static {
        initSpells();
    }

    public static ArrayList<String> splitSpell(String spell) {
        ArrayList<String> result = new ArrayList<String>();
        String[] pinyinInitials = new String[] {
                "a", "b", "e", "p", "m", "f", "d",
                "t", "n", "l", "g", "k", "h", "j",
                "q", "x", "r", "z", "c", "s", "y", "w"
        };
        String[] juv = new String[] {"i","u","v"};
        String[] grn = new String[] {"g","r","n"};

        String input = "";

        for (int i = 0; i < spell.length(); i++) {
            String c = spell.charAt(i) + "";
            input += c;

            if (Arrays.asList(juv).contains(c) && input.length() == 1) {
                return null;
            }

            if (tree.findInitialWith(input)) {
                continue;
            }

            if (Arrays.asList(pinyinInitials).contains(c)) {
                if (tree.findInitialWith(input.substring(0, input.length() - 1))) {
                    result.add(input.substring(0, input.length() - 1));
                    input = input.substring(input.length() - 1, input.length());
                    continue;
                } else {
                    return null;
                }
            } else if (Arrays.asList(grn).contains(input.charAt(input.length() - 2) + "")) {
                if (tree.findInitialWith(input.substring(0, input.length() - 2))) {
                    result.add(input.substring(0, input.length() - 2));
                    input = input.substring(input.length() - 2, input.length());
                    continue;
                } else if (tree.findInitialWith(input.substring(0, input.length() - 1))) {
                    result.add(input.substring(0, input.length() - 1));
                    input = input.substring(input.length() - 1, input.length());
                    continue;
                }
            } else {
                result.add(input);
                input = "";
            }
        }

        result.add(input);
        return result;
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
        String str = "quni";
//        str = str.substring(str.length() - 2, str.length());
//        Utils.println(str);
        if (splitSpell(str) == null) {
            System.out.println("null");
        } else {
            for (String string : splitSpell(str)) {
                System.out.print(string + " ");
            }
        }
    }
}
