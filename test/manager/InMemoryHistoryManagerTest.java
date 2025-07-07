package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {

    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefaultManager();
    }

@Test

    void test14GetHistory() {
        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);
        assertTrue(taskManager.getHistory().isEmpty());
        assertFalse(taskManager.getHistory().size() == 7);
    }
}