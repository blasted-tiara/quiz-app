package ba.fet.rwa.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

@Path("/api/images")
public class ImageApi {
    private final String imageDirName = "images/";
    @GET
    @Path("/{filename}")
    @Produces({"image/png", "image/jpg"})
    public Response getImage(@PathParam("filename") String filename) {
        File file = new File(imageDirName + filename);
        if (!file.exists()) {
            return Response.serverError().entity("Error fetching image").build();
        }
        String contentType;
        try {
            contentType = Files.probeContentType(Paths.get(file.getPath()));
        } catch (Exception e) {
            return Response.serverError().entity("Error fetching image").build();
        }
        return Response.ok(file, contentType).build();
    }
}
