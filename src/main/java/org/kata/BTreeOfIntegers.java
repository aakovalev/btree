package org.kata;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.logging.Logger;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.builder.ToStringBuilder.reflectionToString;
import static org.apache.commons.lang3.builder.ToStringStyle.JSON_STYLE;

/**
 * <code>BTreeOfIntegers</code> is an implementation of B-tree data structure.
 * In this implementation the only supported type of keys are int values.
 * <p>
 * Note: This implementation is NOT thread-safe, so using in multithreading
 * context will require additional synchronization
 * </p>
 *
 * @author kovalev.aleksey@gmail.com
 */
public class BTreeOfIntegers {
    protected BTreeNode root;

    /**
     * Creates <code>BTreeOfIntegers</code> data structure
     *
     * @param minDegree a parameter of B-tree that controls number of keys per
     *                  tree node and child nodes.
     */
    public BTreeOfIntegers(int minDegree) {
        this.root = new BTreeNode(minDegree);
    }

    /**
     * Inserts key into B-tree
     *
     * @param key a key to insert
     */
    public void insert(int key) {
        if (root.isFull()) {
            BTreeNode newRoot = new BTreeNode(root.getMinDegree());
            newRoot.addChild(0, root.getHandle());
            newRoot.splitChild(root);
            root = newRoot;
        }
        root.insertNonFull(key);
    }

    /**
     * Checks if the specified key is in B-tree
     *
     * @param key a key to check
     * @return <code>true</code> if key is in this <code>BTreeOfIntegers</code>
     * or <code>false</code> if otherwise
     */
    public boolean contains(int key) {
        return this.root.contains(key);
    }

    /**
     * Deletes the specified key from <code>BTreeOfIntegers</code>
     * <p>
     * Note: The implementation of this method is not yet completed!
     * </p>
     *
     * @param key a key to delete from <code>BTreeOfIntegers</code>
     * @throws NoSuchElementException if tree does not contain the key
     */
    public void delete(int key) {
        if (root.contains(key) && root.isLeaf()) {
            root.delete(key);
        }
        else {
            BTreeNode nodeContainingKey = root.findNodeContainingKey(key);
            nodeContainingKey.delete(key);
        }
    }


    /**
     * <code>BTreeNode</code> is implementation of node of B-tree data structure
     */
    static class BTreeNode implements Serializable {
        // lowest min degree must be greater than 2, otherwise a tree is
        // degenerated to a list
        public static final int LOWEST_MIN_DEGREE = 2;

        private int minDegree;
        private List<Integer> keys = new ArrayList<>();
        private List<Long> childrenHandles = new ArrayList<>();
        private static Storage<BTreeNode> storage;

        private static final Logger LOG = Logger.getLogger(
                BTreeNode.class.getName());

        static {
            try {
                storage = new CachedStorage<>(new FileBasedStorage<>());
            } catch (IOException e) {
                LOG.severe("Unable to create storage for B-tree node entries!");
            }
        }

        private Long handle;

        public BTreeNode(int minDegree) {
            if (minDegree < LOWEST_MIN_DEGREE) {
                throw new IllegalArgumentException(
                        format("Min degree for tree node should be greater than " +
                                "or equals to 2, but passed '%d'", minDegree));
            }
            this.minDegree = minDegree;
            saveOnDisk();
        }

        public boolean contains(int key) {
            int index = 0;
            while (index < keys.size() && key > keys.get(index)) {
                index++;
            }
            return keys.contains(key) ||
                    !isLeaf() && indexToNode(index).contains(key);
        }

        public void remove(int key) {
            keys.remove(valueOf(key));
        }

        public boolean isLeaf() {
            return childrenHandles.size() == 0;
        }

        public boolean isFull() {
            return keys.size() >= maxKeysPerNode();
        }

        public int getMinDegree() {
            return minDegree;
        }

