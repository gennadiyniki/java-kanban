package manager;

public class Managers {


    public static TaskManager getDefaultManager() { // Пробел перед { отсутствует, это нормально
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() { // Пробел перед { отсутствует, это нормально
        return new InMemoryHistoryManager();
    }
}