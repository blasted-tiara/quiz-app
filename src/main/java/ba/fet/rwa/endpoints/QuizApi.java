package ba.fet.rwa.endpoints;

import ba.fet.rwa.services.QuizService;

import javax.ws.rs.Path;

@Path("/api/quiz")
public class QuizApi {
    QuizService quizService = new QuizService();


}
