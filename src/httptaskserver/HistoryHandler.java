package httptaskserver;

import com.sun.net.httpserver.HttpExchange;
import manager.Exception;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
        //this.taskManager = taskManager;
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        try {
            String history = gson.toJson(taskManager.getHistory());
            sendSuccess(exchange, history);

        } catch (Exception.NotFoundException e) {
            sendNotFound(exchange, "История не найдена");
        }
    }
}