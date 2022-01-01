
import java.io.*;
import java.util.*;

// I THINK I CAN LOSE THIS BECAUSE I COMBINED TREE AND NODE IN Node.java
/*
** Test creating trees and various tree traversal algorithms.
*/
// WHAT IS PRE/POST/IN-ORDER FOR A NON-BINARY TREE?
public class Tree extends Node {

    public Tree(String name) { super(name); }

    public void printBreadthFirst1() {
        ArrayList<Node> list1 = new ArrayList<Node>(getChildren());
        ArrayList<Node> list2 = new ArrayList<Node>();

        System.out.println("\nBreadth-first tree traversal (1):");
        System.out.println("LEVEL 1");
        print();
        for ( int level = 2; list1.size() > 0; level++ ) {
            System.out.println("LEVEL " + level);
            for ( Node child : list1 ) {
                child.print();
                list2.addAll(child.getChildren());
            }
            list1 = new ArrayList<Node>(list2);
            list2.clear();
        }
    }

    // For some reason push() and pop() don't compile for LinkedList
    // http://en.wikipedia.org/wiki/Breadth-first_traversal
    public void printBreadthFirst2() {
        LinkedList<Node> list = new LinkedList<Node>();
        list.addFirst(this);

        // ANY WAY TO PRINT OUT THE LEVEL?
        System.out.println("\nBreadth-first tree traversal (2):");
        while ( list.size() > 0 ) {
            Node n = list.remove();
            n.print();
            list.addAll(n.getChildren());
        }
    }

    public void printPreOrder() {
        System.out.println("\nPre-order tree traversal:");
        printPreOrder(this);
    }

    private void printPreOrder(Node n) {
        n.print();
        for (Node child : n.getChildren())
            printPreOrder(child);
    }

    public void printInOrder() {
        // ONLY MAKES SENSE FOR BINARY TREES?
    }

    public void printPostOrder() {
        // ONLY MAKES SENSE FOR BINARY TREES?
        System.out.println("\nPost-order tree traversal:");
        printPostOrder(this);
    }

    private void printPostOrder(Node n) {
        for (Node child : n.getChildren())
            printPreOrder(child);
        n.print();
    }

    public static Tree parseTreeData(String fileName) {
        HashMap<String, Node> treeData = new HashMap<String, Node>();
        Tree newTree = null;
        String line = null;

        try {
            BufferedReader file
                = new BufferedReader(new FileReader(fileName));
            while (( line = file.readLine() ) != null ) {
                String[] tokens = line.split("[;= ]+");
                // Skip comments and blank lines
                if ( ! tokens[0].matches("^\\s*(#|//)*\\s*$") ) {
//                  System.out.println("Processing line: " + line);
                    String name = null;
                    String parent = null;
                    for ( int i = 0; i < tokens.length; i += 2 ) {
                        if ( tokens[i].equalsIgnoreCase("name") )
                            name = tokens[i + 1];
                        else if ( tokens[i].equalsIgnoreCase("parent") )
                            parent = tokens[i + 1];
                        else
                            System.err.println("Invalid token: " + tokens[i]);
                    }
                    if ( parent == null ) {
                        newTree = new Tree(name);
                        treeData.put(name, newTree);
                    }
                    // HOW TO SET THIS UP SO NODES CAN BE READ IN ANY ORDER?
                    else if ( treeData.containsKey(parent) ) {
                        Node newNode = new Node(name, treeData.get(parent));
                        treeData.put(name, newNode);
                    }
                    else
                        System.err.println("Missing parent node: " + parent);
                }
            }
        }
        catch (IOException e) {
            System.err.println("Exception reading file "
                + fileName + ": " + e);
        }
        catch (IndexOutOfBoundsException e) {
            System.err.println("Invalid node descriptor: " + line);
        }

        return newTree;
    }

    public static void main(String[] args) {
        String fileName = ( args.length > 0 ? args[0] : "Tree1.txt" );
        Tree t1 = Tree.parseTreeData(fileName);
        t1.printBreadthFirst1();
        t1.printBreadthFirst2();
        t1.printPreOrder();
//      t1.printPostOrder();
    }
}

class Node {
    String name;
    Node parent;
    ArrayList<Node> children;

    public Node(String name) {
        this.name = name;
        children = new ArrayList<Node>();
    }

    public Node(String name, Node parent) {
        this(name);
        this.parent = parent;
        this.parent.addChild(this);
    }

    // SAVE LONGEST NAME TO FORMAT FIELD WIDTH
    public String toString() {
        String s = "NAME: " + name + "; PARENT: "
            + ( parent == null ? "/" : parent.getName() ) + "; CHILDREN:";
        for (Node child : children)
            s += " " + child.getName(); 
        return s;
    }

    public void print() { System.out.println(this); }
    public String getName() { return name; }
    public void printName() { System.out.print(name); }
    public void printParent() {
        System.out.print( parent == null ? "/" : parent.getName() );
    }
    public void printChildren() {
        for (Node child : children)
            System.out.print(" " + child.getName());
    }
    public void addChild(Node child) { children.add(child); }
    public ArrayList<Node> getChildren() { return children; }
}

