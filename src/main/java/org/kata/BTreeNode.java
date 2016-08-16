package org.kata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Collections.binarySearch;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

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
        int index = 0;
        while (index < keys.size() && key > keys.get(index)) {
            index++;
        }
        return keys.contains(key) || !isLeaf() && childNodes.get(index).contains(key);
    }

    public void remove(int key) {
        keys.remove(valueOf(key));
    }

    public List<Integer> getKeys() {
        return unmodifiableList(keys);
    }

    public boolean isLeaf() {
        return childNodes.size() == 0;
    }

    public boolean isFull() {
        return keys.size() >= maxKeysPerNode();
    }

    public int getBranchingFactor() {
        return branchingFactor;
    }

    public void splitChild(BTreeNode child) {
        BTreeNode parent = this;
        int median = child.getMedianOfKeys();

        BTreeNode newSubNode = new BTreeNode(branchingFactor);
        child.moveHalfOfKeysTo(newSubNode);
        if (!child.isLeaf()) {
            child.moveHalfOfChilds(newSubNode);
        }

        parent.insertKey(median);
        int newSubNodePositionInParent = parent.getChildPosition(child) + 1;
        parent.addChild(newSubNodePositionInParent, newSubNode);
    }

    public void insertNonFull(int key) {
        if (isLeaf()) {
            insertKey(key);
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(branchingFactor)
                .append(keys)
                .append(childNodes)
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
                .append(childNodes, otherNode.childNodes)
                .isEquals();
    }

    @Override
    public String toString() {
        return reflectionToString(this, JSON_STYLE);
    }

    protected void addChild(int index, BTreeNode childNode) {
        this.childNodes.add(index, childNode);
    }

    private void insertKeys(List<Integer> keysToInsert) {
        keysToInsert.stream().forEach(this::insertNonFull);
    }

    private void insertKey(int key) {
        int position = findPositionForKey(key);
        keys.add(position, key);
    }

    private void insertChildren(List<BTreeNode> children) {
        this.childNodes.addAll(children);
    }

    private void moveHalfOfKeysTo(BTreeNode destNode) {
        List<Integer> keysForNewSubNode = getKeysForNewSubNodeOnSplit();
        destNode.insertKeys(keysForNewSubNode);
        keys.removeAll(getKeysToCut());
    }

    private void moveHalfOfChilds(BTreeNode destNode) {
        List<BTreeNode> childrenForNewNode = getChildrenForNewSubNodeOnSplit();
        destNode.insertChildren(childrenForNewNode);
        childNodes.removeAll(childrenForNewNode);
    }

    private Integer getMedianOfKeys() {
        return keys.get(branchingFactor - 1);
    }

    private List<Integer> getKeysForNewSubNodeOnSplit() {
        int median = getMedianOfKeys();
        return keys.stream().filter(k -> k > median).collect(toList());
    }

    private List<Integer> getKeysToCut() {
        List<Integer> keysToCut = getKeysForNewSubNodeOnSplit();
        keysToCut.add(getMedianOfKeys());
        return keysToCut;
    }

    private List<BTreeNode> getChildrenForNewSubNodeOnSplit() {
        return childNodes
                .subList(childNodes.size() / 2, childNodes.size());
    }

    private BTreeNode findChildNodeToInsertKey(int key) {
        int childNodeIndexByKey = findChildNodeIndexByKey(key);
        return childNodes.get(childNodeIndexByKey);
    }

    private int findChildNodeIndexByKey(int key) {
        return findPositionForKey(key);
    }

    private int findPositionForKey(int key) {
        return ~binarySearch(keys, key);
    }

    private int getChildPosition(BTreeNode child) {
        return childNodes.indexOf(child);
    }

    private int maxKeysPerNode() {
        return 2 * branchingFactor - 1;
    }
}