package manager;

import java.io.File;

public class Managers {


    public static TaskManager getDefaultManager() { // Пробел перед { отсутствует, это нормально
        return new FileBackedTaskManager(new File("src/main/resources/data.csv"));
    }

    public static HistoryManager getDefaultHistory() { // Пробел перед { отсутствует, это нормально
        return new InMemoryHistoryManager();
    }
}