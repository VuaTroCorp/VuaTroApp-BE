package fpt.ntu.vuatrovn.dto;

public class SignupRequest {

    private String username;
    private String password;
    private String confirmPassword;

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    private String email;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

   
}
