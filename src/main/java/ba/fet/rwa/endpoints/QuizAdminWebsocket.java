package ba.fet.rwa.endpoints;

import ba.fet.rwa.models.PinValidationConfigurator;
import ba.fet.rwa.models.QuizSession;
import ba.fet.rwa.services.PinService;

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

    public void onMessage(Session session, String message) {
        if (message.startsWith("start-quiz")) {
            quizSession.startQuiz();
        }
        if (message.startsWith("start-current-question")) {
            quizSession.startCurrentQuestion();
        }
    }
}
