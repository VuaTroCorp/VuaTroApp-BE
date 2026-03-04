package fpt.ntu.vuatrovn.dto;

import java.util.List;

public class CreatePostRequest {
    private String title;
    private Float price;
    private Float area;
    private int roomQuantity;

     private Long typeId;

    public Long getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    private String province;
    private String commune;
    private String street;
    private String description;

    private List<String> imageUrls;

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

    public Float getArea() {
        return this.area;
    }

    public void setArea(Float area) {
        this.area = area;
    }

    public int getRoomQuantity() {
        return this.roomQuantity;
    }

    public void setRoomQuantity(int roomQuantity) {
        this.roomQuantity = roomQuantity;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getImageUrls() {
        return this.imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
    

}
