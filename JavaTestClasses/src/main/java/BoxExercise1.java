/*
** A "Unival" tree is a binary tree in which all the values are identical:
**   1
** 1   1
**
**   1
** 1   1
**    2
**
**    1
**  2   3
** 2 2 3 3
**
** 1. Write a program that determines whether a binary tree is Unival
**    Excepted result: True, False, False
** 2. Write a program that counts the NUMBER of Unival trees/subtrees
**    Excepted result: 3, 2, 6
*/
import helper.BinaryNode;

import java.util.ArrayList;
import java.util.List;

public class BoxExercise1 {

  public static boolean isUnivalTree(BinaryNode node) {
    return isUnivalTree(node, node.value);
  }

  // I think this recurses to the leaves and then works up
  private static boolean isUnivalTree(BinaryNode node, int value) {
    return node == null || node.value == value && isUnivalTree(node.left, value) && isUnivalTree(node.right, value);
  }

  // This is a standalone version with no sub-method
  private static boolean isUnivalTree2(BinaryNode node) {
    if (node == null) {
      return true;
    }
    if (node.left != null && node.left.value != node.value) {
      return false;
    }
    if (node.right != null && node.right.value != node.value) {
      return false;
    }
    return isUnivalTree(node.left) && isUnivalTree(node.right);
  }

  // I think this may be O(n log(n)) complexity; pretty sure it's more than O(n)
  public static int countUnivalSubtrees(BinaryNode node) {
    if (node == null) {
      return 0;
    }
    if (node.left == null && node.right == null) {
      return 1;
    }
    return (isUnivalTree(node) ? 1 : 0) + countUnivalSubtrees(node.left) + countUnivalSubtrees(node.right);
  }

  // I think this version is O(n)
  public static int countUnivalSubtrees2(BinaryNode node) {
    return countUnivalSubtrees2(node, new ArrayList<>());
  }

  private static int countUnivalSubtrees2(BinaryNode node, List<Boolean> univalSubtree) {
    if (node == null) {
      return 0;
    }
    ArrayList<Boolean> lTree = new ArrayList<>();
    ArrayList<Boolean> rTree = new ArrayList<>();
    int subtotal = countUnivalSubtrees2(node.left, lTree) + countUnivalSubtrees2(node.right, rTree);
    if ((node.left == null || (lTree.get(0) && node.left.value == node.value) &&
        (node.right == null || (rTree.get(0) && node.right.value == node.value)))) {
      univalSubtree.add(true);
      return 1 + subtotal;
    }
    univalSubtree.add(false);
    return subtotal;
  }

  public static int countUnivalSubtrees(BinaryNode node, int value) {
    return 0;
  }

  public static void main(String[] args) {
    BinaryNode testTree1 = new BinaryNode(1, new BinaryNode(1), new BinaryNode(1));
    BinaryNode testTree2 = new BinaryNode(1, new BinaryNode(1), new BinaryNode(1, new BinaryNode(2), null));
    BinaryNode testTree3 = new BinaryNode(1,
        new BinaryNode(2, new BinaryNode(2), new BinaryNode(2)),
        new BinaryNode(3, new BinaryNode(3), new BinaryNode(3)));

    for ( BinaryNode node : new BinaryNode[]{ testTree1, testTree2, testTree3 } ) {
      System.out.println("Testing " + node.name + ":");
      System.out.println("    Unival tree (1) == " + isUnivalTree(node));
      System.out.println("    Unival tree (2) == " + isUnivalTree2(node));
      System.out.println("    Count Unival subtrees (1) == " + countUnivalSubtrees(node));
      System.out.println("    Count Unival subtrees (2) == " + countUnivalSubtrees2(node));
    }
  }
}
