package org.kata;

import org.junit.Test;
import org.kata.BTreeOfIntegers.BTreeNode;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
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
        BTreeNode leafOne = makeNode(keys(10), children());
        BTreeNode leafTwo = makeNode(keys(30), children());
        BTreeNode leafThree = makeNode(keys(70, 80, 90), children());

        BTreeNode tree = makeNode(keys(40), children(
                makeNode(keys(20), children(leafOne, leafTwo)),
                makeNode(keys(60), children(leafThree))
                )
        );

        assertThat(tree.getAllLeaves(), hasItems(leafOne, leafTwo, leafThree));
    }

    @Test
    public void canFindHeightForTheGivenLeaf() throws Exception {
        BTreeNode leafOne = makeNode(keys(10), children());
        BTreeNode leafTwo = makeNode(keys(30), children());
        BTreeNode leafThree = makeNode(keys(70, 80, 90), children());
        BTreeNode internalNode = makeNode(keys(20), children(leafOne, leafTwo));

        BTreeNode tree = makeNode(keys(40), children(
                internalNode,
                makeNode(keys(60), children(
                        makeNode(keys(50), children()),
                        leafThree))
                )
        );

        assertThat(tree.getDistanceTo(leafThree), is(2));
        assertThat(tree.getDistanceTo(internalNode), is(1));
        assertThat(tree.getDistanceTo(tree), is(0));
    }

    @Test
    public void canFindHeightForLeafsWhenThereAreDuplicateKeys() throws Exception {
        BTreeNode leafToFind = makeNode(keys(100), children());

        BTreeNode tree = makeNode(keys(100), children(
                makeNode(keys(100), children(
                        makeNode(keys(99), children()),
                        makeNode(keys(100, 100), children()))),
                makeNode(keys(100), children(
                        leafToFind,
                        makeNode(keys(101), children())
                )))
        );

        assertThat(tree.getDistanceTo(leafToFind), is(2));
    }

    @Test
    public void canDetectIfKeysOfNodeAreWithinRangeOrNot() throws Exception {
        BTreeNode leftNode = makeNode(keys(1000), children());
        BTreeNode rightNode = makeNode(keys(2000), children());

        BTreeNode wrongSubTree = makeNode(keys(3500), children(
                makeNode(keys(3600), children()),
                makeNode(keys(3700), children())
        ));

        BTreeNode goodSubTree = makeNode(keys(1500), children(
                leftNode, rightNode));

        assertThat(leftNode.keysAreWithinRange(MIN_VALUE, 1234), is(true));
        assertThat(rightNode.keysAreWithinRange(1234, MAX_VALUE), is(true));

        assertThat(leftNode.keysAreWithinRange(MIN_VALUE, 900), is(false));
        assertThat(leftNode.keysAreWithinRange(1100, MAX_VALUE), is(false));
        assertThat(rightNode.keysAreWithinRange(1500, 1700), is(false));
        assertThat(rightNode.keysAreWithinRange(2100, 2200), is(false));

        assertThat(wrongSubTree.keysAreWithinRange(3500, MAX_VALUE), is(false));
        assertThat(goodSubTree.keysAreWithinRange(MIN_VALUE, 3000), is(true));
    }
}