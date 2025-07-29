package manager;

import tasks.*;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private final File savedTasksFile;

    public FileBackedTaskManager() {
        try {
            // Получаем путь к файлу из ресурсов
            Path path = Paths.get(getClass().getClassLoader().getResource("data.csv").toURI());
            this.savedTasksFile = path.toFile();
        } catch (Exception e) {
            throw new ManagerSaveException("Файл data.csv не найден в ресурсах", e);
        }
    }

    public FileBackedTaskManager(File savedTasksFile) {
        this.savedTasksFile = savedTasksFile;
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(savedTasksFile))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();
            for (Task task : tasks.values()) {
                writer.write(CSVFormatter.toString(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(CSVFormatter.toString(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(CSVFormatter.toString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла", e);
        }
    }

    public static FileBackedTaskManager loadFromFile(File savedTasksFile) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(savedTasksFile.toPath())) {
            bufferedReader.readLine();
            Map<Integer, Task> tasks = new HashMap<>();
            Map<Integer, Epic> epics = new HashMap<>();
            Map<Integer, Subtask> subtasks = new HashMap<>();
            int maxId = 0;
            String line;
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                Task task1 = CSVFormatter.fromString(line);
                if (task1 != null) {
                    if (task1.getType() == TaskType.SUBTASK) {
                        Subtask subtask = (Subtask) task1;
                        if (subtask.getId() > maxId) {
                            maxId = subtask.getId();
                        }
                        subtasks.put(subtask.getId(), subtask);
                    } else if (task1.getType() == TaskType.EPIC) {
                        Epic epic = (Epic) task1;
                        if (epic.getId() > maxId) {
                            maxId = epic.getId();
                        }
                        epics.put(epic.getId(), epic);
                    } else if (task1.getType() == TaskType.TASK) {
                        Task task = task1;
                        if (task.getId() > maxId) {
                            maxId = task.getId();
                        }
                        tasks.put(task.getId(), task);
                    }
                }
            }
            for (Subtask subtask : subtasks.values()) {
                Epic parentEpic = epics.get(subtask.getEpicId());
                if (parentEpic != null) {
                    parentEpic.addSubtask(subtask.getId());
                }
            }
            FileBackedTaskManager manager = new FileBackedTaskManager(savedTasksFile);
            manager.tasks = tasks;
            manager.epics = epics;
            manager.subtasks = subtasks;
            manager.generatorId = maxId;
            return manager;
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке из файла", e);
        }
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
    public void deleteTasks() {
        super.deleteTasks();
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
}