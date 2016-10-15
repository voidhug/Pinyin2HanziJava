package com.ztc.pinyin2hanzi;

import com.ztc.pinyinSyncopate.PinyinSyncopate;

import java.util.ArrayList;
import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        DefaultHmmParams hmmParams = new DefaultHmmParams();

        while (true) {
            System.out.println("请输入拼音: ");
            Scanner scanner = new Scanner(System.in);
            String pinyin = scanner.nextLine();
            for (PriorityQueueWrapper.Item item : Viterbi.compute(hmmParams, PinyinSyncopate.splitSpell(pinyin), 3, false)) {
                System.out.println(item.getScore() + "  " + item.getPath());
            }
        }
    }
}