        public void splitChild(BTreeNode child) {
            BTreeNode parent = this;
            int median = child.getMedianOfKeys();

            BTreeNode newSubNode = new BTreeNode(minDegree);
            child.moveHalfOfKeysTo(newSubNode);
            if (!child.isLeaf()) {
                child.moveHalfOfChildren(newSubNode);
            }

            parent.insertKey(median);
            int newSubNodePositionInParent = parent.getChildPosition(child) + 1;
            parent.addChild(newSubNodePositionInParent, newSubNode.getHandle());

            newSubNode.saveOnDisk();
            child.saveOnDisk();
            parent.saveOnDisk();
        }

        public void insertNonFull(int key) {
            if (isLeaf()) {
                insertKey(key);
                saveOnDisk();
            } else {
                Long nodeHandle = findChildNodeThatShouldContainKey(key);
                BTreeNode node = readFromDisk(nodeHandle);
                if (node.isFull()) {
                    splitChild(node);
                    insertNonFull(key);
                } else {
                    node.insertNonFull(key);
                }
            }
        }

        public void delete(int key) {
            if (isLeaf()) {
                keys.remove((Integer) key);
            }
            saveOnDisk();
        }

        public void saveOnDisk() {
            try {
                if (handle == null) {
                    handle = storage.create(this);
                } else {
                    storage.update(this, handle);
                }
            } catch (IOException e) {
                LOG.severe("Unable to store B-tree node in storage");
            }
        }

        public BTreeNode readFromDisk(long handle) {
            try {
                return storage.load(handle);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(
                        "Unable to read B-tree node from disk");
            }
        }

        @Override
        public int hashCode() {
            return new HashCodeBuilder()
                    .append(minDegree)
                    .append(keys)
                    .append(getChildren())
                    .toHashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (obj == this) {
                return true;
            }
            if (obj.getClass() != getClass()) {
                return false;
            }
            BTreeNode otherNode = (BTreeNode) obj;
            return new EqualsBuilder()
                    .append(minDegree, otherNode.minDegree)
                    .append(keys, otherNode.keys)
                    .append(getChildren(), otherNode.getChildren())
                    .isEquals();
        }

        @Override
        public String toString() {
            return reflectionToString(this, JSON_STYLE);
        }

        protected List<Integer> getKeys() {
            return unmodifiableList(keys);
        }

        protected List<Long> getChildHandles() {
            return unmodifiableList(childrenHandles);
        }

        protected List<BTreeNode> getAllDescendants() {
            List<BTreeNode> allDescendants = new ArrayList<>();
            if (!isLeaf()) {
                getChildHandles().stream().forEach(
                        childHandle -> {
                            BTreeNode child = readFromDisk(childHandle);
                            allDescendants.addAll(child.getAllDescendants());
                        }
                );
            }
            return allDescendants;
        }

        protected void addChild(int index, Long childNodeHandle) {
            this.childrenHandles.add(index, childNodeHandle);
        }

        protected List<BTreeNode> getAllLeaves() {
            List<BTreeNode> allLeaves = new ArrayList<>();
            if (isLeaf()) {
                allLeaves.add(this);
            } else {
                childrenHandles.stream().forEach(childHandle -> {
                    BTreeNode child = readFromDisk(childHandle);
                    allLeaves.addAll(child.getAllLeaves());
                });
            }
            return allLeaves;
        }

        protected int getDistanceTo(BTreeNode node) {
            return findPath(node).size();
        }

        private List<Long> findPath(BTreeNode targetNode) {
            List<Long> path = new ArrayList<>();
            if (targetNode.equals(this)) {
                return path;
            } else if (!isLeaf()) {
                if (childrenHandles.contains(targetNode.getHandle())) {
                    path.add(this.getHandle());
                } else {
                    int index = 0;
                    int key = targetNode.keys.get(0);
                    while (index < keys.size() && key > keys.get(index)) {
                        index++;
                    }
                    // handle case when there are same keys but in different children
                    while (index < childrenHandles.size() && indexToNode(index).contains(key)) {
                        BTreeNode child = indexToNode(index);
                        List<Long> pathFromChild = child.findPath(targetNode);
                        if (pathFromChild.size() > 0) {
                            path.add(child.getHandle());
                            path.addAll(pathFromChild);
                        }
                        index++;
                    }
                }
            }
            return path;
        }

