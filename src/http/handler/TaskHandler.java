package http.handler;

import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskException;
import exception.NotFoundException;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Task;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запроса /tasks от клиента");
        try {
            String requestMethod = exchange.getRequestMethod();
            switch (requestMethod) {
                case "GET":
                    handleGetMethod(exchange);
                    break;
                case "POST":
                    handlePostMethod(exchange);
                    break;
                case "DELETE":
                    handleDeleteMethod(exchange);
                    break;
                default:
                    System.out.println("Ждем метод GET, POST или DELETE, а получили" + requestMethod);
                    exchange.sendResponseHeaders(405, 0);
                    break;
            }
        } catch (RuntimeException exception) {
            exception.printStackTrace();
        } finally {
            exchange.close();
        }
    }

    public void handleGetMethod(HttpExchange exchange) throws IOException {
        Integer id = getIdFromPath(exchange.getRequestURI().getPath());
        try {
            if (id == null) {
                List<Task> tasks = taskManager.getTasks();
                String response = HttpTaskServer.getGson().toJson(tasks);
                sendText(exchange, response, 200);
            } else {
                Task task = taskManager.getTaskById(id);
                String response = HttpTaskServer.getGson().toJson(task);
                sendText(exchange, response, 200);
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange,"Задача не найдена");
        } catch (Exception exception) {
            sendInternalServerError(exchange);
        }
    }

    public void handlePostMethod(HttpExchange exchange) throws IOException {
        Integer id = getIdFromPath(exchange.getRequestURI().getPath());
        InputStream stream = exchange.getRequestBody();
        String body = new String(stream.readAllBytes());
        try {
            if (id != null) {
                Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
                taskManager.updateTask(task);
                sendText(exchange, "Задача успешно обновлена", 201);
            } else {
                Task task = HttpTaskServer.getGson().fromJson(body, Task.class);
                taskManager.createTask(task);
                sendText(exchange, "Задача успешно создана", 201);
            }
        } catch (InvalidTaskException exception) {
            sendHasInteractions(exchange, "Задача пересекается с другой задачей");
        } catch (Exception exception) {
            sendInternalServerError(exchange);
        }
    }

    public void handleDeleteMethod(HttpExchange exchange) throws IOException {
        Integer id = getIdFromPath(exchange.getRequestURI().getPath());
        try {
            if (id != null) {
                taskManager.deleteTask(id);
                System.out.println("Удалили задачу с id: " + id);
                exchange.sendResponseHeaders(200, 0);
            } else {
                exchange.sendResponseHeaders(405, 0);
                System.out.println("Получен некорректный id: " + id);
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange,"Задача не найдена");
        } catch (Exception exception) {
            sendInternalServerError(exchange);
        }
    }
}