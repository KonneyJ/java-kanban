package http;

import com.google.gson.Gson;
import exception.NotFoundException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Status;
import tasks.Task;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskManagerTasksTest {
    TaskManager taskManager;
    HttpTaskServer httpTaskServer;
    Gson gson;
    HttpClient client = HttpClient.newHttpClient();

    @BeforeEach
    public void startServer() throws IOException {
        // создаём экземпляр InMemoryTaskManager
        taskManager = new InMemoryTaskManager();
        // передаём его в качестве аргумента в конструктор HttpTaskServer
        httpTaskServer = new HttpTaskServer(taskManager);
        gson = HttpTaskServer.getGson();
        httpTaskServer.start();
    }

    @AfterEach
    public void stopServer() {
        httpTaskServer.stop();
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException, NotFoundException {
        Task expectedTask = new Task("Task", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        URI uri = URI.create("http://localhost:8080/tasks");
        String body = gson.toJson(expectedTask);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        Task actualTask = taskManager.getTaskById(expectedTask.getId());
        assertEquals(expectedTask, actualTask, "Задачи не совпадают");
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException, NotFoundException {
        Task createdTask = new Task("Task", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        taskManager.createTask(createdTask);
        Task updatedTask = new Task("NewTask", "NewDescription", Status.IN_PROGRESS,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        URI uri = URI.create("http://localhost:8080/tasks/" + createdTask.getId());
        String body = gson.toJson(updatedTask);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(updatedTask, taskManager.getTaskById(updatedTask.getId()), "Задачи не совпадают");
    }

    @Test
    void shouldNotCreateTaskWithIntersaction() throws IOException, InterruptedException, NotFoundException {
        Task expectedTask = new Task("Task", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        taskManager.createTask(expectedTask);
        URI uri = URI.create("http://localhost:8080/tasks");
        String body = gson.toJson(expectedTask);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(406, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void shouldGetAllTasks() throws IOException, InterruptedException {
        Task expectedTask1 = new Task("Task1", "Description1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        taskManager.createTask(expectedTask1);
        Task expectedTask2 = new Task("Task2", "Description2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 15, 0));
        taskManager.createTask(expectedTask2);
        URI uri = URI.create("http://localhost:8080/tasks");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedTask1, expectedTask2).size(), taskManager.getTasks().size(),
                "Списки задач не совпадают");
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task expectedTask = new Task("Task", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        taskManager.createTask(expectedTask);
        URI uri = URI.create("http://localhost:8080/tasks" + expectedTask.getId());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(expectedTask, taskManager.getTaskById(expectedTask.getId()), "Задачи не совпадают");

    }

    @Test
    void shouldNotGetTaskByNotExistId() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks/55");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void shouldDeleteTaskById() throws IOException, InterruptedException {
        Task expectedTask = new Task("Task", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        taskManager.createTask(expectedTask);
        URI uri = URI.create("http://localhost:8080/tasks/0");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList.size(), taskManager.getTasks().size(),
                "Списки задач не совпадают");
    }
}