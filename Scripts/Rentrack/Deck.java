
import java.util.*;

// Should handSize be part of Deck or part of Game?!
public class Deck {
    private List<Card> cards;
    private int handSize;
    private int nextCard;
    private int remainingCards;
    private static final int DECK_SIZE = 52;
    private static final int HAND_SIZE = 5;

    public Deck(int handSize) {
        cards = new ArrayList<Card>(DECK_SIZE);
        for ( Suit s : Suit.values() )
            for ( Rank r : Rank.values() )
                if ( ! r.equals(Rank.N01) )  // Add Aces instead of Ones
                    cards.add(new Card(r, s));
        nextCard = 0;
        this.handSize = handSize;
        remainingCards = cards.size();
        // THROW EXCEPTION...
        if ( cards.size() != DECK_SIZE )
            System.err.println("Bad deck size: " + cards.size());
    }

    public Deck() { this(HAND_SIZE); }

    public List<Hand> deal(int players) {
        ArrayList<Hand> hands = new ArrayList<Hand>(players);
        while ( hands.size() < players )
            hands.add(getNextHand());
        return hands;
    }

//  public Hand getNextHand(int handSize) {
    public Hand getNextHand() {
        ArrayList<Card> cards = new ArrayList<Card>();
        while ( cards.size() < handSize )
            cards.add(getNextCard());
        return new Hand(cards);
    }

//  public Hand getNextHand() { return getNextHand(HAND_SIZE); }

    public Card getNextCard() {
        remainingCards--;
        return cards.get(nextCard++);
    }

    public int remainingHands() {
        return (int)(remainingCards/handSize);
    }

    // DO I NEED THIS IF I HAVE remainingHands()?
    public int remainingCards() { return remainingCards; }

//  Do some set number of swaps...
//  public void shuffle() { }

    public static void main(String[] args) {
        final int NUM_PLAYERS = 2;
        int players;

        if ( args.length > 0 ) {
            try {
                players = Integer.parseInt(args[0]);
            }
            catch (Exception ex) {
                players = NUM_PLAYERS;
            }
        }
        else {
            players = NUM_PLAYERS;
        }

        Deck deck = new Deck();
        while ( deck.remainingHands() > players ) {
            List<Hand> hands = deck.deal(players);
            for ( Hand h : hands )
                System.out.println(h);
            System.out.println();
        }
    }
}

