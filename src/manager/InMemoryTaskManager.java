package manager;

import exception.ConflictException;
import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

public abstract class InMemoryTaskManager implements TaskManager {
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager historyManager = Managers.getDefaultHistory();
    private final Comparator<Task> taskComparator = Comparator.comparing(Task::getStartTime);
    protected Set<Task> prioritizedTasks = new TreeSet<>(taskComparator);

    protected int generatorId = 1;

    public void printTasks() {
        System.out.println("Список всех задач:");
        for (Task task : tasks.values()) {
            System.out.println(task.getName());
            System.out.println(task.getId());
        }
        for (Epic epic : epics.values()) {
            System.out.println(epic.getName());
            System.out.println(epic.getId());
        }
        for (Subtask subtask : subtasks.values()) {
            System.out.println(subtask.getName());
            System.out.println(subtask.getId());
        }
    }

    @Override
    public int getGeneratorId() {
        return generatorId++;
    }


    @Override
    public Task addTask(Task task) {
        if (task.getStartTime() != null && hasTimeIntersection(task)) {
            throw new ConflictException("Обнаружено пересечение по времени");
        }

        task.setId(getGeneratorId());
        tasks.put(task.getId(), task);

        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
        }

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
        if (!epics.containsKey(subtask.getEpicId())) {
            return null;
        }

        if (subtask.getStartTime() != null && subtask.getDuration() != null) {
            if (hasTimeIntersection(subtask)) {
                throw new ConflictException("Обнаружено пересечение времени!");
            }
            prioritizedTasks.add(subtask);
        }

        subtask.setId(getGeneratorId());
        subtasks.put(subtask.getId(), subtask);

        Epic epic = epics.get(subtask.getEpicId());
        epic.addSubtask(subtask.getId());
        updateEpicStatus(epic);
        changeEpicTiming(epic);

