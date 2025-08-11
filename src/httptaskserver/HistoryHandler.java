package httptaskserver;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        try {
            String history = gson.toJson(taskManager.getHistory());
            sendSuccess(exchange, history);

        } catch (NotFoundException e) {
            sendNotFound(exchange, "История не найдена");
        }
    }
}