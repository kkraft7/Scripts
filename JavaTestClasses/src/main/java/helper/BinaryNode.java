package helper;

public class BinaryNode {
  private static int nodeID = 1;
  public BinaryNode left, right;
  public int value;
  public String name;
  public BinaryNode(int val, BinaryNode l, BinaryNode r) {
    this.value = val;
    this.left = l;
    this.right = r;
    this.name = String.format("Node%04d", nodeID);
    nodeID++;
  }
  public BinaryNode(int val) { this(val, null, null); }
}
