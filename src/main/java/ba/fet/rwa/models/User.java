package ba.fet.rwa.models;

import ba.fet.rwa.projections.UserProjection;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.ArrayList;
import java.util.Set;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String username;
    private String passwordHash;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "id"))
    @Column(name = "role", nullable = false)
    private Set<Role> roles;

    public void setPasswordHashFromPlaintext(String password) {
        this.passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
    }

    @JsonProperty("id")
    public Long getId() {
        return id;
    }

    @JsonProperty("username")
    public String getUsername() {
        return username;
    }

    @JsonProperty("passwordHash")
    public String getPasswordHash() {
        return passwordHash;
    }

    @JsonProperty("role")
    public Set<Role> getRoles() {
        return roles;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public UserProjection toProjection() {
        UserProjection projection = new UserProjection();

        projection.setUsername(this.getUsername());
        projection.setId(this.getId());
        projection.setRoles(new ArrayList<>(this.getRoles()));

        return projection;
    }

    public enum Role {
        ADMIN,
        MODERATOR
    }
}
