package org.kata;

import org.junit.Test;

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
}