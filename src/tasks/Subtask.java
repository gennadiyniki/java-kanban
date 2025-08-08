package tasks;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {
    private int epicId;


    public Subtask(int id, String name, String description, int epicId, TaskStatus taskStatus) {
        super(id, name, description, taskStatus);
        this.epicId = epicId;
    }

    public Subtask(int id, String name, String description, int epicId, TaskStatus taskStatus, LocalDateTime startTime, Duration duration) {
        super(id, name, description, taskStatus, startTime, duration);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }


    @Override
    public TaskType getType() {

        return TaskType.SUBTASK;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getTaskStatus() +
                ", epicId=" + epicId +
                ", duration='" + getDuration().toMinutes() + '\'' +
                ", startTime='" + startTime +
                '}';
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }
}