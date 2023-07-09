package ba.fet.rwa.endpoints;

import ba.fet.rwa.models.PinValidationConfigurator;
import ba.fet.rwa.models.Player;
import ba.fet.rwa.services.PinService;
import ba.fet.rwa.models.QuizSession;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/ws/quiz", configurator = PinValidationConfigurator.class)
public class PlayerWebsocket {
    String pin;
    QuizSession quizSession;
    Player player;
    @OnOpen
    public void onOpen(Session session) throws IOException {
        // add user to quiz session
        pin = session.getRequestParameterMap().get("pin").get(0);
        quizSession = PinService.getSession(pin);
        if (quizSession == null) {
            session.close();
            return;
        }

        this.player = new Player(session);
        quizSession.addPlayer(this.player);
    }

    @OnMessage
    public void onMessage(Session session, String message) throws IOException {
        if (message.startsWith("player-data:")) {
            String[] nameSurname = message.split(":");
            this.player.setName(nameSurname[1]);
            this.player.setSurname(nameSurname[2]);
            return;
        }
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        quizSession.removePlayer(this.player);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("QuizWebsocket.onError");
    }
}
