package tasks;

import java.util.ArrayList;
import java.time.Duration;
import java.time.LocalDateTime;

public class Epic extends Task {
    private ArrayList<Integer> subtasksId = new ArrayList<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {

        super(id, name, description, TaskStatus.NEW);
    }

    public Epic(int id, String name, String description, TaskStatus status) {
        super(id, name, description, status);
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

    @Override
    public TaskType getType() {

        return TaskType.EPIC;
    }


    @Override
    public Duration getDuration() {
        if (duration != null) {
            return duration;
        } else {
            return Duration.ofMinutes(0);
        }
    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}

