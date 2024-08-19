package http;

import com.google.gson.Gson;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

public class HttpTaskServerTest {
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
    void shouldReturnHistory() throws IOException, InterruptedException {
        Task createdTask1 = new Task("Task1", "Description1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        taskManager.createTask(createdTask1);
        Task createdTask2 = new Task("Task2", "Description2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 15, 0));
        taskManager.createTask(createdTask2);
        URI uri = URI.create("http://localhost:8080/history");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void shouldReturnPrioritizedTask() throws IOException, InterruptedException {
        Task createdTask1 = new Task("Task1", "Description1", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 14, 0));
        taskManager.createTask(createdTask1);
        Task createdTask2 = new Task("Task2", "Description2", Status.NEW,
                Duration.ofMinutes(15), LocalDateTime.of(2024, 8, 19, 15, 0));
        taskManager.createTask(createdTask2);
        URI uri = URI.create("http://localhost:8080/prioritized");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .GET()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(200, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void shouldReturnUncorrectCodeStatusForPrioritizedTask() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/prioritized");

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .DELETE()
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(405, response.statusCode(), "Код ответа не совпадает");
    }

    @Test
    void shouldReturnUncorrectCodeStatusForHistory() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/history");
        String body = gson.toJson("");
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder();
        HttpRequest request = requestBuilder
                .uri(uri)
                .POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
                .build();

        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        assertEquals(405, response.statusCode(), "Код ответа не совпадает");
    }
}
