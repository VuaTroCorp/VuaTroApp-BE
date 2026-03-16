package fpt.ntu.vuatrovn.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "reviews")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer rating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    // Mối quan hệ: Nhiều đánh giá thuộc về 1 Người dùng
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Mối quan hệ: Nhiều đánh giá thuộc về 1 Bài đăng
    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // ==========================================
    // GETTER & SETTER
    // ==========================================
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
}