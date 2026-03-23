package fpt.ntu.vuatrovn.specification;

import fpt.ntu.vuatrovn.dto.PostSearchRequest;
import fpt.ntu.vuatrovn.entity.Post;
import fpt.ntu.vuatrovn.enums.PostStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class PostSpecification {

    public static Specification<Post> filterPosts(PostSearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // 1. CHỈ hiển thị các bài đăng đã được DUYỆT (APPROVE)
            predicates.add(cb.equal(root.get("status"), PostStatus.APPROVED));

            if (request != null) {
                // 2. Tìm theo từ khóa (Tìm trong Title HOẶC Description)
                if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
                    String pattern = "%" + request.getKeyword().toLowerCase() + "%";
                    Predicate titleMatch = cb.like(cb.lower(root.get("title")), pattern);
                    Predicate descMatch = cb.like(cb.lower(root.get("description")), pattern); // Viết đúng theo tên biến của team
                    predicates.add(cb.or(titleMatch, descMatch));
                }

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