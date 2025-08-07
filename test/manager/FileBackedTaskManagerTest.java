package manager;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {
    @TempDir
    Path tempDir;
    private Path savedTasksFile;

    @BeforeEach
    public void setUp() {
        savedTasksFile = tempDir.resolve("tasks.csv");
        taskManager = new FileBackedTaskManager(savedTasksFile.toFile());
        task = new Task(1, "Таск", "Описание", TaskStatus.NEW);
        epic = new Epic(2, "Эпик", "Описание");
        subtask = new Subtask(3, "Сабтаск", "Описание", 2, TaskStatus.NEW);

        subtask.setDuration(Duration.ofMinutes(30));
        subtask.setStartTime(LocalDateTime.now());

        taskManager.addTask(task);
        taskManager.addEpic(epic);
        taskManager.addSubtask(subtask);
        taskManager.save();
    }

    @Override
    protected FileBackedTaskManager createTaskManager() {
        return new FileBackedTaskManager(savedTasksFile.toFile());
    }

    @Test
    void testSaveAndLoad() throws IOException {
        assertTrue(Files.exists(savedTasksFile), "Файл не был создан");
        FileBackedTaskManager loadedManager = FileBackedTaskManager.loadFromFile(savedTasksFile.toFile());

        Task loadedTask = loadedManager.getTaskById(1);
        assertNotNull(loadedTask, "Таск не загружена");
        assertEquals("Таск", loadedTask.getName());

        Epic loadedEpic = loadedManager.getEpicById(2);
        assertNotNull(loadedEpic, "Эпик не загружен");
        assertEquals("Эпик", loadedEpic.getName());

        Subtask loadedSubtask = loadedManager.getSubtaskById(3);
        assertNotNull(loadedSubtask, "Сабтаск не загружена");
        assertEquals("Сабтаск", loadedSubtask.getName());
    }

    @Test
    void testFileContentFormat() throws IOException {
        String content = Files.readString(savedTasksFile);
        String[] lines = content.split(System.lineSeparator());
        assertEquals("id,type,name,status,description,epic,duration,startTime", lines[0]);
        assertEquals(4, lines.length);
    }
}