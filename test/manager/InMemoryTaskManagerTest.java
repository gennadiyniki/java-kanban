package manager;

import tasks.Epic;

import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTaskManagerTest {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    Task task;
    Epic epic;
    Subtask subtask;
    TaskManager taskManager;

    @BeforeEach
    public void installFiled() {
        taskManager = Managers.getDefaultManager();

        task = new Task(1, "Название", "Описание", TaskStatus.NEW);
        taskManager.addTask(task);
        epic = new Epic(1, "Эпик", "Описание");
        taskManager.addEpic(epic);
        subtask = new Subtask(1, "Сабтаска", "Описание", TaskStatus.NEW, 1);
        taskManager.addSubtask(subtask);
    }

    @Test
    public void test1GetEpicById() {
        int id = epic.getId();
        assertEquals(epic, taskManager.getEpicById(id));
    }
    @Test
    public void test2GetTaskById() {
        int id = task.getId();
        assertEquals(task, taskManager.getTaskById(id));
    }

    @Test
    public void test3GetSubtaskById() {// не проходит
        int id = subtask.getId();
        assertEquals(null, taskManager.getSubtaskById(id));
    }

    @Test
    public void test4DeleteEpic() {
        taskManager.deleteEpics();
        assertTrue(epics.isEmpty());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void test5DeleteTask() {
        taskManager.deleteTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void test6DeleteSubtask() {
        taskManager.deleteSubtasks();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void test7DeleteEpicById() {
        taskManager.deleteEpicById(epic.getId());
        assertTrue(epics.isEmpty());
    }

    @Test
    public void test8DeleteTaskById() {
        taskManager.deleteTaskById(task.getId());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void test9DeleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());
        assertTrue(subtasks.isEmpty());
    }
    @Test
    public void test10UpdateEpicStatus() {
        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    public void test11AddTask() {
        assertEquals(1, task.getId());
        assertEquals("Название", task.getName());
        assertEquals("Описание", task.getDescription());
    }

    @Test
    public void test12AddEpic() {
        assertEquals(2, epic.getId());
        assertEquals("Эпик", epic.getName());
        assertEquals("Описание", epic.getDescription());
    }

    @Test
    public void test13AddSubtask() {
        assertEquals(3, subtask.getId());
        assertEquals("Сабтаска", subtask.getName());
        assertEquals("Описание", subtask.getDescription());
        assertEquals(1, subtask.getEpicId());
    }
}