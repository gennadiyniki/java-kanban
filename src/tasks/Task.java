package tasks;

import java.time.Duration;
import java.util.Objects;
import java.time.LocalDateTime;


public class Task {
    protected TaskStatus taskStatus;
    protected int id;
    protected String name;
    protected String description;
    protected LocalDateTime startTime;
    protected Duration duration;


    public Task(int id, String name, String description, TaskStatus taskStatus, LocalDateTime startTime,
                Duration duration) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
        this.startTime = startTime;
        this.duration = duration;
    }

    public Task(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public Task(int id, String name, String description, TaskStatus taskStatus) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.taskStatus = taskStatus;
    }

    public TaskType getType() {

        return TaskType.TASK;
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

    public String toFileString() {
        String[] string = {Integer.toString(getId()),
                getName(),
                getTaskStatus().toString(),
                getDescription(),
                "",
                String.valueOf(getDuration().toMinutes()),
                String.valueOf(getStartTime())};
        return String.join(",", string);
    }

    public LocalDateTime getStartTime() {
        if (startTime == null || duration == null) {
            return null;
        }
        return startTime.plus(duration);
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(duration);
    }

    public boolean hasIntersectionWith(Task other) {
        if (this.getStartTime() == null || other.getStartTime() == null ||
                this.getDuration() == null || other.getDuration() == null) {
            return false;
        }

        LocalDateTime thisEnd = this.getStartTime().plus(this.getDuration());
        LocalDateTime otherEnd = other.getStartTime().plus(other.getDuration());

        return this.getStartTime().isBefore(otherEnd) &&
                other.getStartTime().isBefore(thisEnd);
    }

    @Override
    public String toString() {
        return "Task{" +
                "taskStatus=" + taskStatus +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }
}

