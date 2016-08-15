package org.kata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Collections.binarySearch;
import static java.util.Collections.unmodifiableList;

public class BTreeNode {
    public static final int MIN_BRANCHING_FACTOR = 2;
    private int branchingFactor;

    private List<Integer> keys = new ArrayList<>();
    private List<BTreeNode> childNodes = new ArrayList<>();

    public BTreeNode(int branchingFactor) {
        if (branchingFactor < MIN_BRANCHING_FACTOR) {
            throw new IllegalArgumentException(
                    format("Branching Factor should be greater than " +
                            "or equals to 2, but passed '%d'", branchingFactor));
        }
        this.branchingFactor = branchingFactor;
    }

    public BTreeNode insert(int key) {
        if (isFull()) {
            BTreeNode parent = split();
            parent.insertToNotFull(key);
            return parent;
        } else {
            insertToNotFull(key);
        }
        return this;
    }

    public boolean contains(int key) {
        return keys.contains(key);
    }

    public void remove(int key) {
        keys.remove(valueOf(key));
    }

    public List<BTreeNode> getChildNodes() {
        return unmodifiableList(childNodes);
    }

    public List<Integer> getKeys() {
        return unmodifiableList(keys);
    }

    public boolean isLeaf() {
        return childNodes.size() == 0;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(branchingFactor)
                .append(keys)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        BTreeNode otherNode = (BTreeNode) obj;
        return new EqualsBuilder()
                .append(branchingFactor, otherNode.branchingFactor)
                .append(keys, otherNode.keys)
                .isEquals();
    }

    @Override
    public String toString() {
        return ToStringBuilder
                .reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    private BTreeNode findChildNodeToInsertKey(int key) {
        int i = keys.size() - 1;
        while (i >= 0 && key < keys.get(i)) {
            i--;
        }
        return childNodes.get(i + 1);
    }

    private int findPositionToInsertKey(int keyToInsert) {
        return ~binarySearch(keys, keyToInsert);
    }

    private BTreeNode split() {
        BTreeNode parent = new BTreeNode(branchingFactor);
        int median = keys.get(branchingFactor - 1);
        parent.insert(median);

        BTreeNode leftSubNode = new BTreeNode(branchingFactor);
        BTreeNode rightSubNode = new BTreeNode(branchingFactor);

        keys.stream().forEach(k -> {
            if (k < median) {
                leftSubNode.insert(k);
            }
            if (k > median) {
                rightSubNode.insert(k);
            }
        });

        parent.addChild(leftSubNode);
        parent.addChild(rightSubNode);

        return parent;
    }

    private void addChild(BTreeNode childNode) {
        this.childNodes.add(childNode);
    }

    private int maxKeysPerNode() {
        return 2 * branchingFactor - 1;
    }

    private boolean isFull() {
        return keys.size() >= maxKeysPerNode();
    }

    private void insertToNotFull(int key) {
        if (isLeaf()) {
            int position = findPositionToInsertKey(key);
            keys.add(position, key);
        }
        else {
            BTreeNode node = findChildNodeToInsertKey(key);
            node.insert(key);
        }
    }
}