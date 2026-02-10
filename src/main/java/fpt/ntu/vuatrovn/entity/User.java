package fpt.ntu.vuatrovn.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;
    private String password;
    private String email;
    // OAuth2
    @Column(nullable = false)
    private String provider;     // local | google | facebook

    @Column(nullable = false)
    private String providerId;   // sub từ Google


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
     private UserStatus status;


    // ===== SETTERS =====
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public UserStatus getStatus() {
        return status;
    }
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getProvider() {
        return provider;
    }
    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getProviderId() {
        return providerId;
    }
    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
