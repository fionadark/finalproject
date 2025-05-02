package augur;

public class Card {
    
    private String name;
    public String name_short;
    public String value;
    public int value_int;
    public String suit;
    public String type;
    public String meaning_up;
    public String meaning_rev;
    public String desc;

    // accessor methods
    public String getName() {
        return name;
    }

    public String getNameShort() {
        return name_short;
    }

    public String getValue() {
        return value;
    }

    public int getValueInt() {
        return value_int;
    }

    public String getSuit() {
        return suit;
    }

    public String getType() {
        return type;
    }

    public String getMeaningUp() {
        return meaning_up;
    }

    public String getMeaningRev() {
        return meaning_rev;
    }

    public String getDesc() {
        return desc;
    }
}
