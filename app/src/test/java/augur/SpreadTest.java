package augur;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

public class SpreadTest {

    @Test
    public void test_getShortNames() {
        Spread spread = new Spread();
        List<String> results = spread.getShortNames();

        // in progress
    }
    
}
