package org.kata;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BTreeOfIntegersContractTest {
    @Test(expected = IllegalArgumentException.class)
    public void branchingFactorMustBeGreaterThanTwo() throws Exception {
        new BTreeOfIntegers(1);
    }

    @Test
    public void canAddOneKeyToEmptyTree() throws Exception {
        BTreeOfIntegers tree = new BTreeOfIntegers(3);
        int key = 1234;
        tree.add(key);
        assertTrue("Tree should contain just added key", tree.contains(key));
        assertFalse("Tree should not contain key that is not yet added",
                tree.contains(4567));
    }

    @Test
    public void canRemoveKeyAddedPreviously() throws Exception {
        BTreeOfIntegers tree = new BTreeOfIntegers(3);
        int key = 1234;
        tree.add(key);
        tree.remove(key);
        assertFalse("Tree should not contain removed key", tree.contains(key));
    }

    @Test
    public void rootShouldNotHaveChildNodesUntilNumberOfKeysLessThanBranchingFactor()
            throws Exception
    {
        BTreeOfIntegers tree = new BTreeOfIntegers(3);
        tree.add(1234);
        tree.add(5678);
        assertThat(tree.getChildNodes().size(), is(0));
    }

    @Test
    public void rootShouldHaveChildNodesWhenNumberOfKeysEqualsOrExceedBranchingFactor() throws Exception {
        BTreeOfIntegers tree = new BTreeOfIntegers(3);
        tree.add(1234);
        tree.add(5678);
        tree.add(9012);
        assertThat(tree.getChildNodes().size(), greaterThan(0));
    }
}