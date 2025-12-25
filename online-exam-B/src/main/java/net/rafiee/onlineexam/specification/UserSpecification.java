package net.rafiee.onlineexam.specification;

import jakarta.persistence.criteria.Predicate;
import net.rafiee.onlineexam.entity.User;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class UserSpecification {
    
    public static Specification<User> searchUsers(String keyword, UserRole role, UserStatus status) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            
            // جستجوی کلمه کلیدی در نام، نام کاربری و ایمیل
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchPattern = "%" + keyword.toLowerCase() + "%";
                Predicate namePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("fullName")), searchPattern);
                Predicate usernamePredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("username")), searchPattern);
                Predicate emailPredicate = criteriaBuilder.like(
                    criteriaBuilder.lower(root.get("email")), searchPattern);
                
                predicates.add(criteriaBuilder.or(namePredicate, usernamePredicate, emailPredicate));
            }
            
            // فیلتر بر اساس نقش
            if (role != null) {
                predicates.add(criteriaBuilder.equal(root.get("role"), role));
            }
            
            // فیلتر بر اساس وضعیت
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }
            
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}