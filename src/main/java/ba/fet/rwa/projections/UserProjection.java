package ba.fet.rwa.projections;

import ba.fet.rwa.models.User;
import ba.fet.rwa.models.User.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class UserProjection {
    private Long id;
    private String username;
    private List<Role> roles;

    public UserProjection() {}

    public UserProjection(User user) {
        id = user.getId();
        username = user.getUsername();
        roles = new ArrayList<>(user.getRoles());
    }

    public static UserProjection toProjection(User user) {
        return new UserProjection(user);
    }

    public static List<UserProjection> toProjection(List<User> users) {
        return users.stream().map(UserProjection::toProjection).collect(Collectors.toList());
    }

    public User toModel() {
        User user = new User();

        user.setUsername(this.getUsername());
        user.setRoles(new HashSet<>(this.getRoles()));

        return user;
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("roles")
    public List<Role> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
