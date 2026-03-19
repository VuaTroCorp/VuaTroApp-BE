package fpt.ntu.vuatrovn.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class CreateReviewRequest {
    
    @Schema(description = "ID của bài đăng phòng trọ", example = "1")
    private Long postId;

    @Schema(description = "Điểm đánh giá (từ 1 đến 5)", example = "5")
    private Integer rating;

    @Schema(description = "Nội dung bình luận", example = "Phòng rất sạch sẽ, chủ nhà thân thiện!")
    private String content;

    // TODO: Bạn tự generate Getter và Setter ở đây nhé (hoặc dùng @Data của Lombok)
    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}