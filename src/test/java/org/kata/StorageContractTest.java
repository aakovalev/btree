package org.kata;

import org.junit.Before;
import org.junit.Test;
import org.kata.BTreeOfIntegers.BTreeNode;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.kata.BTreeTestUtils.children;
import static org.kata.BTreeTestUtils.keys;
import static org.kata.BTreeTestUtils.makeNode;

public abstract class StorageContractTest {
    protected Storage<BTreeNode> storage;

    @Before
    public void setUp() throws Exception {
        storage = createStorage();
    }

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
        long objHandle = storage.create(originalNode);

        BTreeNode restoredNode = storage.load(objHandle);

        assertThat(originalNode, is(restoredNode));
    }

    @Test
    public void shouldSaveAndRestoreMultipleObjects() throws Exception {
        Storage<BTreeNode> storage = createStorage();
        BTreeNode firstNode = new BTreeNode(10);
        BTreeNode secondNode = new BTreeNode(20);
        BTreeNode thirdNode = new BTreeNode(30);

        long firstHandle = storage.create(firstNode);
        long secondHandle = storage.create(secondNode);
        long thirdHandle = storage.create(thirdNode);

        BTreeNode firstNodeRestored = storage.load(firstHandle);
        BTreeNode thirdNodeRestored = storage.load(thirdHandle);
        BTreeNode secondNodeRestored = storage.load(secondHandle);

        assertThat(firstNodeRestored, is(firstNode));
        assertThat(secondNodeRestored, is(secondNode));
        assertThat(thirdNodeRestored, is(thirdNode));
    }

    @Test
    public void shouldUpdateObjectDataInTheStorage() throws Exception {
        BTreeNode node = new BTreeNode(10);
        long handle = storage.create(node);

        node.addChild(0, new BTreeNode(20).getHandle());
        storage.update(node, handle);
        BTreeNode restoredNode = storage.load(handle);

        assertThat(restoredNode, is(node));
    }

    abstract protected FileBasedStorage<BTreeNode> createStorage() throws IOException;
}