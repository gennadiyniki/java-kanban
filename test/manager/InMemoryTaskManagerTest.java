package manager;

import tasks.Epic;

import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public abstract class InMemoryTaskManagerTest extends TaskManagerTest {

    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    Task task;
    Epic epic;
    Subtask subtask;
    TaskManager taskManager;

    @BeforeEach
    public void installFiled() {
        taskManager = Managers.getInMemoryManager();

        task = new Task(1, "Название", "Описание", TaskStatus.NEW);
        taskManager.addTask(task);
        epic = new Epic(1, "Эпик", "Описание");
        taskManager.addEpic(epic);
        subtask = new Subtask(1, "Сабтаск", "Описание", 1, TaskStatus.NEW);
        taskManager.addSubtask(subtask);
    }

    @Test
    public void testGetEpicById() {
        int id = epic.getId();
        assertEquals(epic, taskManager.getEpicById(id));
    }

    @Test
    public void testGetTaskById() {
        int id = task.getId();
        assertEquals(task, taskManager.getTaskById(id));
    }

    @Test
    public void testGetSubtaskById() {
        int id = subtask.getId();
        assertEquals(null, taskManager.getSubtaskById(id));
    }

    @Test
    public void testDeleteEpic() {
        taskManager.deleteEpics();
        assertTrue(epics.isEmpty());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void testDeleteTask() {
        taskManager.deleteTasks();
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testDeleteSubtask() {
        taskManager.deleteSubtasks();
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void testDeleteEpicById() {
        taskManager.deleteEpicById(epic.getId());
        assertTrue(epics.isEmpty());
    }

    @Test
    public void testDeleteTaskById() {
        taskManager.deleteTaskById(task.getId());
        assertTrue(tasks.isEmpty());
    }

    @Test
    public void testDeleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());
        assertTrue(subtasks.isEmpty());
    }

    @Test
    public void testUpdateEpicStatus() {
        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    public void testAddTask() {
        assertEquals(1, task.getId());
        assertEquals("Название", task.getName());
        assertEquals("Описание", task.getDescription());
    }

    @Test
    public void testAddEpic() {
        assertEquals(2, epic.getId());
        assertEquals("Эпик", epic.getName());
        assertEquals("Описание", epic.getDescription());
    }

    @Test
    public void testAddSubtask() {
        assertEquals(1, subtask.getId());
        assertEquals("Сабтаск", subtask.getName());
        assertEquals("Описание", subtask.getDescription());
        assertEquals(1, subtask.getEpicId());
    }
}