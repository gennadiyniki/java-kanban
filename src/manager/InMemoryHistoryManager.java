package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private final List<Task>tasksHistory = new ArrayList<>();// проверить tasksHistory

    @Override
    public void addTaskToHistory(Task task) {
        // добавит таску в список с историей
        if (tasksHistory.size() == 10) {
            tasksHistory.remove(0);
        }
        tasksHistory.add(task);
    }

    @Override
    public List<Task> getHistory() {
        // получить список историй
        return new ArrayList<>(tasksHistory);
    }
}
