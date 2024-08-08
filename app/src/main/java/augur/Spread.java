package augur;
import java.util.*;

public class Spread {
    public int nhits;
    public List<Card> cards;

    public List<String> getShortNames() {
        List<String> names = new ArrayList<String>();

        for(int i = 0; i < this.nhits; i++) {
            names.add(this.cards.get(i).name_short);
        }
        return names;

    }
}
