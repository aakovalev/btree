package org.kata;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BTreeNodeTest {

    @Test(expected = IllegalArgumentException.class)
    public void branchingFactorMustBeGreaterThanTwo() throws Exception {
        new BTreeNode(1);
    }

    @Test
    public void canAddOneKeyToEmptyNode() throws Exception {
        int key = 1234;
        BTreeNode node = new BTreeNode(3);
        node.insertNonFull(key);

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
    public void keysWithinNodeShouldBeOrderedAsc() throws Exception {
        BTreeNode node = new BTreeNode(5);
        node.insertNonFull(56);
        node.insertNonFull(98);
        node.insertNonFull(34);
        node.insertNonFull(12);

        assertThat(node.getKeys(), contains(12, 34, 56, 98));
    }

    @Test
    public void whenLeafNodeIsNotFullAndKeyIsAddedThenTheKeyIsAddedIntoTheLeaf()
            throws Exception
    {
        BTreeNode node = new BTreeNode(2);
        int newKey = 5678;
        node.insertNonFull(newKey);

        assertTrue(node.contains(newKey));
        assertTrue(node.isLeaf());
    }
}