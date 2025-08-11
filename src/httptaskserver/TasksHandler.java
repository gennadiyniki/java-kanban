package httptaskserver;

import com.sun.net.httpserver.HttpExchange;
import exception.ConflictException;
import exception.NotFoundException;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TasksHandler extends BaseHttpHandler {
    public TasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("tasks")) {
            try {
                String response = "";
                if (urlParts.length == 2) {
                    response = gson.toJson(taskManager.getTasks());
                    sendSuccess(exchange, response);
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getTaskById(taskId) != null) {
                        response = gson.toJson(taskManager.getTaskById(taskId));
                        sendSuccess(exchange, response);
                    } else {
                        sendNotFound(exchange, "Таска " + taskId + " не найдена");
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Такой Таски нет");
            }
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("tasks")) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String taskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Task task = gson.fromJson(taskString, Task.class);
                if (urlParts.length == 2) {
                    taskManager.addTask(task);
                    sendSuccessUpdate(exchange, "Новая задача создана");
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);

                    if (taskId > 0 && taskManager.getTaskById(taskId) != null) {
                        taskManager.updateTask(task);
                        String result = String.format("Задача %s успешно обновлена", taskId);
                        sendSuccessUpdate(exchange, result);
                    } else {
                        String result = String.format("Задача %s не найдена", taskId);
                        sendNotFound(exchange, result);
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Нет такой задачи");
            } catch (ConflictException e) {
                sendHasInteractions(exchange, "В эти даты уже запланирована другая задача");
            }
        }
    }

    @Override
    public void handleDelete(HttpExchange httpExchange) throws IOException {
        String[] urlParts = httpExchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("tasks")) {
            try {
                if (urlParts.length == 2) {
                    taskManager.clearSubtasks();
                    sendSuccess(httpExchange, "Все Таски удалены");
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getTaskById(taskId) != null) {
                        taskManager.deleteTaskById(taskId);
                        sendSuccess(httpExchange, "Таска " + taskId + " удалена");
                    } else {
                        sendNotFound(httpExchange, "Таска " + taskId + " не найдена");
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(httpExchange, " Такой Таски нет");
            }
        }
    }
}