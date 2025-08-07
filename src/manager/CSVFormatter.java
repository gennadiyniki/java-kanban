package manager;

import tasks.*;

import java.time.Duration;
import java.time.LocalDateTime;

public class CSVFormatter {

    public static String toString(Task task) {
        StringBuilder builder = new StringBuilder();
        builder.append(task.getId()).append(",");
        builder.append(task.getType()).append(",");
        builder.append(task.getName()).append(",");
        builder.append(task.getTaskStatus()).append(",");
        builder.append(task.getDescription()).append(",");

        if (task.getType() == TaskType.SUBTASK) {
            builder.append(((Subtask) task).getEpicId()).append(",");
        } else {
            builder.append(","); // Пустое поле для эпика или задачи
        }

        // Добавляем duration и startTime
        if (task.getStartTime() != null) {
            builder.append(task.getDuration().toMinutes()).append(",");
            builder.append(task.getStartTime());
        } else {
            builder.append(","); // Пустые поля, если времени нет
        }

        return builder.toString();
    }

    public static Task fromString(String line) {
        if (line == null || line.trim().isEmpty()) {
            return null;
        }

        String[] parts = line.split(",");
        if (parts.length < 5) {
            return null;
        }

        String type = parts[1].trim();
        TaskStatus taskStatus = TaskStatus.valueOf(parts[3].trim());

        Task task = null;
        switch (type) {
            case "TASK":
                task = parseTask(parts);
                break;
            case "EPIC":
                task = parseEpic(parts);
                break;
            case "SUBTASK":
                task = parseSubtask(parts);
                break;
        }
        return task;
    }

    private static Task parseTask(String[] parts) {
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[2].trim();
        String description = parts[4].trim();

        Task task = new Task(id, name, description, TaskStatus.valueOf(parts[3].trim()));

        if (parts.length >= 8 && !parts[6].trim().isEmpty() && !parts[7].trim().isEmpty()) {
            Duration duration = Duration.ofMinutes(Long.parseLong(parts[6].trim()));
            LocalDateTime startTime = LocalDateTime.parse(parts[7].trim());
            task.setStartTime(startTime);
            task.setDuration(duration);
        }

        return task;
    }

    private static Epic parseEpic(String[] parts) {
        int id = Integer.parseInt(parts[0].trim());
        String name = parts[2].trim();
        String description = parts[4].trim();

        return new Epic(id, name, description, TaskStatus.valueOf(parts[3].trim()));
    }

    private static Subtask parseSubtask(String[] parts) {
        if (parts.length < 6) {
            throw new IllegalArgumentException("Неверный формат строки для Subtask");
        }

        int id = Integer.parseInt(parts[0].trim());
        String name = parts[2].trim();
        String description = parts[4].trim();
        int epicId = Integer.parseInt(parts[5].trim());

        Subtask subtask = new Subtask(id, name, description, epicId, TaskStatus.valueOf(parts[3].trim()));

        if (parts.length >= 8 && !parts[6].trim().isEmpty() && !parts[7].trim().isEmpty()) {
            Duration duration = Duration.ofMinutes(Long.parseLong(parts[6].trim()));
            LocalDateTime startTime = LocalDateTime.parse(parts[7].trim());
            subtask.setStartTime(startTime);
            subtask.setDuration(duration);
        }

        return subtask;
    }
}