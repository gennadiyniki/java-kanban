package httpTaskServer;

import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import manager.Managers;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager taskManager;

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        try {
            httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
            httpServer.createContext("/tasks", new TasksHandler(taskManager));
            httpServer.createContext("/subtasks", new SubtasksHandler(taskManager));
            httpServer.createContext("/epics", new EpicsHandler(taskManager));
            httpServer.createContext("/history", new HistoryHandler(taskManager));
            httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
        } catch (IOException exception) {
            throw new RuntimeException("Ошибка создания сервера на порту " + PORT);
        }
    }

    public void start() {
        httpServer.start();
        System.out.printf("Сервер запущен на %s порту ", PORT);
    }

    public void stop() {
        httpServer.stop(1);
        System.out.printf("Сервер остановлен на %s порту ", PORT);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefaultManager();
        HttpTaskServer taskServer = new HttpTaskServer(taskManager);
        taskServer.start();
        taskServer.stop();
    }
}