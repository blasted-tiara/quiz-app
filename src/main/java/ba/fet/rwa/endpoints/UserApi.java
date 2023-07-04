package ba.fet.rwa.endpoints;

import ba.fet.rwa.models.PagesInfo;
import ba.fet.rwa.projections.UserCredentialsProjection;
import ba.fet.rwa.services.UserService;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/api/user")
public class UserApi {
    private final UserService userService = new UserService();

    @GET
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response getUserById(@PathParam("id") String id) {
        return userService.getUserById(id);
    }

    @GET
    @Path("/pagesCount")
    @Produces({ MediaType.APPLICATION_JSON })
    public PagesInfo getPagesCount() {
        return userService.getPagesCount();
    }

    @GET
    @Path("/paginated")
    @Produces({ MediaType.APPLICATION_JSON })
    public Response listPaginatedUsers(@NotNull @QueryParam("page") Integer page) {
        return userService.getPaginatedUsers(page);
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response createUser(UserCredentialsProjection user) {
        return userService.createUser(user);
    }

    @PUT
    @Path("/{id}")
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public Response updateUserById(
            @PathParam("id") String id,
            UserCredentialsProjection user) {
       return userService.updateUser(id, user);
    }

    @DELETE
    @Path("/{id}")
    @Produces({MediaType.APPLICATION_JSON})
    public Response deleteUserById(@PathParam("id") String id) {
        return userService.removeUserById(id);
    }
}
