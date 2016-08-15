package org.kata;

public class BTreeOfIntegers {
    private int branchingFactor;
    protected BTreeNode root;

    public BTreeOfIntegers(int branchingFactor) {
        this.root = new BTreeNode(branchingFactor);
        this.branchingFactor = branchingFactor;
    }

    public void insert(int key) {
        if (root.isFull()) {
            BTreeNode newRoot = new BTreeNode(branchingFactor);
            newRoot.addChild(root);
            newRoot.splitChild(root);
            root = newRoot;
        }
        root.insertNonFull(key);
    }

    public boolean contains(int key) {
        return this.root.contains(key);
    }

    public void remove(int key) {
        this.root.remove(key);
    }
}