package http.handler;

import com.sun.net.httpserver.HttpExchange;
import http.HttpTaskServer;
import manager.TaskManager;

import java.io.IOException;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запроса /prioritized от клиента");
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equals("GET")) {
            try {
                String prioritizedList = HttpTaskServer.getGson().toJson(taskManager.getPrioritizedTasks());
                sendText(exchange, prioritizedList, 200);
            } catch (Exception e) {
                sendInternalServerError(exchange);
            }
        } else {
            System.out.println("Ждем метод GET, а получили " + requestMethod);
            exchange.sendResponseHeaders(405, 0);
        }
        exchange.close();
    }
}
