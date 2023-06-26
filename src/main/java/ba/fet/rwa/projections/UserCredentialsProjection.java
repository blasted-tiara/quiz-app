package ba.fet.rwa.projections;

import ba.fet.rwa.models.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class UserCredentialsProjection extends UserProjection {
    private String password;

    @JsonProperty("password")
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public User toModel() {
        User user = super.toModel();

        user.setPasswordHashFromPlaintext(this.getPassword());

        return user;
    }
}
