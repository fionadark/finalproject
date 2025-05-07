package augur;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class TarotTest {

    // getImgHTMLTag() tests should return correct HTML tag
    @Test
    public void test_getImgHTMLTag_upright() {
        Tarot test = new Tarot();
        String result = test.getImgHTMLTag(false, "example.jpg");
        assertThat(result).isEqualTo(" <td> <img src=\"data:image/jpg;base64," + "example.jpg" + "\" style=\"width:150px; height:auto;\"> </td>");
    }

    @Test
    public void test_getImgHTMLTag_reversed() {
        Tarot test = new Tarot();
        String result = test.getImgHTMLTag(true, "example.jpg");
        assertThat(result).isEqualTo(" <td> <img src=\"data:image/jpg;base64," + "example.jpg" + "\" style=\"width:150px; height:auto; transform: rotate(180deg);\"> </td>");
    }

    @Test
    public void test_getImgHTMLTag_null() {
        Tarot test = new Tarot();
        assertThrows(IllegalArgumentException.class, () -> test.getImgHTMLTag(false, null));
    }
    
    // getCardMeaning() tests
    @Test
    public void test_getCardMeaning_uprightText() {
        Tarot test = new Tarot();
        String result = test.getCardMeaning(false, "cardName", "up_meaning", "rev_meaning");
        assertThat(result).isEqualTo(" <td><strong>cardName</strong><br><br>This card represents: up_meaning</td>");
    }

    @Test
    public void test_getCardMeaning_reversedText() {
        Tarot test = new Tarot();
        String result = test.getCardMeaning(true, "cardName", "up_meaning", "rev_meaning");
        assertThat(result).isEqualTo(" <td><strong>cardName</strong><br><br>This card reversed represents: rev_meaning</td>");
    }

    @Test
    public void test_getCardMeaning_nullText() {
        Tarot test = new Tarot();
        assertThrows(IllegalArgumentException.class, () -> test.getCardMeaning(false, null, null, null));
    }

    // getCardPos() tests
    @Test
    public void test_getCardPos() {
        Tarot test = new Tarot();
        String result = test.getCardPos(3, 2);
        assertThat(result).isEqualTo(" <td>Your Future</td>");
    }

    @Test
    public void test_getCardPos_invalidArg1() {
        Tarot test = new Tarot();
        assertThrows(IllegalArgumentException.class, () -> test.getCardPos(0, 3));
    }

    @Test
    public void test_getCardPos_invalidArg2() {
        Tarot test = new Tarot();
        assertThrows(IllegalArgumentException.class, () -> test.getCardPos(3, -1));
    }

    @Test
    public void test_getCardPos_2invalidArgs() {
        Tarot test = new Tarot();
        assertThrows(IllegalArgumentException.class, () -> test.getCardPos(15, -1));
    }

}
