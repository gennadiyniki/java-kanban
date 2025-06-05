package tasks;
import java.util.Objects;

public class Task {
    protected  TaskStatus taskStatus;
    protected  int id;
    protected String name;
    protected String description;


    public Task(int id, String name, String description, TaskStatus taskStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
    }
    public  Task(String name, String description){
        this.name = name;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    public TaskStatus getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(TaskStatus taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

