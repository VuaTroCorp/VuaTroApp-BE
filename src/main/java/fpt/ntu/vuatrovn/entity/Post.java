package fpt.ntu.vuatrovn.entity;

import java.util.List;

import fpt.ntu.vuatrovn.enums.PostStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name="post")

public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long post_id;

    private String title;
    private Float price;


    @Enumerated(EnumType.STRING)
    private PostStatus status;

    private Float area;
    private int room_quantity;
    private String province;
    private String commune;

    @Column(columnDefinition = "LONGTEXT")
    private String street;

    @Column(columnDefinition = "LONGTEXT")
    private String decription;

    private Double longitude;
    private Double latitude;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    public List<Image> getImages() {
        return this.images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    @ManyToOne
    @JoinColumn(name = "id",nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private RoomType type;

    // Getter & Setter
        public long getId() {
        return this.post_id;
    }

    public void setId(long post_id) {
        this.post_id = post_id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

        public PostStatus getStatus() {
        return this.status;
    }

    public void setStatus(PostStatus status) {
        this.status = status;
    }

    public Float getArea() {
        return this.area;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    public int getRoom_quantity() {
        return this.room_quantity;
    }

    public void setRoom_quantity(int room_quantity) {
        this.room_quantity = room_quantity;
    }

    public String getProvince() {
        return this.province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCommune() {
        return this.commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

        public String getStreet() {
        return this.street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

        public String getDecription() {
        return this.decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public Double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAtitude() {
        return this.latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

        public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

        public RoomType getType() {
        return this.type;
    }

    public void setType(RoomType type) {
        this.type = type;
    }
}
