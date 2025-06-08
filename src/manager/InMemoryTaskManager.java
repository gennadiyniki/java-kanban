import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generatorId = 1;

    @Override
    public int getGeneratorId() {
        return generatorId++;
    }

    @Override
    public Task addTask(Task task) {
        int taskId = getGeneratorId();
        task.setId(taskId);
        tasks.put(taskId, task);
        return task;
    }

    @Override
    public Epic addEpic(Epic epic) {
        epic.setId(getGeneratorId());
        epics.put(epic.getId(), epic);
        return epic;
    }

    @Override
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

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (!tasks.containsKey(taskID)) {
            return null;
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
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

    @Override
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

    @Override
    public Task getTaskById(int id) {
        return tasks.get(id);
    }

    @Override
    public Epic getEpicById(int id) {
        return epics.get(id);
    }

    @Override
    public Subtask getSubtaskById(int id) {
        return subtasks.get(id);
    }

    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteTasks() {
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }

    @Override
    public void deleteTaskById(int id) {
        if (!tasks.containsKey(id)) {
            return;
        }
        tasks.remove(id);
    }

    @Override
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

    @Override
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