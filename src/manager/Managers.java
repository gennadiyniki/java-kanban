package manager;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Managers {
    public static TaskManager getDefaultManager() {
        try {
            // Создаем временный файл в системной директории
            Path tempFile = Files.createTempFile("kanban", ".csv");
            return new FileBackedTaskManager(tempFile.toFile());
        } catch (IOException e) {
            System.err.println("Не удалось создать файл, используется in-memory менеджер");
            return getInMemoryManager();
        }
    }

    public static TaskManager getInMemoryManager() {
        return new InMemoryTaskManager() {
            @Override
            public void save() {
                // Пустая реализация для тестов
            }
        };
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}
