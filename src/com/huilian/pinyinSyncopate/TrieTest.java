package com.huilian.pinyinSyncopate;

public class TrieTest {
    public static void main(String[] args) {
        TrieTree tree = new TrieTree("test");
        tree.insert("word");
        tree.insert("hello");
        tree.insert("hi");
        System.out.println("word " + tree.searchFre("word"));
        System.out.println("hello " + tree.searchFre("hello"));
        System.out.println("hi " + tree.searchFre("hi"));
        System.out.println("hell " + tree.searchFre("hell"));
        System.out.println("hellt " + tree.searchFre("hellt"));
        System.out.println("hellohi" + " split to " + tree.splitSpell("hellohi"));
    }
}
