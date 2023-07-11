package ba.fet.rwa.endpoints;

import ba.fet.rwa.models.PinValidationConfigurator;
import ba.fet.rwa.models.QuizSession;
import ba.fet.rwa.services.PinService;

import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

@ServerEndpoint(value = "/admin/ws/quiz", configurator = PinValidationConfigurator.class)
public class QuizAdminWebsocket {
    String pin;
    QuizSession quizSession;
    boolean sessionExists = false;

    @OnOpen
    public void onOpen(Session session) {
        pin = session.getRequestParameterMap().get("pin").get(0);
        quizSession = PinService.getSession(pin);
        if (quizSession == null) {
            try {
                session.close();
            } catch (Exception e) {
                System.out.println("Error closing session: " + e.getMessage());
            }
            return;
        }
        try {
            quizSession.setOwnerSession(session);
        } catch (Exception e) {
            sessionExists = true;
        }

        if (sessionExists) {
            try {
                session.close();
            } catch (Exception e) {
                System.out.println("Error closing session: " + e.getMessage());
            }
        }
    }

    @OnMessage
    public void onMessage(Session session, String message) {
        String[] parts = message.split(":", 2);
        String messageType = parts[0];
        String messageContent;
        try {
            messageContent = parts[1];
        } catch (Exception e) {
            messageContent = "";
        }

        switch (MessageType.valueOf(messageType)) {
            case START_QUIZ:
                quizSession.startQuiz();
                break;
            case NEXT_QUESTION:
                quizSession.startCurrentQuestion();
                break;
            default:
                break;
        }
    }

    private enum MessageType {
        START_QUIZ,
        NEXT_QUESTION,
    }
}
