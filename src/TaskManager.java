import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generatorId = 1;

    protected int getGeneratorId() {
        return generatorId++;
    }

    public Task addTask(Task task) {
        int taskId = getGeneratorId();
        task.setId(taskId);
        tasks.put(taskId, task);
        return task;
    }

    public Epic addEpic(Epic epic) {
        epic.setId(getGeneratorId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask addSubtask(Subtask subtask) {
        int newSubtaskId = getGeneratorId();
        subtask.setId(newSubtaskId);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return null;
        }
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateEpicStatus(epic);
        return subtask;
    }

    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (!tasks.containsKey(taskID)) {
            return null;
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic updateEpic(Epic updatedEpic) {
        Integer epicId = updatedEpic.getId();
        if (!epics.containsKey(epicId)) {
            return null;
        }
        Epic epic = epics.get(updatedEpic.getId());
        epic.setDescription(updatedEpic.getDescription());
        epic.setName(updatedEpic.getName());
        updateEpicStatus(updatedEpic);
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (!subtasks.containsKey(subtaskId)) {
            return null;
        }
        int epicId = subtask.getEpicId();
        subtasks.put(subtaskId, subtask);
        Epic epic = epics.get(epicId);
        if (epic != null) {
            updateEpicStatus(epic);
        }
        return subtask;
    }

    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.remove(id);
    }

    public void deleteEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return;
        }
        for (Integer subtaskId : epic.getSubtasks()) {
            subtasks.remove(subtaskId);
        }
        epics.remove(id);
    }

    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        epic.getSubtasks().remove((Integer) id);
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    private void updateEpicStatus(Epic epic) {

        if (epic == null) {
            return;
        }

        ArrayList<Integer> subtaskIds = epic.getSubtasks();
        if (subtaskIds.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);

            return;
        }
        int doneCount = 0;

        int newCount = 0;
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) {
                continue;
            }
            if (subtask.getTaskStatus() == TaskStatus.DONE) {
                doneCount++;
            } else if (subtask.getTaskStatus() == TaskStatus.NEW) {
                newCount++;
            }
        }
        if (doneCount == subtaskIds.size()) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (newCount == subtaskIds.size()) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
}