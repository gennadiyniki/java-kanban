package tasks;

public class Subtask extends tasks.Task {
    private int epicId;

    public Subtask(String name, String description, int epicId) {
        super(name,description);
        this.epicId = epicId;
    }
    public Subtask(int id, String name, String description, tasks.TaskStatus taskStatus, int epicId){
        super (id, name, description, taskStatus); //+epicId + taskStatus
        this.epicId = epicId;

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

