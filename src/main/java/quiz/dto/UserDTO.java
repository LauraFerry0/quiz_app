package quiz.dto;

import java.util.Objects;

public class UserDTO {
    private String email;
    private String username;
    private String password;

    public UserDTO(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o != null && this.getClass() == o.getClass()) {
            UserDTO userDTO = (UserDTO)o;
            return Objects.equals(this.email, userDTO.email) && Objects.equals(this.username, userDTO.username);
        } else {
            return false;
        }
    }
}
