package org.kata;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BTreeOfIntegersContractTest {
    @Test
    public void canAddOneKeyToEmptyTree() throws Exception {
        int branchingFactor = 3;
        BTreeOfIntegers tree = new BTreeOfIntegers(branchingFactor);
        insertKeysIntoTree(keys(1234), tree);

        assertTrue("Tree should contain just added key", tree.contains(1234));
        assertFalse("Tree should not contain key that is not yet added",
                tree.contains(4567));
    }

    @Test
    public void canRemoveKeyAddedPreviously() throws Exception {
        int testKey = 1234;
        BTreeOfIntegers tree = new BTreeOfIntegers(3);
        insertKeysIntoTree(keys(1234), tree);
        tree.remove(testKey);

        assertFalse("Tree should not contain removed testKey", tree.contains(testKey));
    }

    @Test
    public void whenRootLeafNodeIsFullAndKeyIsAdded()
            throws Exception
    {
        int branchFactor = 2;
        WhiteBoxTestableBTreeOnIntegers tree =
                new WhiteBoxTestableBTreeOnIntegers(branchFactor);
        insertKeysIntoTree(keys(1234, 5678, 9012, 3456), tree);

        BTreeNode expectedRootNode = new BTreeNode(branchFactor);
        insertKeysIntoNode(keys(5678), expectedRootNode);
        assertThat(tree.getRoot(), is(expectedRootNode));

        BTreeNode expectedLeftNode = new BTreeNode(branchFactor);
        insertKeysIntoNode(keys(1234, 3456), expectedLeftNode);

        BTreeNode expectedRightNode = new BTreeNode(branchFactor);
        insertKeysIntoNode(keys(9012), expectedRightNode);

        assertThat(tree.getRoot().getChildNodes(),
                contains(expectedLeftNode, expectedRightNode));
    }

    @Test
    public void whenNonRootNodeIsFullAndNewKeyIsAdded()
            throws Exception
    {
        int branchingFactor = 2;
        WhiteBoxTestableBTreeOnIntegers tree =
                new WhiteBoxTestableBTreeOnIntegers(branchingFactor);
        insertKeysIntoTree(keys(1234, 5678, 9012, 3456, 4010, 4020), tree);

        BTreeNode first = new BTreeNode(branchingFactor);
        insertKeysIntoNode(keys(1234), first);

        BTreeNode second = new BTreeNode(branchingFactor);
        insertKeysIntoNode(keys(4010, 4020), second);

        BTreeNode third = new BTreeNode(branchingFactor);
        insertKeysIntoNode(keys(9012), third);

        BTreeNode root = tree.getRoot();

        assertThat(root.getKeys(), contains(3456, 5678));
        assertThat(root.getChildNodes(), contains(first, second, third));
    }

    private void insertKeysIntoTree(List<Integer> keys, BTreeOfIntegers tree) {
        keys.forEach(tree::insert);
    }

    private void insertKeysIntoNode(List<Integer> keys, BTreeNode node) {
        keys.forEach(node::insertNonFull);
    }

    private List<Integer> keys(Integer... key) {
        return asList(key);
    }

    private static class WhiteBoxTestableBTreeOnIntegers
            extends BTreeOfIntegers
    {
        public WhiteBoxTestableBTreeOnIntegers(int branchingFactor) {
            super(branchingFactor);
        }
        public BTreeNode getRoot() {
            return root;
        }
    }
}