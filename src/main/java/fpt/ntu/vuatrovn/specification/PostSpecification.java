package fpt.ntu.vuatrovn.specification;

import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.enums.PostStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PostSpecification {

    public static Specification<Post> filterPosts(PostSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. CHỈ hiển thị các bài đăng đã được DUYỆT (APPROVE)
            predicates.add(cb.equal(root.get("status"), PostStatus.APPROVED));

            if (request != null) {
                // ==========================================
                // 🔥 2. TÌM THEO TỪ KHÓA (TỪ ĐIỂN MỞ RỘNG V2)
                // ==========================================
                if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                    String kw = request.getKeyword().toLowerCase().trim();
                    List<Predicate> keywordPredicates = new ArrayList<>();
                    List<String> searchTerms = new ArrayList<>();
                    
                    // Thêm từ khóa gốc do người dùng gõ
                    searchTerms.add(kw);

                    // Bộ từ điển "Bao trọn gói" các thói quen gõ phím của người dùng
                    if (kw.contains("nhà trọ") || kw.contains("nha tro")) {
                        searchTerms.add("phòng trọ");
                        searchTerms.add("phong tro");
                        searchTerms.add("nhà trọ");
                        searchTerms.add("nha tro");
                        searchTerms.add("phòng cho thuê");
                        searchTerms.add("nhà cho thuê");
                    } else if (kw.contains("phòng trọ") || kw.contains("phong tro") || kw.contains("phòng cho thuê")) {
                        searchTerms.add("nhà trọ");
                        searchTerms.add("nha tro");
                        searchTerms.add("phòng trọ");
                        searchTerms.add("phong tro");
                        searchTerms.add("nhà cho thuê");
                    }

                    // Loại bỏ các từ khóa bị trùng lặp để tối ưu tốc độ chạy SQL
                    List<String> uniqueTerms = searchTerms.stream().distinct().collect(Collectors.toList());

                    // Quét toàn bộ danh sách từ khóa trong cả Title và Decription
                    for (String term : uniqueTerms) {
                        String pattern = "%" + term + "%";
                        Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                        Predicate descMatch = cb.like(cb.lower(root.get("decription")), pattern); // Vẫn giữ 'decription' theo DB của team
                        
                        // Nghĩa là: Tiêu đề chứa từ khóa HOẶC Mô tả chứa từ khóa
                        keywordPredicates.add(cb.or(titleMatch, descMatch));
                    }
                    
                    // Gom tất cả các trường hợp lại: Chỉ cần khớp 1 trong các từ khóa là lấy bài đó
                    predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
                }

                // ==========================================
                // CÁC BỘ LỌC KHÁC (GIỮ NGUYÊN BẢN GỐC)
                // ==========================================
                
                // 3. Lọc theo khoảng Giá (Price)
                if (request.getMinPrice() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("price"), request.getMinPrice()));
                }
                if (request.getMaxPrice() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("price"), request.getMaxPrice()));
                }

                // 4. Lọc theo Diện tích (Area)
                if (request.getMinArea() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get("area"), request.getMinArea()));
                }
                if (request.getMaxArea() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get("area"), request.getMaxArea()));
                }

                // 5. Lọc theo Khu vực
                if (request.getProvince() != null && !request.getProvince().trim().isEmpty()) {
                    predicates.add(cb.equal(root.get("province"), request.getProvince()));
                }
                if (request.getCommune() != null && !request.getCommune().trim().isEmpty()) {
                    predicates.add(cb.equal(root.get("commune"), request.getCommune()));
                }

                // 6. Lọc theo Loại phòng (RoomType)
                if (request.getTypeId() != null) {
                    // Truy cập vào khóa ngoại type_id trong entity RoomType
                    predicates.add(cb.equal(root.get("type").get("type_id"), request.getTypeId()));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}