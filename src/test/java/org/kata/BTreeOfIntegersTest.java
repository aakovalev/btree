package org.kata;

import org.junit.Test;
import org.kata.BTreeOfIntegers.BTreeNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.IntStream;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.Integer.MIN_VALUE;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.core.IsCollectionContaining.hasItem;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kata.BTreeTestUtils.*;

public class BTreeOfIntegersTest {
    @Test
    public void canAddOneKeyToEmptyTree() throws Exception {
        int minDegree = 3;
        BTreeOfIntegers tree = new BTreeOfIntegers(minDegree);
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
        tree.delete(testKey);

        assertFalse("Tree should not contain removed testKey",
                tree.contains(testKey));
    }

    @Test
    public void whenRootLeafNodeIsFullAndKeyIsAdded()
            throws Exception
    {
        int minDegree = 2;
        WhiteBoxTestableBTreeOfIntegers tree =
                new WhiteBoxTestableBTreeOfIntegers(minDegree);
        BTreeNode originalTree =
                makeNode(minDegree, keys(1234, 5678, 9012), children());
        tree.setRoot(originalTree);

        tree.insert(3456);

        BTreeNode expectedTree =
                makeNode(minDegree, keys(5678),
                        children(
                                makeNode(minDegree, keys(1234, 3456),
                                        children()),
                                makeNode(minDegree, keys(9012), children())
                        )
                );

        BTreeNode actualTree = tree.getRoot();
        assertThat(actualTree, is(expectedTree));
    }

    @Test
    public void whenNonRootNoneInternalNodeIsFullAndNewKeyIsAdded()
            throws Exception
    {
        int minDegree = 2;
        WhiteBoxTestableBTreeOfIntegers tree =
                new WhiteBoxTestableBTreeOfIntegers(minDegree);
        BTreeNode initialTree = makeNode(minDegree, keys(5678), children(
                makeNode(minDegree, keys(1234, 3456, 4010), children()),
                makeNode(minDegree, keys(9012), children())
                )
        );
        tree.setRoot(initialTree);

        tree.insert(4020);

        BTreeNode expectedTree =
                makeNode(minDegree, keys(3456, 5678), children(
                        makeNode(minDegree, keys(1234), children()),
                        makeNode(minDegree, keys(4010, 4020), children()),
                        makeNode(minDegree, keys(9012), children())
                        )
                );

        BTreeNode actualTree = tree.getRoot();
        assertThat(actualTree, is(expectedTree));
    }

    @Test
    public void whenInternalNodeIsFullAndNewKeyIsAdded() throws Exception {
        int minDegree = 2;
        WhiteBoxTestableBTreeOfIntegers tree =
                new WhiteBoxTestableBTreeOfIntegers(minDegree);
        BTreeNode initialTree = makeNode(minDegree, keys(4020), children(
                makeNode(minDegree, keys(3456), children(
                        makeNode(minDegree, keys(1234), children()),
                        makeNode(minDegree, keys(4010), children())
                        )
                ),
                makeNode(minDegree, keys(4040, 4060, 5678), children(
                        makeNode(minDegree, keys(4030), children()),
                        makeNode(minDegree, keys(4050), children()),
                        makeNode(minDegree, keys(4070, 4080), children()),
                        makeNode(minDegree, keys(9012), children())
                        )
                ))
        );
        tree.setRoot(initialTree);

        tree.insert(4090);

        BTreeNode expectedTree = makeNode(minDegree, keys(4020, 4060),
                children(
                        makeNode(minDegree, keys(3456), children(
                                makeNode(minDegree, keys(1234), children()),
                                makeNode(minDegree, keys(4010), children())
                                )
                        ),
                        makeNode(minDegree, keys(4040), children(
                                makeNode(minDegree, keys(4030), children()),
                                makeNode(minDegree, keys(4050), children())
                                )
                        ),
                        makeNode(minDegree, keys(5678), children(
                                makeNode(minDegree, keys(4070, 4080, 4090),
                                        children()),
                                makeNode(minDegree, keys(9012), children())
                                )
                        )
                ));

        BTreeNode actualTree = tree.getRoot();
        assertThat(actualTree, is(expectedTree));
    }

    @Test
    public void shouldFindKeyIfExistsInTree() throws Exception {
        int minDegree = 2;
        BTreeNode testTree =
                makeNode(minDegree, keys(4020), children(
                        makeNode(minDegree, keys(3456), children(
                                makeNode(minDegree, keys(1234), children()),
                                makeNode(minDegree, keys(4010), children())
                                )
                        ),
                        makeNode(minDegree, keys(5678), children(
                                makeNode(minDegree, keys(4030, 4040, 4050),
                                        children()),
                                makeNode(minDegree, keys(9012), children())))
                        )
                );

        assertTrue(testTree.contains(4020));
        assertTrue(testTree.contains(9012));
        assertTrue(testTree.contains(3456));
        assertTrue(testTree.contains(4030));

        assertFalse(testTree.contains(9099));
        assertFalse(testTree.contains(MIN_VALUE));
        assertFalse(testTree.contains(MAX_VALUE));
        assertFalse(testTree.contains(4031));
        assertFalse(testTree.contains(4021));
    }

