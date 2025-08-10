package httptaskserver;

import com.sun.net.httpserver.HttpExchange;
import exception.NotFoundException;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;


public class SubtasksHandler extends BaseHttpHandler {
    public SubtasksHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handleGet(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("subtasks")) {
            try {
                String response = "";
                if (urlParts.length == 2) {
                    response = gson.toJson(taskManager.getSubtasks());
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getSubtaskById(taskId) != null) {
                        response = gson.toJson(taskManager.getSubtaskById(taskId));
                    } else {
                        sendNotFound(exchange, "Сабтаска " + taskId + " не найдена");
                    }
                }
                sendSuccess(exchange, response);
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Такой Сабтаски нет");
            }
        }
    }

    @Override
    public void handlePost(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("subtasks")) {
            try {
                InputStream inputStream = exchange.getRequestBody();
                String subtaskString = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(subtaskString, Subtask.class);
                if (urlParts.length == 2) {
                    taskManager.addSubtask(subtask);
                    sendSuccessUpdate(exchange, "Создана новая Сабтаска");
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getSubtaskById(taskId) != null) {
                        taskManager.updateSubtask(subtask);
                        sendSuccessUpdate(exchange, "Сабтаска " + taskId + " обновлена");
                    } else {
                        sendNotFound(exchange, "Сабтаска " + taskId + " не найдена");
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Такой Сабтаски нет");
            }
        }
    }

    @Override
    public void handleDelete(HttpExchange exchange) throws IOException {
        String[] urlParts = exchange.getRequestURI().getPath().split("/");
        if (urlParts[1].equals("subtasks")) {
            try {
                if (urlParts.length == 2) {
                    taskManager.clearSubtasks();
                    sendSuccess(exchange, "Все подзадачи удалены");
                }
                if (urlParts.length == 3) {
                    int taskId = Integer.parseInt(urlParts[2]);
                    if (taskId > 0 && taskManager.getSubtaskById(taskId) != null) {
                        taskManager.deleteSubtaskById(taskId);
                        String result = String.format("Подзадача %s удалена", taskId);
                        sendSuccess(exchange, result);
                    } else {
                        String result = String.format("Подзадача %s не найдена", taskId);
                        sendNotFound(exchange, result);
                    }
                }
            } catch (NotFoundException e) {
                sendNotFound(exchange, "Нет такой подзадачи");
            }
        }
    }
}