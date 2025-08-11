package httptaskserver;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;


import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {
    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange httpExchange) throws IOException {
        try {
            String prioritized = gson.toJson(taskManager.getPrioritizedTasks());
            sendSuccess(httpExchange, prioritized);
        } catch (NotFoundException e) {
            sendNotFound(httpExchange, "Отсортированный список не найден");
        }
    }
}