package org.kata;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BTreeNodeTests {

    @Test(expected = IllegalArgumentException.class)
    public void branchingFactorMustBeGreaterThanTwo() throws Exception {
        new BTreeNode(1);
    }

    @Test
    public void keysWithinNodeShouldBeOrderedAsc() throws Exception {
        BTreeNode node = new BTreeNode(5);
        node.insert(56);
        node.insert(98);
        node.insert(34);
        node.insert(12);

        assertThat(node.getKeys(), contains(12, 34, 56, 98));
    }

    @Test
    public void canAddOneKeyToEmptyNode() throws Exception {
        int key = 1234;
        BTreeNode node = new BTreeNode(3);
        node.insert(key);

        assertTrue("Node should contain just added key", node.contains(key));
        assertFalse("Node should not contain key that is not yet added",
                node.contains(4567));
    }

    @Test
    public void canRemoveKeyAddedPreviously() throws Exception {
        int key = 1234;
        BTreeNode node = new BTreeNode(3);
        node.remove(key);

        assertFalse("Node should not contain removed key", node.contains(key));
    }

    @Test
    public void givenLeafNodeIsNotFullWhenKeyIsAddedThenTheKeyIsAddedIntoTheLeaf()
            throws Exception
    {
        BTreeNode node = new BTreeNode(2);
        int newKey = 5678;
        node.insert(newKey);

        assertTrue(node.contains(newKey));
        assertTrue(node.isLeaf());
    }

    @Test
    public void givenLeafNodeIsFullWhenKeyIsAddedThenNodeIsSplitted()
            throws Exception
    {
        int branchFactor = 2;
        BTreeNode node = new BTreeNode(branchFactor);
        node.insert(1234);
        node.insert(5678);
        node.insert(9012);
        // next operation should cause node split
        BTreeNode resultNode = node.insert(3456);

        BTreeNode expectedNode = new BTreeNode(branchFactor);
        expectedNode.insert(5678);
        assertThat(resultNode, is(expectedNode));

        BTreeNode expectedLeftNode = new BTreeNode(branchFactor);
        expectedLeftNode.insert(1234);
        expectedLeftNode.insert(3456);

        BTreeNode expectedRightNode = new BTreeNode(branchFactor);
        expectedRightNode.insert(9012);

        assertThat(resultNode.getChildNodes(),
                contains(expectedLeftNode, expectedRightNode));
    }
}