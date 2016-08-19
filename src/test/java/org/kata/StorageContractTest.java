package org.kata;

import org.junit.Test;
import org.kata.BTreeOfIntegers.BTreeNode;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kata.BTreeTestUtils.children;
import static org.kata.BTreeTestUtils.keys;
import static org.kata.BTreeTestUtils.makeNode;

public abstract class StorageContractTest {
    @Test
    public void shouldStoreAndRestoreOneObject() throws Exception {
        int minDegree = 2;
        BTreeNode originalNode = makeNode(minDegree, keys(1234), children(
                makeNode(minDegree, keys(1000), children(
                        makeNode(minDegree, keys(900), children()),
                        makeNode(minDegree, keys(1100), children())
                )),
                makeNode(minDegree, keys(2000), children(
                        makeNode(minDegree, keys(1500), children()),
                        makeNode(minDegree, keys(2500), children())
                ))
        ));

        Storage<BTreeNode> storage = createStorage();
        long objHandle = storage.save(originalNode);

        BTreeNode restoredNode = storage.load(objHandle);

        assertThat(originalNode, is(restoredNode));
    }

    @Test
    public void shouldSaveAndRestoreMultipleObjects() throws Exception {
        Storage<BTreeNode> storage = createStorage();
        BTreeNode firstNode = new BTreeNode(10);
        BTreeNode secondNode = new BTreeNode(20);
        BTreeNode thirdNode = new BTreeNode(30);

        long firstHandle = storage.save(firstNode);
        long secondHandle = storage.save(secondNode);
        long thirdHandle = storage.save(thirdNode);

        BTreeNode firstNodeRestored = storage.load(firstHandle);
        BTreeNode thirdNodeRestored = storage.load(thirdHandle);
        BTreeNode secondNodeRestored = storage.load(secondHandle);

        assertThat(firstNodeRestored, is(firstNode));
        assertThat(secondNodeRestored, is(secondNode));
        assertThat(thirdNodeRestored, is(thirdNode));
    }

    abstract protected FileBasedStorage<BTreeNode> createStorage() throws java.io.IOException;
}