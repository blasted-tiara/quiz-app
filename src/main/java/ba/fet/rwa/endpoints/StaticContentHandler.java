package ba.fet.rwa.endpoints;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;

@Path("/")
public class StaticContentHandler {

    @GET
    @Path("/{path: .*}")
    public Response getFile(@PathParam("path") String path) {
        File file = Paths.get("src/main/resources/static", path).toFile();
        if (!file.exists()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        String mediaType = getMediaType(path);

        StreamingOutput fileStream = output -> {
            try {
                java.nio.file.Files.copy(file.toPath(), output);
            } catch (Exception e) {
                throw new WebApplicationException("File Not Found !!");
            }
        };
        return Response.ok(fileStream, mediaType).build();
    }

    private String getMediaType(String path) {
        if (path.endsWith(".html")) {
            return MediaType.TEXT_HTML;
        } else if (path.endsWith(".css")) {
            return "text/css";
        } else if (path.endsWith(".js")) {
            return "application/javascript";
        } else if (path.endsWith(".ico")) {
            return "image/x-icon";
        } else {
            return MediaType.TEXT_PLAIN;
        }
    }
}