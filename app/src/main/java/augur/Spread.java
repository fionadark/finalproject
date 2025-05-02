package augur;
import java.util.*;

public class Spread {
    public int nhits;
    public List<Card> cards;

    // accessor methods
    public List<String> getShortNames() {
        List<String> names = new ArrayList<String>();

        for(int i = 0; i < this.nhits; i++) {
            names.add(this.cards.get(i).getNameShort());
        }
        return names;

    }

    public int getNHits() {
        return nhits;
    }

    public List<Card> getCards() {
        return cards;
    }

}
