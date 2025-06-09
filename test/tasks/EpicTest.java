package tasks;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    public void test15epicIdenticalById() {
        Epic epic1 = new Epic(123, "OTF", "oneTwoThree");
        Epic epic2 = new Epic(123, "OTF", "oneTwoThree");
        assertEquals(epic1, epic2);
    }
}
