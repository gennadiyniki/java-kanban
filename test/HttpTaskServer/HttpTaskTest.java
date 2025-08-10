package HttpTaskServer;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Subtask;
import tasks.Task;
import tasks.TaskStatus;


import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class HttpTaskTest {

    protected TaskManager manager = Managers.getDefaultManager();

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
    HttpTaskServer httpTaskServer;
    Gson gson = BaseHttpHandler.getGson();
    String url = "http://localhost:8080";

    @BeforeEach
    void beforeEach() throws IOException {
        // Инициализация менеджера и HTTP сервера
        manager = Managers.getDefaultManager();
        httpTaskServer = new HttpTaskServer(manager);
        httpTaskServer.start();

        // Создаем и добавляем задачи
        Task task1 = new Task(1, "Таска 1", "Описание Таски 1", TaskStatus.NEW,
                LocalDateTime.parse("10.08.2025 00:00", dateTimeFormatter),
                Duration.ofMinutes(100));
        manager.addTask(task1);

        Task task2 = new Task(2, "Таска 2", "Описание Таски 2", TaskStatus.NEW,
                LocalDateTime.parse("11.08.2025 00:00", dateTimeFormatter),
                Duration.ofMinutes(100));
        manager.addTask(task2);

        Epic epic1 = new Epic(3, "Эпик 1", "Описание Эпика 1");
        manager.addEpic(epic1);

        Epic epic2 = new Epic(4, "Эпик 2", "Описание Эпика 2");
        manager.addEpic(epic2);


        Subtask subtask1 = new Subtask(5, "Сабтаска 1", "Описание Сабтаски 1",
                epic1.getId(), TaskStatus.NEW,
                LocalDateTime.parse("12.08.2025 10:00", dateTimeFormatter),
                Duration.ofMinutes(30));
        manager.addSubtask(subtask1);

        Subtask subtask2 = new Subtask(6, "Сабтаска 2", "Описание Сабтаски 2",
                epic1.getId(), TaskStatus.NEW,
                LocalDateTime.parse("13.08.2025 00:00", dateTimeFormatter),
                Duration.ofMinutes(10));
        manager.addSubtask(subtask2);
    }

    @AfterEach
    void afterEach() {
        httpTaskServer.stop();
    }

    @Test
    public void testReturnAllTasks() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlTask = URI.create(url + "/tasks");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlTask)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        List<Task> tasks = gson.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());
        assertNotNull(tasks, "Таски не выводятся");
        assertEquals(manager.getTasks(), tasks, "Некорректное количество Тасок");
        assertEquals(200, response.statusCode(), "КодСтатус неверный");
    }

    @Test
    public void testCreateNewTask() throws IOException, InterruptedException {
        Task newTask = new Task(7, "Таска 3", "Описание Таски 3", TaskStatus.NEW,
                LocalDateTime.parse("14.08.2025 00:00", dateTimeFormatter),
                Duration.ofMinutes(100));

        // Отправка запроса с таймаутом
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url + "/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(gson.toJson(newTask)))
                .timeout(Duration.ofSeconds(5))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode(), "КодСтатус неверный");
        assertNotNull(response.body(), "В теле ответа ничего нет");

        List<Task> tasks = manager.getTasks();
        Task createdTask = tasks.stream()
                .filter(t -> t.getName().equals("Таска 3"))
                .findFirst()
                .orElse(null);

        assertNotNull(createdTask, "Таска не найдена в менеджере");
        assertEquals("Описание Таски 3", createdTask.getDescription());
    }

    @Test
    public void testDeleteTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI urlTask = URI.create(url + "/tasks/1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlTask)
                .DELETE()
                .build();
        assertEquals(2, manager.getTasks().size(), "Список Тасок не корректный");
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode(), "КодСтатус неверный");
        assertEquals(1, manager.getTasks().size(), "Список Тасок не корректный");
    }

    @Test
    public void testReturnEpicById() throws IOException, InterruptedException {
        HttpClient httpClient = HttpClient.newHttpClient();
        URI urlEpic = URI.create(url + "/epics/3");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(urlEpic)
                .GET()
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        Epic epic = gson.fromJson(response.body(), new TypeToken<Epic>() {
        }.getType());
        assertEquals(manager.getEpicById(3), epic, "Эпики не совпадают");
        assertEquals(200, response.statusCode(), "КодСтатус неверный");
    }
}