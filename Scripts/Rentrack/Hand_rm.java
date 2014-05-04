
// package Rentrack;

import java.io.*;
import java.util.*;

enum HandType { HIGH_CARD, PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT,
                FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH };
enum Result { LOSS, TIE, WIN };

// MAY NOT NEED THIS
/**
*** Document this (key by rank but sort by rank/size)
**/
class CardGroup implements Comparable<CardGroup> {
    private final Rank rank;
    private int size;

    public CardGroup(Rank rank, int size) {
        this.rank = rank;
        this.size = size;
    }

    public CardGroup(Rank rank) {
        this.rank = rank;
        this.size = 0;
    }

    public Rank getRank() { return rank; }
    public int  getSize() { return size; }
    public int  hashCode() { return rank.hashCode(); }
    public void increment() { size++; }

    public int compareTo(CardGroup cg) {
        if ( size > cg.getSize() )
            return 1;
        if ( size < cg.getSize() )
            return -1;
        return rank.compareTo(cg.getRank());
    }
}

// Create Comparable CardGroup object?
/**
*** Ranking of Hands (http://www.pokerpages.com/pokerinfo/rank):
***
*** Royal Flush
*** Straight Flush
*** Four of a Kind
*** Full House
*** Flush
*** Straight
*** Three of a Kind
*** Two Pair
*** Pair
*** High Card
**/
public class Hand implements Comparable<Hand> {
    private List<Card> cards;
    private TreeMap<Rank, Integer> rankMap;
    private TreeMap<Rank, Integer> rankMap2;
    private ArrayList<CardGroup> cardGroups;
    private ArrayList<Rank> rankList;
    private ArrayList<Rank> rankList2;
    private final HandType type;
    private final boolean flush;
//  May not need high, low, ace:
    private int high = 0;
    private int low = 0;
    private boolean ace = false;

    // SWITCH MAIN CONSTRUCTOR TO ArrayList
//  public Hand(Card[] cards) {
    public Hand(List<Card> cards) {
        this.cards = cards;
        if ( cards == null || cards.size() != 5 )
            System.err.println("Invalid hand length: " + cards.size());

        // I THINK THIS FAILED WITH A NULL POINTER EXCEPTION (CAN LOSE)
/*
        rankMap2 = new TreeMap<Rank, Integer>(new Comparator<Rank>() {
                public int compare(Rank r1, Rank r2) {
                    if ( ! rankMap2.get(r1).equals(rankMap2.get(r2)) )
                        return -rankMap2.get(r1).compareTo(rankMap2.get(r2));
                    return -r1.compareTo(r2);
                }
            }
        );
*/

        // Initialize hash and flush value for evaluating hand
        rankMap = new TreeMap<Rank, Integer>();
        rankList2 = new ArrayList<Rank>();
        cardGroups = new ArrayList<CardGroup>();
        boolean flush = true;
        Suit lastSuit = null;
        // Sort the hand as/after I read it in?
        for ( Card c : cards ) {
            Rank r = c.getRank();
            if ( lastSuit != null && ! lastSuit.equals(c.getSuit()))
                flush = false;
            if ( ! rankMap.containsKey(r) )
                rankMap.put(r, 1);
            else
                rankMap.put(r, rankMap.get(r) + 1);
            if ( ! rankList2.contains(r) )
                rankList2.add(r);
            r.incrementGroup();
//          CardGroup cg = new CardGroup(r);
            if ( ! cardGroups.contains(r) )
                cardGroups.add(new CardGroup(r));
            
            lastSuit = c.getSuit();
        }
        this.flush = flush;

        Collections.sort(cardGroups);
        // rankList contains rankMap keys sorted by value then key
        rankList = new ArrayList<Rank>(rankMap.keySet());
        Collections.sort(rankList, new Comparator<Rank>() {
                public int compare(Rank r1, Rank r2) {
                    int val = rankMap.get(r1).compareTo(rankMap.get(r2));
                    return -(val == 0 ? r1.compareTo(r2) : val);
                }
            }
        );
        type = evaluate();  // Relies on rankList (pass it in)?
    }

//  Add a contructor to take in a card string?
    public Hand(Card[] cards) { this(Arrays.asList(cards)); }

    private HandType evaluate() {

        switch(rankList.size()) {
            case 5:
                if ( rankList.get(0).ordinal()
                   - rankList.get(4).ordinal() == 4 ||
                     ( rankList.get(0).equals(Rank.ACE) &&
                       rankList.get(1).equals(Rank.N05) ) ||
                     ( rankList.get(4).equals(Rank.N01) &&
                       rankList.get(3).equals(Rank.N10) ))
                    return ( flush ?
                        HandType.STRAIGHT_FLUSH : HandType.STRAIGHT );
                return ( flush ?
                    HandType.FLUSH : HandType.HIGH_CARD );
            case 4:
                return HandType.PAIR;
            case 3:
                return ( rankMap.get(rankList.get(0)) == 3 ?
                    HandType.THREE_OF_A_KIND : HandType.TWO_PAIR );
            case 2:
                return ( rankMap.get(rankList.get(0)) == 4 ?
                    HandType.FOUR_OF_A_KIND : HandType.FULL_HOUSE );
        }
        // Throw an exception here?
        return null;
    }

//  For some reason descendingKeySet() fails with "cannotFindSymbol"
    private void printCardMap() {
        for ( Rank r : rankList )
            System.out.println(r + ": " + rankMap.get(r));
    }

    public int compareTo(Hand hand) {
        if ( ! type.equals(hand.getType()) )
            return type.compareTo(hand.getType());
        // Calculate tie-breaker for hands of the same type
        return 0;
    }

    // Note that compareTo() does not guarantee a return of [-1, 0, 1],
    // only a negative integer, positive integer, or 0
    public Result getResult(Hand hand) {
        int r = compareTo(hand);
        return Result.values()[r < 0 ? 0 : r > 0 ? 2 : 1];
    }

    public List<Card> getCards() { return cards; }

//  private List<Card> groupCards() { }

    public List<Card> getCardGroups() {

    }

    public String toString() {
        String s = "";
        for ( Card c : cards )
            s += c + " ";
        return s;
    }

    public HandType getType() { return type; }

    public static ArrayList<Hand> readHandData(String fileName) {
        ArrayList<Hand> hands = new ArrayList<Hand>();
        String line = null;

        try {
            BufferedReader file
                = new BufferedReader(new FileReader(fileName));

            while (( line = file.readLine() ) != null ) {

                // System.out.println("Processing line: " + line);
                // Skip comments and blank lines
                if ( line.matches("^\\s*(//|#|$).*") )
                    continue;

                ArrayList<Card> cards = new ArrayList<Card>();
                for ( String c : line.split("\\s+") )
                    cards.add(new Card(c));
                hands.add(new Hand(cards));
            }
        }
        catch(IOException e) {
            System.err.println("Exception reading hand data from file "
                + fileName + ": " + e);
        }
        return hands;
    }

    public static void main(String[] args) {
        ArrayList<Hand> hands = readHandData("HandType.txt");

        Hand previous = null;
        for ( Hand h : hands ) {
            System.out.println("HAND: " + h);
        //  Put this under a debug conditional
        //  h.printCardMap();
            System.out.println("TYPE: " + h.getType());
            if ( previous != null )
                System.out.println("Previous hand versus this hand: "
                    + previous.getResult(h));
            previous = h;
            System.out.println();
        }
    }
}

