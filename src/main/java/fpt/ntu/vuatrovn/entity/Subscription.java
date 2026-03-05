package fpt.ntu.vuatrovn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "subscription")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long subscription_id;

    private String subscription_name;
    private String decription;
    private Float price;
    private int duration;
    private int max_posts;

    // Getter & Setter

    public long getId() {
        return this.subscription_id;
    }

    public void setId(long subscription_id) {
        this.subscription_id = subscription_id;
    }

    public String getSubscription_name() {
        return this.subscription_name;
    }

    public void setSubscription_name(String subscription_name) {
        this.subscription_name = subscription_name;
    }

    public String getDecription() {
        return this.decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public Float getPrice() {
        return this.price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }

    public int getDuration() {
        return this.duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getMax_posts() {
        return this.max_posts;
    }

    public void setMax_posts(int max_posts) {
        this.max_posts = max_posts;
    }
}
