
// package Rentrack;

import java.io.*;
import java.util.*;

enum HandType { HIGH_CARD, PAIR, TWO_PAIR, THREE_OF_A_KIND, STRAIGHT,
                FLUSH, FULL_HOUSE, FOUR_OF_A_KIND, STRAIGHT_FLUSH };
enum Result { LOSS, TIE, WIN };

// Put all this in a "poker" package/directory?
// Add an ID for identifying specific hands?
// Add a member for result (LOSS/TIE/WIN)?
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
    private HashMap<Rank, Integer> rankMap;
    private ArrayList<Rank> rankList;
    private final HandType type;
    private final boolean flush;

    public Hand(List<Card> cards) {
        this.cards = cards;
        if ( cards == null || cards.size() != 5 )
            System.err.println("Invalid hand size: " + cards.size());

        // Initialize hash and flush value for evaluating hand
        rankMap = new HashMap<Rank, Integer>();
        boolean flush = true;
        Suit lastSuit = null;
        for ( Card c : cards ) {
            Rank r = c.getRank();
            if ( lastSuit != null && ! lastSuit.equals(c.getSuit()))
                flush = false;
            if ( ! rankMap.containsKey(r) )
                rankMap.put(r, 1);
            else
                rankMap.put(r, rankMap.get(r) + 1);
            lastSuit = c.getSuit();
        }
        this.flush = flush;

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
        for ( int i = 0; i < rankMap.size(); i++ ) {
            Rank rank1 = rankList.get(i);
            Rank rank2 = hand.getCardGroups().get(i);
            if ( ! rank1.equals(rank2) )
                return rank1.compareTo(rank2);
        }
        return 0;
    }

    // Note that compareTo() does not guarantee a return of [-1, 0, 1],
    // only a negative integer, positive integer, or 0
    public Result getResult(Hand hand) {
        int r = compareTo(hand);
        return Result.values()[r < 0 ? 0 : r > 0 ? 2 : 1];
    }

    public HandType getType() { return type; }
    public List<Card> getCards() { return cards; }
    public List<Rank> getCardGroups() { return rankList; }

    public String toString() {
        String s = "";
        for ( Card c : cards )
            s += c + " ";
        return s;
    }

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
        ArrayList<Hand> hands = readHandData("HandTests.txt");

        Hand previous = null;
        for ( Hand h : hands ) {
            if ( previous != null ) {
                System.out.println("HAND1: " + previous);
                System.out.println(" TYPE: " + previous.getType());
                System.out.println("HAND2: " + h);
                System.out.println(" TYPE: " + h.getType());
                System.out.println("HAND1: " + previous.getResult(h));
                System.out.println();
            //  Put this under a debug conditional
            //  h.printCardMap();
            }
            previous = h;
        }
    }
}

