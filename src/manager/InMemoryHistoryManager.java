package manager;

import tasks.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final int maxValue = 10;

    private final LinkedList<Task> tasksHistory = new LinkedList<>();;// проверить tasksHistory

    @Override
    public void addTaskToHistory(Task task) {

        // добавит таску в список с историей
        if (tasksHistory.size() == maxValue) {
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
