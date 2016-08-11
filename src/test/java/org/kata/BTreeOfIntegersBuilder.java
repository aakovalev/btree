package org.kata;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.kata.BTreeOfIntegers.MIN_BRANCHING_FACTOR;

public class BTreeOfIntegersBuilder {

    private int branchFactor = MIN_BRANCHING_FACTOR;
    private List<Integer> keys = new ArrayList<>();

    public static BTreeOfIntegersBuilder bTree() {
        return new BTreeOfIntegersBuilder();
    }

    public BTreeOfIntegersBuilder withBranchFactor(int branchFactor) {
        this.branchFactor = branchFactor;
        return this;
    }

    public BTreeOfIntegersBuilder withKeys(Integer... keys) {
        this.keys.addAll(asList(keys));
        return this;
    }

    public BTreeOfIntegers build() {
        BTreeOfIntegers tree = new BTreeOfIntegers(branchFactor);
        keys.stream().forEach(tree::insert);
        return tree;
    }
}