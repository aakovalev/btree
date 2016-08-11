package org.kata;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.valueOf;
import static java.lang.String.format;
import static java.util.Collections.binarySearch;
import static java.util.Collections.unmodifiableList;

public class BTreeOfIntegers {
    public static final int MIN_BRANCHING_FACTOR = 2;
    private int branchingFactor;

    private List<Integer> keys = new ArrayList<>();
    private List<BTreeOfIntegers> childNodes = new ArrayList<>();

    public BTreeOfIntegers(int branchingFactor) {
        if (branchingFactor < MIN_BRANCHING_FACTOR) {
            throw new IllegalArgumentException(
                    format("Branching Factor should be greater than " +
                            "or equals to 2, but passed '%d'", branchingFactor));
        }
        this.branchingFactor = branchingFactor;
    }

    public void insert(int keyToInsert) {
        if (keys.size() + 1 < branchingFactor) {
            int position = findPositionToInsertKey(keyToInsert);
            keys.add(position, keyToInsert);
        }
        else {
            childNodes.add(new BTreeOfIntegers(branchingFactor));
        }
    }

    private int findPositionToInsertKey(int keyToInsert) {
        return ~binarySearch(keys, keyToInsert);
    }

    public boolean contains(int key) {
        return keys.contains(key);
    }

    public void remove(int key) {
        keys.remove(valueOf(key));
    }

    public List<BTreeOfIntegers> getChildNodes() {
        return childNodes;
    }

    public List<Integer> getKeys() {
        return unmodifiableList(keys);
    }
}