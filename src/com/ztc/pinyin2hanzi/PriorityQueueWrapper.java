package com.ztc.pinyin2hanzi;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;

public class PriorityQueueWrapper {
    private PriorityQueue<Item> priorityQueue;
    private int capacity;

    public PriorityQueueWrapper(int capacity) {
        priorityQueue = new PriorityQueue<Item>(new MyComparator());
        this.capacity = capacity;
    }

    public void put(double score, ArrayList<String> path) {
        Item item = new Item(score, path);
        priorityQueue.add(item);
        if (priorityQueue.size() > capacity) {
            priorityQueue.poll();
        }
    }

    public PriorityQueue<Item> getPriorityQueue() {
        return priorityQueue;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("");
        for (Item item : priorityQueue) {
            builder.append(item.getScore());
            builder.append("  ");
            builder.append(item.getPath());
            builder.append("\n");
        }
        return new String(builder);
    }

    class Item {
        private double score;
        private ArrayList<String> path;

        Item(double score, ArrayList<String> path) {
            this.score = score;
            this.path = path;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public ArrayList<String> getPath() {
            return path;
        }

        public void setPath(ArrayList<String> path) {
            this.path = path;
        }
    }

    class MyComparator implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            Item item1 = (Item) o1;
            Item item2 = (Item) o2;
            if (item1.getScore() > item2.getScore()) {
                return -1;
            }
            if (item1.getScore() < item2.getScore()) {
                return 1;
            }
            return 0;
        }
    }
}
