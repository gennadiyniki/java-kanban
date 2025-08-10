package manager;

import tasks.*;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final Path filePath;

    // 1. Конструкторы с поддержкой старого и нового API
    public FileBackedTaskManager(File file) {
        this(file.toPath());
    }

    public FileBackedTaskManager(Path path) {
        this.filePath = path.normalize().toAbsolutePath();
        ensureFileExists();
    }

    public FileBackedTaskManager() {
        this(resolveDefaultPath());
    }

    // 2. Улучшенная инициализация файла
    private void ensureFileExists() {
        try {
            if (!Files.exists(filePath)) {
                Files.createDirectories(filePath.getParent());
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            try {
                throw new Exception.ManagerException("Не удалось инициализировать файл хранения:");
            } catch (Exception.ManagerException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static Path resolveDefaultPath() {
        try {
            URL resource = FileBackedTaskManager.class.getClassLoader().getResource("data.csv");
            return resource != null ? Paths.get(resource.toURI()) : Paths.get("task_data.csv");
        } catch (Exception e) {
            return Paths.get("task_data.csv");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    // 3. Безопасное сохранение с атомарной записью
    @Override
    public void save() {
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(filePath.getParent(), "temp", ".tmp");

            try (BufferedWriter writer = Files.newBufferedWriter(tempFile)) {
                writer.write("id,type,name,status,description,epic,duration,startTime\n");

                // Сохраняем все типы задач
                saveTasks(writer, tasks.values());
                saveTasks(writer, epics.values());
                saveTasks(writer, subtasks.values());
            }

            Files.move(tempFile, filePath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            try {
                if (tempFile != null) Files.deleteIfExists(tempFile);
            } catch (IOException ignored) {
            }

            try {
                throw new Exception.ManagerException("Ошибка сохранения данных");
            } catch (Exception.ManagerException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void saveTasks(BufferedWriter writer, Collection<? extends Task> tasks) throws IOException {
        for (Task task : tasks) {
            writer.write(CSVFormatter.toString(task) + "\n");
        }
    }

    // 4. Загрузка данных (сохраняем старый интерфейс)
    public static FileBackedTaskManager loadFromFile(File file) {
        return loadFromPath(file.toPath());
    }

    public static FileBackedTaskManager loadFromPath(Path path) {
        try {
            if (!Files.exists(path) || Files.size(path) == 0) {
                return new FileBackedTaskManager(path);
            }

            try (BufferedReader reader = Files.newBufferedReader(path)) {
                Map<Integer, Task> tasks = new HashMap<>();
                Map<Integer, Epic> epics = new HashMap<>();
                Map<Integer, Subtask> subtasks = new HashMap<>();
                int maxId = 0;

                reader.readLine(); // Пропускаем заголовок

                String line;
                while ((line = reader.readLine()) != null && !line.isEmpty()) {
                    Task task = CSVFormatter.fromString(line);
                    if (task == null) continue;

                    if (task instanceof Subtask) {
                        subtasks.put(task.getId(), (Subtask) task);
                    } else if (task instanceof Epic) {
                        epics.put(task.getId(), (Epic) task);
                    } else {
                        tasks.put(task.getId(), task);
                    }

                    maxId = Math.max(maxId, task.getId());
                }

                // Восстанавливаем связи
                restoreSubtasksEpicLinks(subtasks, epics);

                FileBackedTaskManager manager = new FileBackedTaskManager(path);
                manager.tasks = tasks;
                manager.epics = epics;
                manager.subtasks = subtasks;
                manager.generatorId = maxId;

                // Обновляем статусы эпиков
                epics.values().forEach(manager::updateEpicStatus);

                return manager;
            }
        } catch (IOException e) {
            try {
                throw new Exception.ManagerException("Ошибка загрузки данных");
            } catch (Exception.ManagerException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private static void restoreSubtasksEpicLinks(Map<Integer, Subtask> subtasks, Map<Integer, Epic> epics) {
        for (Subtask subtask : subtasks.values()) {
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                epic.addSubtask(subtask.getId());
            }
        }
    }

    // 5. Геттер для тестов (при необходимости)
    public Path getFilePath() {
        return filePath;
    }

    @Override
    public Task updateTask(Task task) {
        super.updateTask(task);
        save();
        return task;
    }

    @Override
    public Epic updateEpic(Epic updatedEpic) {
        super.updateEpic(updatedEpic);
        save();
        return updatedEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }


    @Override
    public Task addTask(Task task) {
        super.addTask(task);
        save();
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return epic;
    }

    @Override
    public Subtask addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return subtask;
    }

    @Override
    public void deleteEpicById(int id) {
        super.deleteEpicById(id);
        save();
    }

    @Override
    public void deleteTaskById(int id) {
        super.deleteTaskById(id);
        save();
    }

    @Override
    public void deleteSubtaskById(int id) {
        super.deleteSubtaskById(id);
        save();
    }

    @Override
    protected void updateEpicStatus(Epic epic) {
        super.updateEpicStatus(epic);
        save();
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return prioritizedTasks.stream().toList();
    }
}