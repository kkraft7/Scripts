
import java.io.*;
import java.util.*;
import java.lang.Math;

/**
*** Test creating trees and various tree traversal algorithms.
**/
// WHAT IS PRE/POST/IN-ORDER TRAVERSAL FOR A NON-BINARY TREE?
// NEED PRINT OPTIONS FOR NODE ITSELF, NODE + CHILDREN, AND FULL TREE?
// NEED A COMPLETE SUITE OF AUTOMATED TESTS!
public class Node {
    private String name;
    private Node parent;
    private int level;
    private ArrayList<Node> children = new ArrayList<Node>();
    private static int nameFormatWidth = 3;

    /**
    *** Constructor for non-root node
    **/
    public Node(String name, Node parent) {
        this.name = name;
        children = new ArrayList<Node>();
        setParent(parent);
    }

    /**
    *** Constructor for root node
    **/
    public Node(String name) { this( name, null ); }

    // SAVE LONGEST NAME TO FORMAT FIELD WIDTH
    // HAVE A STATIC INITIALIZER THAT DEFAULTS TO 1? setNameFormatWidth(int)?
    public String toString() {
        // USE String.format() HERE?
//      String s = String.format("NAME: %(%d)s; LEVEL: %d; PARENT: %(%d)s; CHILDREN:", name, level, ( parent == null ? "/" : parent.getName()));
        String s = "NAME: " + name + "; LEVEL: " + level + "; PARENT: "
            + ( parent == null ? "/" : parent.getName() ) + "; CHILDREN:";
        for (Node child : children)
            s += " " + child.getName(); 
        return s;
    }

    private static void pause() {
        try { System.in.read(); } catch(IOException e) {}
    }

    public void print() { System.out.println(this); }
    public int getLevel() { return level; }
    public String getName() { return name; }
    public ArrayList<Node> getChildren() { return children; }
    public void addChild(Node child) { children.add(child); }
    public void move(Node newParent) { setParent(newParent); }

    public void setParent(Node newParent) {
        parent = newParent;
        if ( parent != null )
            parent.addChild(this);
        // Level is always determined based on the parent:
        level = ( parent == null ? 1 : parent.getLevel() + 1 );
    }

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
    // printTreeBreadthFirst() ?
    public void printBreadthFirst2() {
        LinkedList<Node> list = new LinkedList<Node>();
        list.addFirst(this);

        // ANY WAY TO TRACK THE LEVEL? (DON'T NEED TO NOW)
        System.out.println("\nBreadth-first tree traversal (2):");
        while ( list.size() > 0 ) {
            Node n = list.remove();
            n.print();
            list.addAll(n.getChildren());
        }
    }

// Using list.size() to control the for-loop doesn't work because when
// the last parent is removed (and list.size() == 0) there are still
// its children left to add, so the loop terminates maxChild - 1 nodes
// too early.

// NEED A TREE CLASS TO ENCAPSULATE ITERATION, MAX LEVEL, MAX CHILD, ETC?
    public static Node createBreadthFirst1(int maxLevel, int maxChild) {
        Node root, parent = null;
        LinkedList<Node> list = new LinkedList<Node>();
    //  list.add( root = parent = new Node( "001", null ));
        list.add( root = new Node( "001", null ));
        int maxID = ( maxChild <= 1 ? maxLevel :
           (int)(Math.pow(maxChild, maxLevel) - 1)/(maxChild - 1));

        System.out.println("\nBreadth-first tree creation (1)");
        System.out.println(  "===============================");
        System.out.format("MAX LEVEL = %d; MAX CHILD = %d; MAX ID = %d\n",
            maxLevel, maxChild, maxID);

        for ( int id = 1; id < maxID; id++ ) {
            if (( id - 1 ) % maxChild == 0 )
                parent = list.remove(); // Removes from front of list
            list.add( new Node( String.format("%03d", (id + 1)), parent ));
        }
        return root;
    }

