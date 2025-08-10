package httpTaskServer;

import com.sun.net.httpserver.HttpExchange;
import manager.Exception;
import manager.TaskManager;
import tasks.Epic;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class EpicsHandler extends BaseHttpHandler {

    public EpicsHandler(TaskManager taskManager) {
        super(taskManager);
        //this.taskManager = taskManager;
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("epics")) {
            try {
                String response = "";
                if (urlParts.length == 2) {
                    response = gson.toJson(taskManager.getEpics());
                    sendSuccess(exchange, response);
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getEpicById(taskId) != null) {
                        response = gson.toJson(taskManager.getEpicById(taskId));
                        sendSuccess(exchange, response);
                    } else {
                        sendNotFound(exchange, "Эпик " + taskId + " не найден");
                    }
                }
            } catch (Exception.NotFoundException e) {
                sendNotFound(exchange, "Эпик отсуствует");
            }
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("epics")) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String epicString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(epicString, Epic.class);
                if (urlParts.length == 2) {
                    taskManager.addEpic(epic);
                    sendSuccessUpdate(exchange, "Эпик создан");
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getEpicById(taskId) != null) {
                        taskManager.updateEpic(epic);
                        sendSuccessUpdate(exchange, "Эпик " + taskId + " обновлен");
                    } else {
                        sendNotFound(exchange, "Эпик " + taskId + " не найден");
                    }
                }
            } catch (Exception.NotFoundException e) {
                sendNotFound(exchange, "Эпик отсуствует");
            }
        }
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("epics")) {
            try {
                if (urlParts.length == 2) {
                    taskManager.clearSubtasks();
                    sendSuccess(exchange, "Все эпики удалены");
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getEpicById(taskId) != null) {
                        taskManager.deleteEpicById(taskId);
                        sendSuccess(exchange, "Эпик " + taskId + " удален");
                    } else {
                        sendNotFound(exchange, "Эпик " + taskId + " не найден");
                    }
                }
            } catch (Exception.NotFoundException e) {
                sendNotFound(exchange, "Эпик отсуствует");
            }
        }
    }
}



