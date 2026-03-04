package fpt.ntu.vuatrovn.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "roomtype")

public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long type_id;
    private String type_name;

    public long getId() {
        return this.type_id;
    }

    public void setId(long type_id) {
        this.type_id = type_id;
    }

    public String getType_name() {
        return this.type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }
}