    @Test
    public void allowsToInsertDuplicateKeys() throws Exception {
        int minDegree = 2;
        WhiteBoxTestableBTreeOfIntegers tree =
                new WhiteBoxTestableBTreeOfIntegers(minDegree);
        insertKeysIntoTree(keys(100, 100, 100, 100), tree);

        BTreeNode expectedTree = makeNode(minDegree, keys(100), children(
                makeNode(minDegree, keys(100, 100), children()),
                makeNode(minDegree, keys(100), children())
                )
        );

        BTreeNode actualTree = tree.getRoot();
        assertThat(actualTree, is(expectedTree));
    }

    @Test
    public void eachNodeShouldContainNotLessThanMinDegreeMinusOneKeysExceptTheRoot()
            throws Exception
    {
        WhiteBoxTestableBTreeOfIntegers tree =
                WhiteBoxTestableBTreeOfIntegers.generateRandomBTree();

        tree.getAllNonRootNodes().stream().forEach(
                node -> {
                    int minDegree = node.getMinDegree();
                    int keyCount = node.getKeys().size();
                    assertTrue(
                            format("Each node with min degree %d (except root) " +
                                            "should contain not less than %d keys, " +
                                            "but was %d",
                                    minDegree, minDegree - 1, keyCount),
                            keyCount >= minDegree - 1);
                });
    }

    @Test
    public void eachNodeShouldContainNotMoreThanMinDegreeByTwoMinusOneKeys()
            throws Exception
    {
        WhiteBoxTestableBTreeOfIntegers tree =
                WhiteBoxTestableBTreeOfIntegers.generateRandomBTree();

        tree.getAllNodes().stream().forEach(
                node -> {
                    int minDegree = node.getMinDegree();
                    int keyCount = node.getKeys().size();
                    assertTrue(
                            format("Each node with min degree = %d should contain " +
                                            "not more than than %d keys, but was %d",
                                    minDegree, 2 * minDegree - 1, keyCount),
                            keyCount <= 2 * minDegree - 1);
                });
    }

    @Test
    public void keysWithinEachNodeShouldBeOrderedAsc() throws Exception {
        WhiteBoxTestableBTreeOfIntegers tree =
                WhiteBoxTestableBTreeOfIntegers.generateRandomBTree();

        tree.getAllNodes().stream().forEach(
                node -> {
                    List<Integer> keysInExpectedOrder =
                            new ArrayList<>(node.getKeys());
                    Collections.sort(keysInExpectedOrder);

                    assertThat(node.getKeys(),
                            contains(keysInExpectedOrder.toArray()));
                }
        );
    }

    @Test
    public void keyValuesWithinEachNodeAreWithinRequiredInterval()
            throws Exception
    {
        WhiteBoxTestableBTreeOfIntegers tree =
                WhiteBoxTestableBTreeOfIntegers.generateRandomBTree();

        assertTrue(tree.getRoot().keysAreWithinRange(MIN_VALUE, MAX_VALUE));
    }

    @Test
    public void eachLeafIsAtTheSameDistanceFromRoot() throws Exception {
        WhiteBoxTestableBTreeOfIntegers tree =
                WhiteBoxTestableBTreeOfIntegers.generateRandomBTree();

        BTreeNode oneOfLeafs = tree.getAllLeaves().get(0);
        int distanceFromRoot = tree.getDistanceFromRootTo(oneOfLeafs);
        tree.getAllLeaves().stream().forEach(leaf -> assertThat(
                tree.getDistanceFromRootTo(leaf), is(distanceFromRoot)));
    }

    @Test (expected = NoSuchElementException.class)
    public void whenNonExistingKeyRequestedToBeRemovedThrowAnException()
            throws Exception
    {
        int minDegree = 2;
        BTreeNode initialTree = makeNode(keys(1, 2, 3), children());
        WhiteBoxTestableBTreeOfIntegers tree =
                new WhiteBoxTestableBTreeOfIntegers(minDegree);
        tree.setRoot(initialTree);

        tree.delete(42);
    }

    @Test
    public void canDeleteKeyFromRootWhenItIsLeaf() throws Exception {
        int keyToDelete = 4;
        BTreeNode rootAndLeaf = makeNode(keys(3, keyToDelete, 5), children());
        int minDegree = 2;
        WhiteBoxTestableBTreeOfIntegers tree =
                new WhiteBoxTestableBTreeOfIntegers(minDegree);
        tree.setRoot(rootAndLeaf);
        tree.delete(keyToDelete);

        assertThat(rootAndLeaf.getKeys(), not(hasItem(keyToDelete)));
    }

    @Test
    public void canDeleteKeyFromInternalLeafThatHasAtLeastMinimumDegreeOfNodes()
            throws Exception
    {
        int minDegree = 2;
        BTreeNode initialTree = makeNode(keys(3), children(
                makeNode(keys(1, 2), children()),
                makeNode(keys(4, 5), children())
                )
        );
        WhiteBoxTestableBTreeOfIntegers tree =
                new WhiteBoxTestableBTreeOfIntegers(minDegree);
        tree.setRoot(initialTree);

        tree.delete(2);

        BTreeNode expectedTree = makeNode(keys(3), children(
                makeNode(keys(1), children()),
                makeNode(keys(4, 5), children())
                )
        );
        assertThat(tree.getRoot(), is(expectedTree));
    }

    //@Test
    public void smokeCheckOfPopulatingDecentSizeTree() throws Exception {
        BTreeOfIntegers tree = new BTreeOfIntegers(10);
        IntStream.range(0, 1000000).forEach(tree::insert);

        assertTrue(tree.contains(123456));
    }
}