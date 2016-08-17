package org.kata;

import java.util.List;

import static java.util.Arrays.asList;

public class BTreeTestUtils {

    public static BTreeNode makeNode(
            int minDegree, List<Integer> keys, List<BTreeNode> children)
    {
        BTreeNode expectedTree = new BTreeNode(minDegree);
        insertKeysIntoNode(keys, expectedTree);
        insertChildrenIntoNode(children, expectedTree);
        return expectedTree;
    }

    public static void insertChildrenIntoNode(
            List<BTreeNode> children, BTreeNode node)
    {
        int i = 0;
        for (BTreeNode child : children) {
            node.addChild(i++, child);
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