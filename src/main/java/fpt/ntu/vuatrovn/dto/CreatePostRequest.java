package fpt.ntu.vuatrovn.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private Float price;

    @NotNull(message = "Area is required")
    @Positive(message = "Area must be greater than 0")
    private Float area;

    @Positive(message = "Room quantity must be greater than 0")
    private int roomQuantity;

    @NotNull(message = "Type ID is required")
    private Long typeId;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "Description is required")
    private String description;

    private List<MultipartFile> images;

    // ================= GETTER SETTER =================
    public Long getTypeId() {
        return this.typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getAddress() {
        return this.address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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