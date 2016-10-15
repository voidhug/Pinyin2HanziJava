package com.ztc.pinyinSyncopate;

public class TrieTree {
    private TrieNode root;

    public TrieTree(String name) {
        root = new TrieNode(name);
        root.setFre(0);
        root.setEnd(false);
        root.setRoot(true);
    }

    public void insert(String word) {
        TrieNode node = root;
        char[] words = word.toCharArray();
        for (int i = 0; i < words.length; i++) {
            if (node.getChildrens().containsKey(words[i] + "")) {
                if (i == words.length - 1) {
                    TrieNode endNode = node.getChildrens().get(words[i] + "");
                    endNode.setFre(endNode.getFre() + 1);
                    endNode.setEnd(true);
                }
            } else {
               TrieNode newNode = new TrieNode(words[i] + "");
                if (i == words.length - 1) {
                    newNode.setFre(1);
                    newNode.setEnd(true);
                    newNode.setRoot(false);
                }
                node.getChildrens().put(words[i] + "", newNode);
            }
            node = node.getChildrens().get(words[i] + "");
        }
    }

    public int searchFre(String word) {
        int fre = -1;
        TrieNode node = root;
        char[] words = word.toCharArray();
        for (int i = 0; i < words.length; i++) {
            if (node.getChildrens().containsKey(words[i] + "")) {
                node = node.getChildrens().get(words[i] + "");
                fre = node.getFre();
            } else {
                fre = -1;
                break;
            }
        }
        return fre;
    }

    public String splitSpell(String spell) {
        TrieNode node = root;
        char[] letters = spell.toCharArray();
        String spells = "";
        for (int i = 0; i < letters.length; i++) {
            if (node.getChildrens().containsKey(letters[i] + "")) {
                spells += letters[i];
                node = node.getChildrens().get(letters[i] + "");
            } else {
                node = root;
                spells += " ";
                i--;
            }
        }

        return spells;
    }
}
