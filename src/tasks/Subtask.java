package tasks;

public class Subtask extends tasks.Task {
    private int epicId;

    public Subtask(int id, String name, String description, int epicId, TaskStatus taskStatus) {
        super(id, name, description, taskStatus);
        this.epicId = epicId;
    }

    @Override
    public TaskType getType() {
        return TaskType.SUBTASK;
    }

    public int getEpicId() {
        return epicId;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getTaskStatus() +
                ", epicId=" + epicId +
                '}';

    }
}