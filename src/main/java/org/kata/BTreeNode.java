package org.kata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

public class BTreeNode {
    // lowest min degree (= or minimum number of node children) must be
    // greater than 2, otherwise a tree is degenerate to a list
    public static final int LOWEST_MIN_DEGREE = 2;

    private int minDegree;
    private List<Integer> keys = new ArrayList<>();
    private List<BTreeNode> childNodes = new ArrayList<>();

    public BTreeNode(int minDegree) {
        if (minDegree < LOWEST_MIN_DEGREE) {
            throw new IllegalArgumentException(
                    format("Min degree for tree node should be greater than " +
                            "or equals to 2, but passed '%d'", minDegree));
        }
        this.minDegree = minDegree;
    }

    public boolean contains(int key) {
        int index = 0;
        while (index < keys.size() && key > keys.get(index)) {
            index++;
        }
        return keys.contains(key) ||
                !isLeaf() && childNodes.get(index).contains(key);
    }

    public void remove(int key) {
        keys.remove(valueOf(key));
    }

    public boolean isLeaf() {
        return childNodes.size() == 0;
    }

    public boolean isFull() {
        return keys.size() >= maxKeysPerNode();
    }

    public int getMinDegree() {
        return minDegree;
    }

    public void splitChild(BTreeNode child) {
        BTreeNode parent = this;
        int median = child.getMedianOfKeys();

        BTreeNode newSubNode = new BTreeNode(minDegree);
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
                .append(minDegree)
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
                .append(minDegree, otherNode.minDegree)
                .append(keys, otherNode.keys)
                .append(childNodes, otherNode.childNodes)
                .isEquals();
    }

    @Override
    public String toString() {
        return reflectionToString(this, JSON_STYLE);
    }

    protected List<Integer> getKeys() {
        return unmodifiableList(keys);
    }

    protected List<BTreeNode> getChildren() {
        return unmodifiableList(childNodes);
    }

    protected List<BTreeNode> getAllDescendants() {
        List<BTreeNode> allDescendants = new ArrayList<>();
        allDescendants.addAll(getChildren());
        if (!isLeaf()) {
            getChildren().stream().forEach(
                    child -> allDescendants.addAll(child.getAllDescendants()));
        }
        return allDescendants;
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
        keys = keys.subList(0, minDegree - 1);
    }

    private void moveHalfOfChilds(BTreeNode destNode) {
        List<BTreeNode> childrenForNewNode = getChildrenForNewSubNodeOnSplit();
        destNode.insertChildren(childrenForNewNode);
        childNodes.removeAll(childrenForNewNode);
    }

    private Integer getMedianOfKeys() {
        return keys.get(minDegree - 1);
    }

    private List<Integer> getKeysForNewSubNodeOnSplit() {
        return keys.subList(minDegree, keys.size());
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
        int index = 0;
        while (index < keys.size() && key > keys.get(index)) {
            index++;
        }
        return index;
    }

    private int getChildPosition(BTreeNode child) {
        return childNodes.indexOf(child);
    }

    private int maxKeysPerNode() {
        return 2 * minDegree - 1;
    }
}