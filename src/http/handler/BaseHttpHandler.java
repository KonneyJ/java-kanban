package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {
    protected TaskManager taskManager;

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        exchange.getResponseBody().write(resp);
        exchange.close();
    }

    public void sendNotFound(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 404);
    }

    public void sendHasInteractions(HttpExchange exchange, String text) throws IOException {
        sendText(exchange, text, 406);
    }

    public void sendInternalServerError(HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(500, 0);
    }

    protected Integer getIdFromPath(String path) {
        String[] parts = path.split("/");
        if (parts.length > 2) {
            return Integer.parseInt(parts[2]);
        }
        return null;
    }
}