        return subtask;
    }

    @Override
    public void clearSubtasks() {
        subtasks.clear();
        prioritizedTasks.removeIf(task -> task.getType() == TaskType.SUBTASK);
        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
            changeEpicTiming(epic);
        }
    }

    // 3. Безопасное сохранение с атомарной записью
    public abstract void save();

    @Override
    public Task updateTask(Task task) {
        Integer taskID = task.getId();
        if (!tasks.containsKey(taskID)) {
            return null;
        }
        if (task.getStartTime() != null && hasTimeIntersection(task)) {
            throw new ConflictException("Обнаружено пересечение по времени");
        }
        prioritizedTasks.removeIf(t -> t.getId() == task.getId());
        if (task.getStartTime() != null) {
            prioritizedTasks.add(task);
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
            changeEpicTiming(epic);
        }
        return subtask;
    }

    @Override
    public Task getTaskById(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
            return subtask;
        } else {
            return null;
        }
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
    public void deleteTask(int id) {
        if (tasks.containsKey(id)) {
            Task task = tasks.remove(id);
            historyManager.remove(id);
            prioritizedTasks.remove(task);
        } else {
            System.out.println("Такой задачи нет");
        }
    }

    @Override
    public void deleteEpics() {
        epics.clear();
        subtasks.clear();

    }

    @Override
    public void deleteSubtasks() {
        for (Subtask subtask : subtasks.values()) {
            historyManager.remove(subtask.getId());
        }
        subtasks.clear();

        for (Epic epic : epics.values()) {
            epic.clearSubtasks();
            updateEpicStatus(epic);
        }
    }


    @Override
    public void deleteTaskById(int id) {
        Task task = tasks.remove(id);
        if (!tasks.containsKey(id)) {
            return;
        }
        historyManager.remove(id);
        prioritizedTasks.remove(task);
    }


    @Override
    public void deleteEpicById(int id) {
        Epic epic = epics.remove(id); // 1. Удаляем эпик из основной мапы
        if (epic == null) return;

        // 3. Удаляем все подзадачи этого эпика
        for (Integer subtaskId : epic.getSubtasks()) {
            Subtask subtask = subtasks.remove(subtaskId);
            if (subtask != null) {
                prioritizedTasks.remove(subtask);
                historyManager.remove(subtaskId);
            }
        }

        historyManager.remove(id); // 4. Удаляем эпик из истории
    }

    @Override
    public void deleteSubtaskById(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask == null) {
            return;
        }
        historyManager.remove(id);
        int epicId = subtask.getEpicId();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return;
        }
        epic.getSubtasks().remove((Integer) id);
        subtasks.remove(id);
        historyManager.remove(id);
        prioritizedTasks.remove(subtask);
        updateEpicStatus(epic);
        changeEpicTiming(epic);
    }

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }

    @Override
    public Set<Integer> getSubtasksById(int id) {
        Epic epic = epics.get(id);
        if (epic == null) {
            return Collections.emptySet();
        }
        return new HashSet<>(epic.getSubtasks());
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return List.of();
    }

    protected void updateEpicStatus(Epic epic) {

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

    private boolean isTimeOverlap(Task task1, Task task2) {
        if (task1.getStartTime() == null || task2.getStartTime() == null) {
            return false;
        }
        LocalDateTime start1 = task1.getStartTime();
        LocalDateTime end1 = start1.plus(task1.getDuration());
        LocalDateTime start2 = task2.getStartTime();
        LocalDateTime end2 = start2.plus(task2.getDuration());

        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    protected void changeEpicTiming(Epic epic) {
        // Получаем все подзадачи эпика через доступные методы
        List<Subtask> epicSubtasks = new ArrayList<>();
        for (Integer subtaskId : epic.getSubtasks()) {
            Subtask subtask = subtasks.get(subtaskId);
            if (subtask != null) {
                epicSubtasks.add(subtask);
            }
        }

        // Фильтруем подзадачи с указанным временем
        List<Subtask> subtasksWithTime = epicSubtasks.stream()
                .filter(subtask -> subtask.getStartTime() != null && subtask.getDuration() != null)
                .toList();

        // Если нет подзадач с временем - сбрасываем время эпика
        if (subtasksWithTime.isEmpty()) {
            epic.setStartTime(null);
            epic.setEndTime(null);
            epic.setDuration(null);
            return;
        }

        //  самое раннее время начала
        LocalDateTime earliestStart = subtasksWithTime.get(0).getStartTime();
        // самое позднее время окончания
        LocalDateTime latestEnd = subtasksWithTime.get(0).getEndTime();
        // продолжительность
        Duration totalDuration = subtasksWithTime.get(0).getDuration();

        for (int i = 1; i < subtasksWithTime.size(); i++) {
            Subtask current = subtasksWithTime.get(i);

            // Обновляем самое раннее время начала
            if (current.getStartTime().isBefore(earliestStart)) {
                earliestStart = current.getStartTime();
            }

            // Обновляем самое позднее время окончания
            if (current.getEndTime().isAfter(latestEnd)) {
                latestEnd = current.getEndTime();
            }

            // Суммируем продолжительность
            totalDuration = totalDuration.plus(current.getDuration());
        }

        // Устанавливаем время для эпика
        epic.setStartTime(earliestStart);
        epic.setEndTime(latestEnd);
        epic.setDuration(Duration.between(earliestStart, latestEnd));
    }

    protected void addToPrioritizedTasks(Task task) {
        prioritizedTasks.add(task);
    }

    protected boolean checkIntersections(Task task) {

        LocalDateTime startOfTask = task.getStartTime();
        LocalDateTime endOfTask = task.getEndTime();


        return prioritizedTasks.stream()
                .filter(prioritizedTask -> prioritizedTask.getStartTime() != null)
                .filter(prioritizedTask -> !prioritizedTask.equals(task))
                .anyMatch(prioritizedTask ->

                        (prioritizedTask.getStartTime().isEqual(startOfTask) || prioritizedTask.getStartTime().isBefore(startOfTask)) && prioritizedTask.getEndTime().isAfter(startOfTask)
                                || (startOfTask.isEqual(prioritizedTask.getStartTime()) || startOfTask.isBefore(prioritizedTask.getStartTime())) && endOfTask.isAfter(prioritizedTask.getStartTime())

                );

    }

    private boolean hasTimeIntersection(Task newTask) {
        if (newTask.getStartTime() == null || newTask.getDuration() == null) {
            return false;
        }

        LocalDateTime newStart = newTask.getStartTime();
        LocalDateTime newEnd = newStart.plus(newTask.getDuration());

        return prioritizedTasks.stream()
                .filter(task -> task != newTask)
                .filter(task -> task.getStartTime() != null)
                .filter(task -> task.getDuration() != null)
                .anyMatch(task -> {
                    LocalDateTime existingStart = task.getStartTime();
                    LocalDateTime existingEnd = existingStart.plus(task.getDuration());
                    return newStart.isBefore(existingEnd) && existingStart.isBefore(newEnd);
                });
    }
}