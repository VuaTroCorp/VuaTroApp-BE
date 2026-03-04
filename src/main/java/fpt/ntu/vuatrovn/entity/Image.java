package fpt.ntu.vuatrovn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long image_id;

    private String url;
    private int order_index;

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    // Getter & Setter
        public long getId() {
        return this.image_id;
    }

    public void setId(long image_id) {
        this.image_id = image_id;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder_index() {
        return this.order_index;
    }

    public void setOrder_index(int order_index) {
        this.order_index = order_index;
    }

    public Post getPost() {
        return this.post;
    }

    public void setPost(Post post) {
        this.post = post;
    }
}
