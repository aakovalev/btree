package org.kata;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kata.BTreeOfIntegersBuilder.bTree;

public class BTreeOfIntegersContractTest {

    @Test(expected = IllegalArgumentException.class)
    public void branchingFactorMustBeGreaterThanTwo() throws Exception {
        bTree().withBranchFactor(1).build();
    }

    @Test
    public void canAddOneKeyToEmptyTree() throws Exception {
        int key = 1234;
        BTreeOfIntegers tree = bTree().withBranchFactor(3).build();
        tree.insert(key);

        assertTrue("Tree should contain just added key", tree.contains(key));
        assertFalse("Tree should not contain key that is not yet added", tree.contains(4567));
    }

    @Test
    public void canRemoveKeyAddedPreviously() throws Exception {
        int key = 1234;
        BTreeOfIntegers tree = bTree().withBranchFactor(3).withKeys(key).build();
        tree.remove(key);

        assertFalse("Tree should not contain removed key", tree.contains(key));
    }

    @Test
    public void givenLeafIsNotFullWhenKeyIsAddedThenTheKeyIsAddedIntoTheLeaf()
            throws Exception
    {
        BTreeOfIntegers tree = bTree().withBranchFactor(3)
                .withKeys(1234)
                .build();
        int newKey = 5678;
        tree.insert(newKey);

        assertTrue(tree.contains(newKey));
        assertTrue(tree.isLeaf());
    }

    @Test
    public void keysWithinNodeShouldBeOrdered() throws Exception {
        BTreeOfIntegers tree = bTree().withBranchFactor(5)
                .withKeys(98, 56, 12, 34)
                .build();

        assertThat(tree.getKeys(), contains(12, 34, 56, 98));
    }

    @Test
    public void givenLeafIsFullWhenKeyIsAddedThenL()
            throws Exception
    {
        BTreeOfIntegers tree = bTree().withBranchFactor(3)
                .withKeys(1234, 5678)
                .build();
        tree.insert(9012);

        assertThat(tree.getChildNodes().size(), greaterThan(0));
    }
}