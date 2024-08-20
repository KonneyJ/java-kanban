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

public class HttpTaskManagerEpicTest {
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
    void shouldCreateEpic() throws IOException, InterruptedException, NotFoundException {
        Epic expectedEpic = new Epic("Epic", "Description");
        URI uri = URI.create("http://localhost:8080/epics");
        String body = gson.toJson(expectedEpic);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedEpic).size(), taskManager.getEpics().size(), "Списки эпиков не совпадают");
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException, NotFoundException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        Epic updatedEpic = new Epic("NewEpic", "NewDescription");
        URI uri = URI.create("http://localhost:8080/epics/" + expectedEpic.getId());
        String body = gson.toJson(updatedEpic);

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(201, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedEpic).size(), taskManager.getEpics().size(), "Списки эпиков не совпадают");
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        Epic expectedEpic1 = new Epic("Epic1", "Description1");
        taskManager.createEpic(expectedEpic1);
        Epic expectedEpic2 = new Epic("Epic2", "Description2");
        taskManager.createEpic(expectedEpic2);
        URI uri = URI.create("http://localhost:8080/epics");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedEpic1, expectedEpic2).size(), taskManager.getEpics().size(),
                "Списки эпиков не совпадают");
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        URI uri = URI.create("http://localhost:8080/epics" + expectedEpic.getId());

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        assertEquals(List.of(expectedEpic).size(), taskManager.getEpics().size(),
                "Списки эпиков не совпадают");
    }

    @Test
    void shouldNotGetEpicByNotExistId() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/55");

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
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic expectedEpic = new Epic("Epic", "Description");
        taskManager.createEpic(expectedEpic);
        URI uri = URI.create("http://localhost:8080/epics/0");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
        List<Task> emptyList = new ArrayList<>();
        assertEquals(emptyList.size(), taskManager.getEpics().size(),
                "Списки эпиков не совпадают");
    }

    @Test
    void shouldGetSubtasksByEpic() throws IOException, InterruptedException {
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
        URI uri = URI.create("http://localhost:8080/epics/0/subtasks");

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
    void shouldNotGetSubtasksByEpicByNotExistId() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/epics/0/subtasks");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode(), "Код ответа не совпадает");
    }
}
