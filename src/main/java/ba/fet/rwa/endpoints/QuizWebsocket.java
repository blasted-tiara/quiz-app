package ba.fet.rwa.endpoints;

import ba.fet.rwa.models.PinValidationConfigurator;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/ws/quiz", configurator = PinValidationConfigurator.class)
public class QuizWebsocket {
    @OnOpen
    public void onOpen(Session session) throws IOException {
        System.out.println("QuizWebsocket.onOpen");
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        System.out.println("QuizWebsocket.onMessage");
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        System.out.println("QuizWebsocket.onClose");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("QuizWebsocket.onError");
    }
}
