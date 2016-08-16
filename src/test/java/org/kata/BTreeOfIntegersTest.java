package org.kata;

import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BTreeOfIntegersTest {
    @Test
    public void canAddOneKeyToEmptyTree() throws Exception {
        int branchFactor = 3;
        BTreeOfIntegers tree = new BTreeOfIntegers(branchFactor);
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

        BTreeNode expectedTree =
                makeNode(branchFactor, keys(5678),
                        children(
                                makeNode(branchFactor, keys(1234, 3456),
                                        children()),
                                makeNode(branchFactor, keys(9012), children())
                        )
                );

        BTreeNode actualTree = tree.getRoot();
        assertThat(actualTree, is(expectedTree));
    }

    @Test
    public void whenNonRootNoneInternalNodeIsFullAndNewKeyIsAdded()
            throws Exception
    {
        int branchFactor = 2;
        WhiteBoxTestableBTreeOnIntegers tree =
                new WhiteBoxTestableBTreeOnIntegers(branchFactor);
        insertKeysIntoTree(keys(1234, 5678, 9012, 3456, 4010, 4020), tree);

        BTreeNode expectedTree =
                makeNode(branchFactor, keys(3456, 5678),
                        children(
                                makeNode(branchFactor, keys(1234),
                                        children()),
                                makeNode(branchFactor, keys(4010, 4020),
                                        children()),
                                makeNode(branchFactor, keys(9012),
                                        children())
                        )
                );

        BTreeNode actualTree = tree.getRoot();
        assertThat(actualTree, is(expectedTree));
    }

    @Test
    public void whenInternalNodeIsFullAndNewKeyIsAdded() throws Exception {
        int branchFactor = 2;
        WhiteBoxTestableBTreeOnIntegers tree =
                new WhiteBoxTestableBTreeOnIntegers(branchFactor);
        insertKeysIntoTree(
                keys(1234, 5678, 9012, 3456, 4010, 4020, 4030, 4040,
                        4050, 4060, 4070, 4080, 4090), tree);

        BTreeNode expectedTree = makeNode(branchFactor, keys(4020, 4060),
                children(
                        makeNode(branchFactor, keys(3456),
                                children(
                                        makeNode(branchFactor,
                                                keys(1234), children()),
                                        makeNode(branchFactor,
                                                keys(4010), children())
                                )
                        ),
                        makeNode(branchFactor, keys(4040),
                                children(
                                        makeNode(branchFactor,
                                                keys(4030), children()),
                                        makeNode(branchFactor,
                                                keys(4050), children())
                                )
                        ),
                        makeNode(branchFactor, keys(5678),
                                children(
                                        makeNode(
                                                branchFactor,
                                                keys(4070, 4080, 4090),
                                                children()),
                                        makeNode(
                                                branchFactor,
                                                keys(9012),
                                                children())
                                ))
                ));

        BTreeNode actualTree = tree.getRoot();
        assertThat(actualTree, is(expectedTree));
    }

    @Test
    public void shouldFindKeyIfExistsInTree() throws Exception {
        int branchFactor = 2;
        BTreeNode testTree =
                makeNode(branchFactor, keys(4020),
                        children(
                                makeNode(branchFactor, keys(3456),
                                        children(
                                                makeNode(branchFactor,
                                                        keys(1234),
                                                        children()),
                                                makeNode(branchFactor,
                                                        keys(4010),
                                                        children())
                                        )
                                ),
                                makeNode(branchFactor, keys(5678),
                                        children(
                                                makeNode(branchFactor,
                                                        keys(4030, 4040, 4050),
                                                        children()),
                                                makeNode(branchFactor,
                                                        keys(9012),
                                                        children())
                                        ))
                        )
                );
        assertTrue(testTree.contains(4020));
        assertTrue(testTree.contains(9012));
        assertTrue(testTree.contains(3456));
        assertTrue(testTree.contains(4030));
    }

    private BTreeNode makeNode(
            int branchingFactor, List<Integer> keys, List<BTreeNode> children)
    {
        BTreeNode expectedTree = new BTreeNode(branchingFactor);
        insertKeysIntoNode(keys, expectedTree);
        insertChildrenIntoNode(children, expectedTree);
        return expectedTree;
    }

    private void insertChildrenIntoNode(
            List<BTreeNode> children, BTreeNode node)
    {
        int i = 0;
        for (BTreeNode child : children) {
            node.addChild(i++, child);
        }
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

    private List<BTreeNode> children(BTreeNode... child) {
        return asList(child);
    }

    private static class WhiteBoxTestableBTreeOnIntegers
            extends BTreeOfIntegers {
        public WhiteBoxTestableBTreeOnIntegers(int branchingFactor) {
            super(branchingFactor);
        }

        public BTreeNode getRoot() {
            return root;
        }
    }
}