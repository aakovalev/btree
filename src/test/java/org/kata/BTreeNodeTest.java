package org.kata;

import org.junit.Test;
import org.kata.BTreeOfIntegers.BTreeNode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kata.BTreeTestUtils.*;

public class BTreeNodeTest {

    @Test(expected = IllegalArgumentException.class)
    public void minDegreeMustBeGreaterThanTwo() throws Exception {
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

    @Test
    public void canFindAllLeavesOfTheGivenNode() throws Exception {
        int minDegree = 2;

        BTreeNode leafOne = makeNode(minDegree, keys(10), children());
        BTreeNode leafTwo = makeNode(minDegree, keys(30), children());
        BTreeNode leafThree = makeNode(minDegree, keys(70, 80, 90), children());

        BTreeNode tree = makeNode(minDegree, keys(40),
                children(
                        makeNode(minDegree, keys(20), children(leafOne, leafTwo)),
                        makeNode(minDegree, keys(60), children(leafThree))
                )
        );

        assertThat(tree.getAllLeaves(), hasItems(leafOne, leafTwo, leafThree));
    }

    @Test
    public void canFindHeightForTheGivenLeaf() throws Exception {
        int minDegree = 2;

        BTreeNode leafOne = makeNode(minDegree, keys(10), children());
        BTreeNode leafTwo = makeNode(minDegree, keys(30), children());
        BTreeNode leafThree = makeNode(minDegree, keys(70, 80, 90), children());
        BTreeNode internalNode = makeNode(minDegree, keys(20), children(leafOne, leafTwo));

        BTreeNode tree = makeNode(minDegree, keys(40),
                children(
                        internalNode,
                        makeNode(minDegree, keys(60), children(leafThree))
                )
        );

        assertThat(tree.getDistanceTo(leafThree), is(2));
        assertThat(tree.getDistanceTo(internalNode), is(1));
        assertThat(tree.getDistanceTo(tree), is(0));
    }
}