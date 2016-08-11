package org.kata;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
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
        assertFalse("Tree should not contain key that is not yet added",
                tree.contains(4567));
    }

    @Test
    public void canRemoveKeyAddedPreviously() throws Exception {
        int key = 1234;
        BTreeOfIntegers tree = bTree().withBranchFactor(3).withKeys(key).build();
        tree.remove(key);

        assertFalse("Tree should not contain removed key", tree.contains(key));
    }

    @Test
    public void rootShouldNotHaveChildNodesTilNumOfKeysLessThanBranchFactor()
            throws Exception
    {
        BTreeOfIntegers tree = bTree()
                .withBranchFactor(3)
                .withKeys(1234, 5678)
                .build();

        assertThat(tree.getChildNodes().size(), is(0));
    }

    @Test
    public void childNodeShouldAppearOnceNumOfKeysEqualsOrExceedsBranchFactor()
            throws Exception
    {
        BTreeOfIntegers tree = bTree()
                .withBranchFactor(3)
                .withKeys(1234, 5678, 9012)
                .build();

        assertThat(tree.getChildNodes().size(), greaterThan(0));
    }

    @Test
    public void keysWithinNodeShouldBeOrdered() throws Exception {
        BTreeOfIntegers tree = bTree()
                .withBranchFactor(5)
                .withKeys(98, 56, 12, 34)
                .build();

        assertThat(tree.getKeys(), contains(12, 34, 56, 98));
    }
}