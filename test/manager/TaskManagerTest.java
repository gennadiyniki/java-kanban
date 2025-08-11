package manager;

import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;


import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;


import static org.junit.jupiter.api.Assertions.*;


public abstract class TaskManagerTest<T extends TaskManager> {
    protected T taskManager;
    protected Task task;
    protected Epic epic;
    protected Subtask subtask;

    @BeforeEach
    public void setUp() {
        taskManager = createTaskManager();

        // Создаем задачи с временем выполнения
        LocalDateTime now = LocalDateTime.now();

        task = new Task(1, "Таск", "Описание", TaskStatus.NEW,
                now, Duration.ofMinutes(30));
        taskManager.addTask(task);

        epic = new Epic(2, "Эпик", "Описание");
        taskManager.addEpic(epic);

        subtask = new Subtask(3, "Сабтаск", "Описание", epic.getId(),
                TaskStatus.NEW, now.plusHours(1), Duration.ofMinutes(45));
        taskManager.addSubtask(subtask);
    }

    protected abstract T createTaskManager();

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
    public void testDeleteTask() {
        taskManager.deleteTasks();
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testDeleteSubtask() {
        taskManager.deleteSubtasks();
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void testDeleteEpicById() {
        taskManager.deleteEpicById(epic.getId());
        assertNull(taskManager.getEpicById(epic.getId()));
    }

    @Test
    public void testDeleteTaskById() {
        taskManager.deleteTaskById(task.getId());
        assertNull(taskManager.getTaskById(task.getId()));
    }

    @Test
    public void testDeleteSubtaskById() {
        taskManager.deleteSubtaskById(subtask.getId());
        assertNull(taskManager.getSubtaskById(subtask.getId()));
    }

    @Test
    public void testUpdateEpicStatus() {
        assertEquals(TaskStatus.NEW, epic.getTaskStatus());
    }

    @Test
    public void testAddTask() {
        Task newTask = new Task(2, "Новая задача", "Описание", TaskStatus.NEW);
        taskManager.addTask(newTask);
        assertEquals(newTask, taskManager.getTaskById(newTask.getId()));
    }

    @Test
    public void testAddEpic() {
        Epic newEpic = new Epic(2, "Новый эпик", "Описание");
        taskManager.addEpic(newEpic);
        assertEquals(newEpic, taskManager.getEpicById(newEpic.getId()));
    }

    @Test
    public void testTaskTimeIntersection() {
        LocalDateTime now = LocalDateTime.now();
        Task task1 = new Task(10, "Task 1", "Description 1", TaskStatus.NEW, now, Duration.ofMinutes(30));
        Task task2 = new Task(11, "Task 2", "Description 2", TaskStatus.NEW, now.plusMinutes(15), Duration.ofMinutes(30));

        assertTrue(task1.hasIntersectionWith(task2), "Задачи должны пересекаться");
    }
}