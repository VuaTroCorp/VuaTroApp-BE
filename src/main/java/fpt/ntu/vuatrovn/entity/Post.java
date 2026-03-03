package fpt.ntu.vuatrovn.entity;

import fpt.ntu.vuatrovn.enums.PostStatus;
import jakarta.persistence.Column;
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

    private Float longitude;
    private Float atitude;

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

    public Float getLongitude() {
        return this.longitude;
    }

    public void setLongitude(Float longitude) {
        this.longitude = longitude;
    }

    public Float getAtitude() {
        return this.atitude;
    }

    public void setAtitude(Float atitude) {
        this.atitude = atitude;
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
