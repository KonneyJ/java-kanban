package http.handler;

import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskException;
import exception.NotFoundException;
import http.HttpTaskServer;
import manager.TaskManager;
import tasks.Epic;
import tasks.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Pattern;

public class EpicHandler extends BaseHttpHandler {
    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запроса /epics от клиента");
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
            if (Pattern.matches("^/subtasks$", exchange.getRequestURI().getPath())) {
                try {
                    List<Subtask> subtaskByEpic = taskManager.getSubtasksByEpic(id);
                    if (subtaskByEpic.isEmpty()) {
                        sendNotFound(exchange, "Подзадачи эпика не найдены");
                    } else {
                        String response = HttpTaskServer.getGson().toJson(subtaskByEpic);
                    }
                } catch (NotFoundException exception) {
                    sendNotFound(exchange, "Подзадачи эпика не найдены");
                }
            } else {
                if (id == null) {
                    List<Epic> epics = taskManager.getEpics();
                    String response = HttpTaskServer.getGson().toJson(epics);
                    sendText(exchange, response, 200);
                } else {
                    Epic epic = taskManager.getEpicById(id);
                    String response = HttpTaskServer.getGson().toJson(epic);
                    sendText(exchange, response, 200);
                }
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Эпик не найден");
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
                Epic epic = HttpTaskServer.getGson().fromJson(body, Epic.class);
                taskManager.updateEpic(epic);
                sendText(exchange, "Эпик успешно обновлен", 201);
            } else {
                Epic epic = HttpTaskServer.getGson().fromJson(body, Epic.class);
                taskManager.createEpic(epic);
                sendText(exchange, "Эпик успешно создан", 201);
            }
        } catch (InvalidTaskException exception) {
            sendHasInteractions(exchange, "Эпик пересекается с другой задачей");
        } catch (Exception exception) {
            sendInternalServerError(exchange);
        }
    }

    public void handleDeleteMethod(HttpExchange exchange) throws IOException {
        Integer id = getIdFromPath(exchange.getRequestURI().getPath());
        try {
            if (id != null) {
                taskManager.deleteEpics(id);
                System.out.println("Удалили эпик с id: " + id);
                exchange.sendResponseHeaders(200, 0);
            } else {
                exchange.sendResponseHeaders(405, 0);
                System.out.println("Получен некорректный id: " + id);
            }
        } catch (NotFoundException exception) {
            sendNotFound(exchange, "Эпик не найден");
        } catch (Exception exception) {
            sendInternalServerError(exchange);
        }
    }
}
