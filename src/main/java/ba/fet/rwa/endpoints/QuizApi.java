package ba.fet.rwa.endpoints;

import ba.fet.rwa.models.Quiz;
import ba.fet.rwa.projections.QuizProjection;
import ba.fet.rwa.services.PageService;
import ba.fet.rwa.services.QuizService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;
import sun.jvm.hotspot.debugger.Page;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;

import static ba.fet.rwa.services.QuizService.PAGE_SIZE;

@Path("/api/quiz")
public class QuizApi {
    QuizService quizService = new QuizService();
    PageService pageService = new PageService();

    @POST
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createQuiz(
            @FormDataParam("thumbnailFile") InputStream thumbnailFile,
            @FormDataParam("thumbnailFile") FormDataContentDisposition thumbnailFileDetail,
            @FormDataParam("title") String title,
            @FormDataParam("description") String description
    ) {
        return quizService.createQuiz(thumbnailFile, thumbnailFileDetail, title, description);
    }

    @GET
    @Path("/{quizId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getQuizById(@PathParam("quizId") String quizId) {
        return quizService.getQuizById(quizId);
    }

    @PUT
    @Path("/{quizId}")
    @Consumes({MediaType.MULTIPART_FORM_DATA})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateQuiz(
            @PathParam("quizId") String id,
            @FormDataParam("thumbnailFile") InputStream thumbnailFile,
            @FormDataParam("thumbnailFile") FormDataContentDisposition thumbnailFileDetail,
            @FormDataParam("quiz") String quizString
    ) {
        ObjectMapper mapper = new ObjectMapper();
        QuizProjection quiz = null;
        try {
            quiz = mapper.readValue(quizString, QuizProjection.class);
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error while parsing quiz").build();
        }

        return quizService.updateQuiz(id, thumbnailFile, thumbnailFileDetail, quiz);
    }

    @DELETE
    @Path("/{quizId}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteQuiz(@PathParam("quizId") String id) {
        return quizService.deleteQuiz(id);
    }

    @GET
    @Path("/pagesCount")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getPagesCount() {
        return pageService.getPagesCountResponse(PAGE_SIZE, Quiz.class);
    }

    @GET
    @Path("/paginated")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response listPaginatedUsers(@NotNull @QueryParam("page") Integer page) {
        return quizService.getPaginatedQuizzes(page);
    }
}
