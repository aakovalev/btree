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
        int childNodeIndexByKey = findChildNodeIndexByKey(key);
        return childNodes.get(childNodeIndexByKey);
    }

    private int findChildNodeIndexByKey(int key) {
        int i = keys.size() - 1;
        while (i >= 0 && key < keys.get(i)) {
            i--;
        }
        return i + 1;
    }

    private int findPositionToInsertKey(int keyToInsert) {
        return ~binarySearch(keys, keyToInsert);
    }

    public void splitChild(BTreeNode child) {
        BTreeNode parent = this;
        int median = child.keys.get(branchingFactor - 1);
        child.keys.remove(valueOf(median));

        BTreeNode newSubNode = new BTreeNode(branchingFactor);
        child.keys.stream()
                .filter(k -> k > median)
                .forEach(newSubNode::insertNonFull);
        child.keys.removeAll(newSubNode.getKeys());

        int keyPositionInParent = parent.findPositionToInsertKey(median);
        parent.keys.add(keyPositionInParent, median);
        int newSubNodePositionInParent = parent.childNodes.indexOf(child) + 1;
        parent.childNodes.add(newSubNodePositionInParent, newSubNode);
    }

    protected void addChild(BTreeNode childNode) {
        this.childNodes.add(childNode);
    }

    private int maxKeysPerNode() {
        return 2 * branchingFactor - 1;
    }

    public boolean isFull() {
        return keys.size() >= maxKeysPerNode();
    }

    public void insertNonFull(int key) {
        if (isLeaf()) {
            int position = findPositionToInsertKey(key);
            keys.add(position, key);
        } else {
            BTreeNode node = findChildNodeToInsertKey(key);
            if (node.isFull()) {
                splitChild(node);
                insertNonFull(key);
            }
            else {
                node.insertNonFull(key);
            }
        }
    }
}