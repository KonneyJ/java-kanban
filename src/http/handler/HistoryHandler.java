package http.handler;

import com.sun.net.httpserver.HttpExchange;
import http.HttpTaskServer;
import manager.TaskManager;

import java.io.IOException;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Началась обработка запроса /history от клиента");
        String requestMethod = exchange.getRequestMethod();
        if (requestMethod.equals("GET")) {
            try {
                String historyList = HttpTaskServer.getGson().toJson(taskManager.getHistory());
                sendText(exchange, historyList, 200);
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
