package org.kata;

import java.util.List;

import static java.util.Arrays.asList;
import static org.kata.BTreeOfIntegers.*;
import static org.kata.BTreeOfIntegers.BTreeNode.LOWEST_MIN_DEGREE;

public class BTreeTestUtils {

    public static BTreeNode makeNode(
            int minDegree, List<Integer> keys, List<BTreeNode> children)
    {
        BTreeNode expectedTree = new BTreeNode(minDegree);
        insertKeysIntoNode(keys, expectedTree);
        insertChildrenIntoNode(children, expectedTree);
        expectedTree.saveOnDisk();
        return expectedTree;
    }

    public static BTreeNode makeNode(
            List<Integer> keys, List<BTreeNode> children)
    {
        return makeNode(LOWEST_MIN_DEGREE, keys, children);
    }

    public static void insertChildrenIntoNode(
            List<BTreeNode> children, BTreeNode node)
    {
        int i = 0;
        for (BTreeNode child : children) {
            node.addChild(i++, child.getHandle());
        }
    }

    public static void insertKeysIntoTree(List<Integer> keys, BTreeOfIntegers tree) {
        keys.forEach(tree::insert);
    }

    public static void insertKeysIntoNode(List<Integer> keys, BTreeNode node) {
        keys.forEach(node::insertNonFull);
    }

    public static List<Integer> keys(Integer... key) {
        return asList(key);
    }

    public static List<BTreeNode> children(BTreeNode... child) {
        return asList(child);
    }
}