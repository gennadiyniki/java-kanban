package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Task;
import tasks.Epic;
import tasks.Subtask;
import tasks.TaskStatus;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryHistoryManagerTest {
    private HistoryManager history;
    private TaskManager taskManager;
    private Task task;
    private Epic epic;
    private Subtask subtask;

    @BeforeEach
    void setUp() {
        history = new InMemoryHistoryManager();
        taskManager = Managers.getDefaultManager();

        task = new Task(1, "Task", "Description", TaskStatus.NEW);
        epic = new Epic(2, "Epic", "Description");
        subtask = new Subtask(3, "Subtask", "Description", 2, TaskStatus.NEW);


        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
    }


    @Test
    void test14GetHistory() {
        taskManager.getTaskById(0);
        taskManager.getEpicById(1);
        taskManager.getSubtaskById(2);

        assertTrue(taskManager.getHistory().isEmpty());
        assertFalse(taskManager.getHistory().size() == 7);
    }

    @Test
    void EmptyHistory() { // проверка на пустую историю
        assertTrue(history.getHistory().isEmpty());
    }

    @Test
    void DuplicateTasks() { //обработки дубликатов
        history.add(task);
        history.add(task);
        assertEquals(1, history.getHistory().size());
    }

    @Test
    void removeFromHead() { // Удаление из истории: начало
        history.add(task);
        history.add(epic);
        history.remove(task.getId());

        List<Task> result = history.getHistory();
        assertEquals(1, result.size());
        assertEquals(epic, result.get(0));
    }

    @Test
    void removeFromMiddle() {
        // Добавляем задачи в историю (порядок: первый добавленный будет первым в списке)
        history.add(task);    // Будет первым в истории (first)
        history.add(epic);    // Будет в середине
        history.add(subtask); // Будет последним в истории (last)

        history.remove(epic.getId()); // Удаляем из середины

        List<Task> result = history.getHistory();
        assertEquals(2, result.size());

        // Порядок после удаления: task -> subtask
        assertEquals(task, result.get(0));    // Первый добавленный
        assertEquals(subtask, result.get(1)); // Последний добавленный
    }

    @Test
    void removeFromTail() {
        history.add(task);    // first (голова списка)
        history.add(epic);    // середина
        history.add(subtask); // last (хвост списка)

        // Удаляем последний добавленный элемент (хвост)
        history.remove(subtask.getId());

        // Ожидаемый порядок: task → epic
        List<Task> expected = List.of(task, epic);
        assertEquals(expected, history.getHistory());
    }

    @Test
    void removeNonExistentId() {  // удаление несуществующей задачи
        assertDoesNotThrow(() -> history.remove(999));
    }
}