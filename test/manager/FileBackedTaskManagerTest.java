package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;

class FileBackedTaskManagerTest {

    @TempDir
    Path tempDir;
    private FileBackedTaskManager taskManager;
    private Path savedTasksFile;

    @BeforeEach
    void setUp() throws IOException {
        savedTasksFile = tempDir.resolve("tasks.csv");
        taskManager = new FileBackedTaskManager(savedTasksFile.toFile());


        Task task = new Task(1, "Таск", "Описание", TaskStatus.NEW);
        Epic epic = new Epic(2, "Эпик", "Описание", TaskStatus.NEW);
        Subtask subtask = new Subtask(3, "Сабтаск", "Описание", 2, TaskStatus.NEW);

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.save();
    }

    @Test
    void testSaveAndLoad() throws IOException {
        assertTrue(Files.exists(savedTasksFile), "Файл не был создан"); // Проверяем что файл создан
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedTasksFile.toFile());
        // Проверяем таски
        Task loadedTask = loadedManager.getTaskById(1);
        assertNotNull(loadedTask, "Таск не загружена");
        assertEquals("Таск", loadedTask.getName());
        assertEquals(TaskStatus.NEW, loadedTask.getTaskStatus());
        // Проверка эпиков
        Epic loadedEpic = loadedManager.getEpicById(2);
        assertNotNull(loadedEpic, "Эпик не загружен");
        assertEquals("Эпик", loadedEpic.getName());
        assertTrue(loadedEpic.getSubtasks().contains(3), "Сабтаск не привязана к ЭПИКУ");
        // Проверка сабтаски
        Subtask loadedSubtask = loadedManager.getSubtaskById(3);
        assertNotNull(loadedSubtask, "Сабтаск не загружена");
        assertEquals("Сабтаск", loadedSubtask.getName());
        assertEquals(2, loadedSubtask.getEpicId(), "Неверный EpicId у САБТАСКИ");

    }

    @Test
    void testFileContentFormat() throws IOException {
        String content = Files.readString(savedTasksFile);
        String[] lines = content.split(System.lineSeparator());
        assertEquals("id,type,name,status,description,epic", lines[0]);
        assertEquals(4, lines.length);
    }
}