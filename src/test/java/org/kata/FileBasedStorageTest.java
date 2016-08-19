package org.kata;

import org.kata.BTreeOfIntegers.BTreeNode;

import java.io.IOException;

public class FileBasedStorageTest extends StorageContractTest {
    @Override
    protected FileBasedStorage<BTreeNode> createStorage() throws IOException {
        return new FileBasedStorage<>();
    }
}