
// package Rentrack;

// DO I REALLY NEED N01?
enum Suit { SPD, CLB, DMD, HRT }
enum Rank { N01, N02, N03, N04, N05, N06, N07,
            N08, N09, N10, JCK, QNN, KNG, ACE;
    // For evaluating hand values
    int groupSize = 0;
    public void incrementGroup() { groupSize++; }
};

public class Card implements Comparable<Card> {
    private final Rank rank;
    private final Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    // Assumes a string of the form: Rank-Suit
    public Card(String card) {
        this(
            Rank.valueOf(card.split("-")[0]),
            Suit.valueOf(card.split("-")[1])
        );
    }

    public int value() { return rank.ordinal() + 1; }
    public Rank getRank() { return rank; }
    public Suit getSuit() { return suit; }
    public String toString() { return rank + "-" + suit; }

    public int compareTo(Card card) {
        if ( value() > card.value() )
            return 1;
        else if ( value() < card.value() )
            return -1;
        return 0;
    }
}

