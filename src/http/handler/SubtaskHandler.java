package http.handler;

import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskException;
import exception.NotFoundException;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class SubtaskHandler extends BaseHttpHandler {
    public SubtaskHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запроса /subtasks от клиента");
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
                List<Subtask> subtasks = taskManager.getSubtasks();
                String response = HttpTaskServer.getGson().toJson(subtasks);
                sendText(exchange, response, 200);
            } else {
                Subtask subtask = taskManager.getSubtaskById(id);
                String response = HttpTaskServer.getGson().toJson(subtask);
                sendText(exchange, response, 200);
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Подзадача не найдена");
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
                Subtask subtask = HttpTaskServer.getGson().fromJson(body, Subtask.class);
                taskManager.updateSubtask(subtask);
                sendText(exchange, "Подзадача успешно обновлена", 201);
            } else {
                Subtask subtask = HttpTaskServer.getGson().fromJson(body, Subtask.class);
                taskManager.createSubtask(subtask);
                sendText(exchange, "Подзадача успешно создана", 201);
            }
        } catch (InvalidTaskException exception) {
            sendHasInteractions(exchange, "Подзадача пересекается с другой задачей");
        } catch (Exception exception) {
            sendInternalServerError(exchange);
        }
    }

    public void handleDeleteMethod(HttpExchange exchange) throws IOException {
        Integer id = getIdFromPath(exchange.getRequestURI().getPath());
        try {
            if (id != null) {
                taskManager.deleteSubtask(id);
                System.out.println("Удалили подзадачу с id: " + id);
                exchange.sendResponseHeaders(200, 0);
            } else {
                exchange.sendResponseHeaders(405, 0);
                System.out.println("Получен некорректный id: " + id);
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Подзадача не найдена");
        } catch (Exception exception) {
            sendInternalServerError(exchange);
        }
    }
}
