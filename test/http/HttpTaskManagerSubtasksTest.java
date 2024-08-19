package http;

import com.google.gson.Gson;
import exception.NotFoundException;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tasks.Epic;
import tasks.Status;
import tasks.Subtask;
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

public class HttpTaskManagerSubtasksTest {
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
    void shouldCreateSubtask() throws IOException, InterruptedException, NotFoundException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        Subtask expectedSubtask = new Subtask("Subtask", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0),
                expectedEpic.getId());
        URI uri = URI.create("http://localhost:8080/subtasks");
        String body = gson.toJson(expectedSubtask);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedSubtask).size(), taskManager.getSubtasks().size(),
                "Списки подзадач не совпадают");
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException, NotFoundException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        Subtask createdSubtask = new Subtask("Subtask", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0),
                expectedEpic.getId());
        taskManager.createSubtask(createdSubtask);
        Subtask updatedSubtask = new Subtask("NewSubtask", "NewDescription", Status.IN_PROGRESS,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0),
                expectedEpic.getId());
        URI uri = URI.create("http://localhost:8080/subtasks/" + createdSubtask.getId());
        String body = gson.toJson(updatedSubtask);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(createdSubtask).size(), taskManager.getSubtasks().size(),
                "Списки подзадач не совпадают");
    }

    @Test
    void shouldNotCreateSubtaskWithIntersaction() throws IOException, InterruptedException, NotFoundException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        Subtask expectedSubtask = new Subtask("Subtask", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0),
                expectedEpic.getId());
        taskManager.createSubtask(expectedSubtask);
        URI uri = URI.create("http://localhost:8080/subtasks");
        String body = gson.toJson(expectedSubtask);

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
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        Subtask expectedSubtask1 = new Subtask("Subtask1", "Description1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0),
                expectedEpic.getId());
        taskManager.createSubtask(expectedSubtask1);
        Subtask expectedSubtask2 = new Subtask("Subtask2", "Description2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 15, 0),
                expectedEpic.getId());
        taskManager.createSubtask(expectedSubtask2);
        URI uri = URI.create("http://localhost:8080/subtasks");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedSubtask1, expectedSubtask2).size(), taskManager.getSubtasks().size(),
                "Списки подзадач не совпадают");
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        Subtask expectedSubtask = new Subtask("Subtask", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0),
                expectedEpic.getId());
        taskManager.createSubtask(expectedSubtask);
        URI uri = URI.create("http://localhost:8080/subtasks/1");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedSubtask).size(), taskManager.getSubtasks().size(),
                "Списки подзадач не совпадают");

    }

    @Test
    void shouldNotGetSubtaskByNotExistId() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/55");

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
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        Subtask expectedSubtask = new Subtask("Subtask", "Description", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0),
                expectedEpic.getId());
        taskManager.createSubtask(expectedSubtask);
        URI uri = URI.create("http://localhost:8080/subtasks/1");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList.size(), taskManager.getSubtasks().size(),
                "Списки подзадач не совпадают");
    }
}
