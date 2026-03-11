package fpt.ntu.vuatrovn.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class CreatePostRequest {
    private String title;
    private Float price;
    private Float area;
    private int roomQuantity;

     private Long typeId;

     private Double latitude;
     private Double longitude;

     public Double getLatitude() {
         return this.latitude;
     }

     public void setLatitude(Double latitude) {
         this.latitude = latitude;
     }

     public Double getLongitude() {
         return this.longitude;
     }

     public void setLongitude(Double longitude) {
         this.longitude = longitude;
     }

    public Long getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    private String address;

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    private String description;

    private List<MultipartFile> images;

    public List<MultipartFile> getImages() {
        return this.images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
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

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    

}
