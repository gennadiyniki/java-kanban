package tasks;

import java.util.ArrayList;

public class Epic extends tasks.Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();

    public Epic(int id, String name, String description) {

        super(id, name, description, TaskStatus.NEW);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasksId;
    }

    public void addSubtask(int subtaskId) {
        if (!subtasksId.contains(subtaskId)) {
            subtasksId.add(subtaskId);
        }
    }
    public void clearSubtasks() {
        subtasksId.clear();
    }
}