    public static Node createBreadthFirst2(int maxLevel, int maxChild) {
        Node root = null, parent = null;
        LinkedList<Node> list = new LinkedList<Node>();
        System.out.println("\nBreadth-first tree creation (2)");
        System.out.println(  "===============================");
        System.out.println("MAX LEVEL: " + maxLevel);
        System.out.println("MAX CHILD: " + maxChild);
        for ( int id = 0, level = 0; level < maxLevel; level++ ) {
            for ( int i = 0; i < Math.pow(maxChild, level); i++, id++ ) {
                Node child = new Node( Integer.toString(id + 1), parent );
                if ( id == 0 )
                    root = child;
                // DON'T NEED IF? WOULD JUST GET EXTRA UNUSED NODES IN LIST?
                // LEVEL IS *ALWAYS* LESS THAN MAX LEVEL HERE!
                // if ( level < maxLevel )
                    list.add(child); // Adds at end of list
                if ( id % maxChild == 0 )
                    parent = list.remove(); // Removes from front of list
            //  System.out.format("LEVEL = %d; ID = %d\n", (level + 1), id);
            }
        }
        return root;
    }

//  THIS ISN'T WORKING... WHAT'S MY GOAL HERE?
    public static Node createBreadthFirst3(int maxLevel, int maxChild) {
        int level = 0;
        Node root, parent = null; // = new Node("1");
        LinkedList<Node> list = new LinkedList<Node>();
    //  list.add(parent);
        list.add(root = parent = new Node("1"));

        System.out.println("\nBreadth-first tree creation (3):");
    //  parent.print();

    //  for ( int id = 1; list.size() > 0; id++ ) {
        for ( int id = 1; level <= maxLevel; id++ ) {
            // THE 2ND NUMBER HAS TO BE THE DEGREE (OR NUMBER OF CHILDREN)
            // I THINK MAX CHILD == 1 IS A SPECIAL CASE (WILL ALSO FAIL!)
            double levelF = (Math.log(id + 1)/Math.log(maxChild));
            level = (int)(Math.log(id + 1)/Math.log(maxChild)) + 1;
            System.out.format("ID: %03d; LEVEL: %d; LEVEL (FLOAT): %f\n",
                (id + 1), level, levelF);
        //  System.out.format("ID = %02d; Level = %d\n", id, level);
            //  id, (int)Math.log10(id));
            if ( id % maxChild == 0 ) {
                parent = list.remove();
            }
            Node child = new Node( Integer.toString(id), parent );
//          child.print();
            if ( level < maxLevel ) {
                list.add(child);
            }
        //  list.addAll(n.getChildren());
        //  for ( int c = 0; c < children; c++ ) {
        //      list.add(new Node 
        //  }
        }
        return root;
    }

// CAN I FACTOR OUT THE SIMILARITIES IN THESE TEST ROUTINES?
    public static void testCreateBreadthFirst1() {
        for ( int i = 4; i > 0; i-- ) {
            System.out.println("maxLevel = 4; maxChild = " + i);
            createBreadthFirst1(4, i).printBreadthFirst2();
            pause();
        }
    }

    public static void testCreateBreadthFirst2() {
        for ( int i = 4; i > 0; i-- ) {
            System.out.println("maxLevel = 4; maxChild = " + i);
            createBreadthFirst2(4, i).printBreadthFirst2();
            pause();
        }
    }

    public static void testCreateBreadthFirst3() {
        for ( int i = 4; i > 0; i-- ) {
            System.out.println("maxLevel = 4; maxChild = " + i);
            createBreadthFirst3(4, i).printBreadthFirst2();
            pause();
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
        //  printPostOrder(child);
        n.print();
    }

    public static Node parseTreeData(String fileName) {
        HashMap<String, Node> treeData = new HashMap<String, Node>();
        Node root = null;
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
                        root = new Node(name);
                        treeData.put(name, root);
                    }
                    // HOW TO SET THIS UP SO NODES CAN BE READ IN ANY ORDER?
                    else if ( treeData.containsKey(parent) ) {
                        Node child = new Node(name, treeData.get(parent));
                        treeData.put(name, child);
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

        return root;
    }

    // PASS "MODE" FIRST TO DETERMINE IF WE ARE READING OR GENERATING TREE
    // MODES:
    // 1. Read/Parse tree
    // 2. Generate tree according to maxChild and maxLevel from command line
    // 3. Run automated test of tree generator
    public static void main(String[] args) {
        String fileName = "Tree1.txt";
        int maxLevel = 4;
        int maxChild = 2;
        if ( args.length > 0 ) {
            try {
                maxLevel = Integer.parseInt(args[0]);
            }
            catch (NumberFormatException ex) {
                fileName = args[0];
            }
        }
        if ( args.length > 1 ) {
            try {
                maxChild = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException ex) { }
        }
    //  String fileName = ( args.length > 0 ? args[0] : "Tree1.txt" );
        Node t1 = Node.parseTreeData(fileName);
        t1.printBreadthFirst1();
        t1.printBreadthFirst2();
        t1.printPreOrder();
    //  t1.printPostOrder();
    //  t1.createBreadthFirst1();
    //  t1.testCreateBreadthFirst2();
        testCreateBreadthFirst1();
    //  testCreateBreadthFirst2();
    //  testCreateBreadthFirst3();
    }
}

