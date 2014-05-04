
import java.util.*;

/**
*** Shuffle.java: Implement the card permutation algorithm described below
*** (assigned by Ning for their interview).
***
*** 1. Given a deck of n cards, take the top card off the deck and set
***    it on the table (on top of any cards already on the table)
*** 2. Take the next card and put it on the bottom of the deck
*** 3. Continue steps 1 and 2 until all cards are on the table
***    (this is a round)
*** 4. Take the deck from the table and repeat steps 1-3 until the
***    deck is in the original order
***
*** Write a program to determine how many rounds it will take to put
*** a deck back into the original order. It should take the number
*** of cards in the deck as a command line argument and write the
*** result to stdout.
**/

public class Shuffle {
    private LinkedList<Integer> deck, pile, initialDeck;
    private int numberOfRounds = 0;
    private static int debugLevel = 1;
    private static boolean runTest = false;

    /**
    *** I model a pile of cards as a LinkedList, which acts in this case
    *** as a double-ended queue.
    **/
    public Shuffle(int deckSize) {
        deck = new LinkedList<Integer>();
        pile = new LinkedList<Integer>();
        for ( int i = 0; i < deckSize; i++ )
            deck.add(i + 1);
        initialDeck = new LinkedList<Integer>(deck);
    }

    /**
    *** The following LinkedList API is used to emulate a double-ended queue:
    *** add()     : Add specified element at end of list
    *** remove()  : Remove and return element at end of list
    *** addFirst(): Add specified element at front of list
    **/
    public void round() {

        while ( deck.size() > 0 ) {
            pile.addFirst(deck.remove());
            if ( deck.size() > 0 )
                deck.add(deck.remove());
            if ( debugLevel >= 2 )
                displayState();
        }

        deck.addAll(pile);
        pile.clear();
        numberOfRounds++;
        
        if ( debugLevel >= 1 )
            displayState();
    }

    public void cycle() {

        if ( debugLevel >= 1 )
            displayState();

        do {
            round();
        } while ( ! deck.equals( initialDeck ));
    }

    public static void runTest() {
        debugLevel = 0;
	int[] test1 = new int[] { 4, 5, 10, 17, 50, 100 };

        int maxSize = 20;
        int[] sequential = new int[maxSize];
        for ( int i = 0; i < maxSize; i++ )
            sequential[i] = i + 1;

        for ( int size : sequential ) {
            Shuffle s = new Shuffle(size);
            s.cycle();
            System.out.println(s.getNumberOfRounds()
                + " were required for a deck of size " + size);
        }
    }

    public int getNumberOfRounds() { return numberOfRounds; }

    public void displayState() {

        System.out.println(
            "State after " + getNumberOfRounds() + " rounds:\n" + this);
        try {
            System.in.read();
        }
        catch (Exception e) { }
    }

    public String toString() {
        return
            "ORIG: " + initialDeck + "\n" +
            "DECK: " + deck + "\n" +
            "PILE: " + pile;
    }

    public static void main(String[] args) {

        if ( runTest ) {
            Shuffle.runTest();
            System.exit(0);
        }

        int deckSize;
        try {
            deckSize = Integer.parseInt(args[0]);
            Shuffle s = new Shuffle(deckSize);
            s.cycle();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }
}

