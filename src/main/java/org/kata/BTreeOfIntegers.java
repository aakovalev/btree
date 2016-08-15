package org.kata;

public class BTreeOfIntegers {
    private int height = 0;
    private BTreeNode root;

    public BTreeOfIntegers(int branchingFactor) {
        this.root = new BTreeNode(branchingFactor);
    }

    public void insert(int key) {
        this.root.insert(key);
    }

    public boolean contains(int key) {
        return this.root.contains(key);
    }

    public void remove(int key) {
        this.root.remove(key);
    }

    public int getHeight() {
        return height;
    }
}