        protected boolean keysAreWithinRange(int left, int right) {
            boolean valid = true;

            for (int k : getKeys()) {
                if (k < left || k > right) {
                    valid = false;
                    break;
                }
            }

            if (valid && !isLeaf()) {
                int currentChildIndex = 0;
                int maxChildIndex = getChildHandles().size() - 1;
                int maxKeyIndex = getKeys().size() - 1;

                for (Long childHandle : getChildHandles()) {
                    int newRightBound = currentChildIndex == 0 ?
                            getKeys().get(maxKeyIndex) : right;
                    int newLeftBound = currentChildIndex == maxChildIndex ?
                            getKeys().get(0) : left;
                    BTreeNode child = readFromDisk(childHandle);
                    if (!child.keysAreWithinRange(newLeftBound, newRightBound)) {
                        valid = false;
                        break;
                    }
                    currentChildIndex++;
                }
            }

            return valid;
        }

        protected Long getHandle() {
            return handle;
        }

        private void insertKeys(List<Integer> keysToInsert) {
            keysToInsert.stream().forEach(this::insertNonFull);
        }

        private void insertKey(int key) {
            int position = findPositionForKey(key);
            keys.add(position, key);
        }

        private void insertChildren(List<Long> childrenHandles) {
            this.childrenHandles.addAll(childrenHandles);
        }

        private void moveHalfOfKeysTo(BTreeNode destNode) {
            List<Integer> keysForNewSubNode = getKeysForNewSubNodeOnSplit();
            destNode.insertKeys(keysForNewSubNode);
            keys = new ArrayList<>(keys.subList(0, minDegree - 1));
        }

        private void moveHalfOfChildren(BTreeNode destNode) {
            List<Long> childrenForNewNode = getChildrenForNewSubNodeOnSplit();
            destNode.insertChildren(childrenForNewNode);
            childrenHandles.removeAll(childrenForNewNode);
        }

        private Integer getMedianOfKeys() {
            return keys.get(minDegree - 1);
        }

        private List<Integer> getKeysForNewSubNodeOnSplit() {
            return keys.subList(minDegree, keys.size());
        }

        private List<Long> getChildrenForNewSubNodeOnSplit() {
            return childrenHandles
                    .subList(childrenHandles.size() / 2, childrenHandles.size());
        }

        private Long findChildNodeThatShouldContainKey(int key) {
            int childNodeIndexByKey = findChildNodeIndexByKey(key);
            return childrenHandles.get(childNodeIndexByKey);
        }

        private int findChildNodeIndexByKey(int key) {
            return findPositionForKey(key);
        }

        private int findPositionForKey(int key) {
            int index = 0;
            while (index < keys.size() && key > keys.get(index)) {
                index++;
            }
            return index;
        }

        private int getChildPosition(BTreeNode child) {
            return childrenHandles.indexOf(child.getHandle());
        }

        private int maxKeysPerNode() {
            return 2 * minDegree - 1;
        }

        private BTreeNode indexToNode(int index) {
            Long childHandle = childrenHandles.get(index);
            return readFromDisk(childHandle);
        }

        private List<BTreeNode> getChildren() {
            List<BTreeNode> children = new ArrayList<>();
            if (!isLeaf()) {
                children.addAll(childrenHandles.stream()
                        .map(this::readFromDisk)
                        .collect(toList()));
            }
            return children;
        }

        private BTreeNode findNodeContainingKey(int key) {
            if (keys.contains(key)) {
                return this;
            }
            if (!isLeaf()) {
                Long nodeHandle = findChildNodeThatShouldContainKey(key);
                BTreeNode childToLookForKey = readFromDisk(nodeHandle);
                return childToLookForKey.findNodeContainingKey(key);
            }
            throw new NoSuchElementException(
                    format("There is no key %d in the tree", key));
        }
    }
}