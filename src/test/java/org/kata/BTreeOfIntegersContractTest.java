package org.kata;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BTreeOfIntegersContractTest {
    @Test
    public void canAddOneKeyToEmptyTree() throws Exception {
        int key = 1234;
        BTreeOfIntegers tree = new BTreeOfIntegers(3);
        tree.insert(key);

        assertTrue("Tree should contain just added key", tree.contains(key));
        assertFalse("Tree should not contain key that is not yet added",
                tree.contains(4567));
    }

    @Test
    public void canRemoveKeyAddedPreviously() throws Exception {
        int key = 1234;
        BTreeOfIntegers tree = new BTreeOfIntegers(3);
        tree.remove(key);

        assertFalse("Tree should not contain removed key", tree.contains(key));
    }

    @Test
    public void whenRootLeafNodeIsFullAndKeyIsAdded()
            throws Exception
    {
        int branchFactor = 2;
        WhiteBoxTestableBTreeOnIntegers tree =
                new WhiteBoxTestableBTreeOnIntegers(2);
        tree.insert(1234);
        tree.insert(5678);
        tree.insert(9012);
        // next operation should cause node split
        tree.insert(3456);

        BTreeNode expectedRootNode = new BTreeNode(branchFactor);
        expectedRootNode.insertNonFull(5678);
        assertThat(tree.getRoot(), is(expectedRootNode));

        BTreeNode expectedLeftNode = new BTreeNode(branchFactor);
        expectedLeftNode.insertNonFull(1234);
        expectedLeftNode.insertNonFull(3456);

        BTreeNode expectedRightNode = new BTreeNode(branchFactor);
        expectedRightNode.insertNonFull(9012);

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
        tree.insert(1234);
        tree.insert(5678);
        tree.insert(9012);
        tree.insert(3456);
        tree.insert(4010);
        tree.insert(4020);

        BTreeNode first = new BTreeNode(branchingFactor);
        first.insertNonFull(1234);

        BTreeNode second = new BTreeNode(branchingFactor);
        second.insertNonFull(4010);
        second.insertNonFull(4020);

        BTreeNode third = new BTreeNode(branchingFactor);
        third.insertNonFull(9012);

        BTreeNode root = tree.getRoot();

        assertThat(root.getKeys(), contains(3456, 5678));
        assertThat(root.getChildNodes(), contains(first, second, third));
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