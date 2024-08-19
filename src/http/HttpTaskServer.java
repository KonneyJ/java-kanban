package http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import http.adapter.DurationAdapter;
import http.adapter.LocalDateTimeAdapter;
import http.adapter.TaskStatusAdapter;
import http.handler.*;
import manager.Managers;
import manager.TaskManager;
import tasks.Status;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    public static final int PORT = 8080;
    private final HttpServer httpServer;
    private final TaskManager taskmanager;

    public HttpTaskServer(TaskManager taskmanager) throws IOException {
        this.taskmanager = taskmanager;
        this.httpServer = HttpServer.create(new InetSocketAddress("localhost", PORT), 0);
        this.httpServer.createContext("/tasks", new TaskHandler(taskmanager));
        this.httpServer.createContext("/subtasks", new SubtaskHandler(taskmanager));
        this.httpServer.createContext("/epics", new EpicHandler(taskmanager));
        this.httpServer.createContext("/history", new HistoryHandler(taskmanager));
        this.httpServer.createContext("/prioritized", new PrioritizedHandler(taskmanager));
    }

    public void start() {
        System.out.println("Сервер запущен на порту " + PORT);
        System.out.println("http://localhost:" + PORT);
        httpServer.start();
    }

    public void stop() {
        httpServer.stop(0);
        System.out.println("Остановлен сервер на порту: " + PORT);
    }

    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault();
        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public static Gson getGson() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationAdapter());
        gsonBuilder.registerTypeAdapter(Status.class, new TaskStatusAdapter());
        return gsonBuilder.create();
    }
}
