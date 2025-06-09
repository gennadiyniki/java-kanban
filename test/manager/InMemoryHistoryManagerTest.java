package manager;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private static TaskManager taskManager;

    @Test
    public void test14GetHistory() {
        taskManager = Managers.getDefaultManager();
        assertTrue(taskManager.getHistory().isEmpty());
        assertFalse(taskManager.getHistory().size() == 7);
    }
}