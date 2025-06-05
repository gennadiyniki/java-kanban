import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class TaskManager {
   private HashMap<Integer, Task> tasks = new HashMap<>();
    private Map<Integer, Epic> epics = new HashMap<>();
    private Map<Integer, Subtask> subtasks = new HashMap<>();

    private int generatorId = 1;

    protected int getGeneratorId() {
        return  generatorId++;
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
        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        updateTask(epic);
        return subtask;
    }

    public void clearSubtasks() {
        subtasks.clear();
    }

    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (!tasks.containsKey(taskID)) {
            return null;
        }
        tasks.put(task.getId(), task);
        return task;
    }

    public Epic updateEpic(Epic epic) {
        Integer epicId = epic.getId();
        if (!epics.containsKey(epicId)) {
            return null;
        }
        epics.put(epic.getId(), epic);
        return epic;
    }

    public Subtask updateSubtask(Subtask subtask) {
        Integer subtaskId = subtask.getId();
        if (!subtasks.containsKey(subtaskId)) {
            return null;
        }
        subtasks.put(subtaskId, subtask);
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
        if (subtask == null) return;
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) return;
        epic.getSubtasks().remove((Integer)id);
        subtasks.remove(id);
        updateEpicStatus(epic);
    }

    public void updateEpicStatus(Epic epic) {
        if (epic == null) {
            return;
        }
        ArrayList<Integer> subtaskIds = epic.getSubtasks();
        if (subtaskIds.isEmpty()) {
            epic.setTaskStatus(TaskStatus.NEW);
            return;
        }
        int DoneCount = 0;
        int NewCount = 0;
        for (Integer subtaskId : subtaskIds) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask == null) {
                continue;
            }
            if (subtask.getTaskStatus() == TaskStatus.DONE) {
                DoneCount++;
            } else if (subtask.getTaskStatus() == TaskStatus.NEW) {
                NewCount++;
            }
        }
        if (DoneCount == subtaskIds.size()) {
            epic.setTaskStatus(TaskStatus.DONE);
        } else if (NewCount == subtaskIds.size()) {
            epic.setTaskStatus(TaskStatus.NEW);
        } else {
            epic.setTaskStatus(TaskStatus.IN_PROGRESS);
        }
    }
    public void updateTaskStatus(int taskId, TaskStatus status) {
        Task task = tasks.get(taskId);
        if (task != null) {
            task.setTaskStatus(status);
        }
    }

    public void updateSubtaskStatus(int subtaskId, TaskStatus taskStatus) {
        Subtask subtask = subtasks.get(subtaskId);
        if (subtask != null) {
            subtask.setTaskStatus(taskStatus);
            int epicId = subtask.getEpicId();
            Epic epic = epics.get(epicId);
            if (epic != null) {
                updateEpicStatus(epic);
            }
        }
    }

}