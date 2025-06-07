package tasks;

import java.util.ArrayList;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(int id, String name, String description) {
        super(id, name, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasksId;
    }

    public void addSubtask(int subtaskId) {
        if (!subtasksId.contains(subtaskId)) { // Prevent duplicate IDs
            subtasksId.add(subtaskId);
        }
    }
    public void clearSubtasks() {
        subtasksId.clear(); // Очищаем список ID подзадач
    }
}
