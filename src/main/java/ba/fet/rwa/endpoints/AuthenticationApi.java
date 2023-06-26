package ba.fet.rwa.endpoints;

import ba.fet.rwa.models.LoginRequest;
import ba.fet.rwa.services.LoginService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/authentication")
public class AuthenticationApi {
    LoginService loginService = new LoginService();

    @POST
    @Path("/login")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response loginUser(@Context HttpServletRequest req, LoginRequest loginRequest) {
        if (loginService.authenticate(loginRequest)) {
            HttpSession session = req.getSession(true);
            session.setAttribute("currentUser", loginRequest.getUsername());
            return Response.status(Response.Status.OK).entity("{\"status\":\"User authenticated\"}").build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("{\"status\":\"User not authenticated\"}").build();
        }
    }

    @POST
    @Path("/logout")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public Response logout(@Context HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return Response.status(Response.Status.OK).build();
    }
}
