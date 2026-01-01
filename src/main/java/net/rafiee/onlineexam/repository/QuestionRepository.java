package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.Question;
import net.rafiee.onlineexam.enumuration.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // یافتن سوالات یک دوره (بانک سوالات)
    List<Question> findByCourseId(Long courseId);
    
    // یافتن سوالات یک استاد
    List<Question> findByCreatedByUsernameAndCourse_CourseCode(String username, String course_courseCode);
    
    // یافتن سوالات یک استاد در یک دوره
    @Query("SELECT q FROM Question q WHERE q.course.id = :courseId AND q.createdBy.username = :username")
    List<Question> findByCourseIdAndInstructorId(
            @Param("courseId") Long courseId,
            @Param("username") String username);
    
    // جستجو در بانک سوالات
    @Query("SELECT q FROM Question q WHERE q.course.id = :courseId " +
           "AND (LOWER(q.title) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(q.questionText) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Question> searchInCourse(
            @Param("courseId") Long courseId,
            @Param("keyword") String keyword);
    
    // شمارش سوالات یک دوره
    Long countByCourseId(Long courseId);

}