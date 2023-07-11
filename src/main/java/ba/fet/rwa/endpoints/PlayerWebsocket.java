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
        String[] parts = message.split(":", 2);
        String messageType = parts[0];
        String messageContent = parts[1];
        switch (MessageType.valueOf(messageType)) {
            case PLAYER_DATA:
                setPlayerData(messageContent);
                break;
            case ANSWER:
                Long answerId = Long.parseLong(messageContent);
                Long questionId = quizSession.getCurrentQuestion().getId();
                player.addAnswer(questionId, answerId);
                break;
            default:
                break;
        }
    }

    private void setPlayerData(String messageContent) {
        String[] nameSurname = messageContent.split(":", 2);
        this.player.setName(nameSurname[0]);
        this.player.setSurname(nameSurname[1]);
    }

    @OnClose
    public void onClose(Session session) throws IOException {
        quizSession.removePlayer(this.player);
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("QuizWebsocket.onError");
    }

    private enum MessageType {
        PLAYER_DATA,
        ANSWER
    }
}
