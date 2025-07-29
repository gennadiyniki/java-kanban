package manager;

import java.io.File;

public class Managers {

    public static TaskManager getDefaultManager() {
        return new FileBackedTaskManager(
                new File("/Users/gennadiyniki/IdeaProjects/java-kanban/src/resources/data.csv")
        );
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}