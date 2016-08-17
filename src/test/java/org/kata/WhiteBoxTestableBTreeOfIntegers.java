package org.kata;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.kata.BTreeNode.LOWEST_MIN_DEGREE;

public class WhiteBoxTestableBTreeOfIntegers extends BTreeOfIntegers {
    private final static Random RND = new Random();

    // limit highest min degree for test purpose
    private final static int HIGHEST_MIN_DEGREE = 10;

    // limit keys / min degree ratio for test purpose
    private final static int KEY_NUMBER_MIN_DEGREE_RATIO = 50;

    public static int generateRandomDegree() {
        return RND.nextInt(HIGHEST_MIN_DEGREE) + LOWEST_MIN_DEGREE;
    }

    public static WhiteBoxTestableBTreeOfIntegers generateRandomBTree()
    {
        WhiteBoxTestableBTreeOfIntegers generatedTree =
                new WhiteBoxTestableBTreeOfIntegers(generateRandomDegree());
        populateWithKeys(generatedTree);
        return generatedTree;
    }

    private static void populateWithKeys(
            WhiteBoxTestableBTreeOfIntegers tree)
    {
        int numberOfKeysToGenerate =
                tree.getMinDegree() * KEY_NUMBER_MIN_DEGREE_RATIO;
        RND.ints().limit(numberOfKeysToGenerate).forEach(tree::insert);
    }

    public WhiteBoxTestableBTreeOfIntegers(int minDegree) {
        super(minDegree);
    }

    public BTreeNode getRoot() {
        return root;
    }

    public int getMinDegree() {
        return root.getMinDegree();
    }

    public List<BTreeNode> getAllNonRootNodes() {
        return root.getAllDescendants();
    }

    public List<BTreeNode> getAllNodes() {
        List<BTreeNode> allNodes = new ArrayList<>();
        allNodes.add(root);
        allNodes.addAll(root.getAllDescendants());
        return allNodes;
    }
}