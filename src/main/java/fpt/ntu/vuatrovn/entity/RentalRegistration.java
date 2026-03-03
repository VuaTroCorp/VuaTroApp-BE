package fpt.ntu.vuatrovn.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import fpt.ntu.vuatrovn.enums.RentalStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rental registration")
public class RentalRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long registration_id;

    private LocalDate registration_date;
    private LocalDateTime viewing_date;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    @ManyToOne
    @JoinColumn(name = "id", nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Getter & Setter
        public long getId() {
        return this.registration_id;
    }

    public void setId(long registration_id) {
        this.registration_id = registration_id;
    }

    public LocalDate getRegistration_date() {
        return this.registration_date;
    }

    public void setRegistration_date(LocalDate registration_date) {
        this.registration_date = registration_date;
    }

    public LocalDateTime getViewing_date() {
        return this.viewing_date;
    }

    public void setViewing_date(LocalDateTime viewing_date) {
        this.viewing_date = viewing_date;
    }

    public RentalStatus getStatus() {
        return this.status;
    }

    public void setStatus(RentalStatus status) {
        this.status = status;
    }

        public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
