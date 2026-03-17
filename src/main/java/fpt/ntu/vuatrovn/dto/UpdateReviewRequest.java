package fpt.ntu.vuatrovn.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class UpdateReviewRequest {

    @Schema(description = "Điểm đánh giá (từ 1 đến 5)", example = "4")
    private Integer rating;

    @Schema(description = "Nội dung bình luận sửa lại", example = "Phòng tốt, nhưng hơi xa trung tâm một chút.")
    private String content;

    // TODO: Bạn tự generate Getter và Setter ở đây nhé (hoặc dùng @Data của Lombok)
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}