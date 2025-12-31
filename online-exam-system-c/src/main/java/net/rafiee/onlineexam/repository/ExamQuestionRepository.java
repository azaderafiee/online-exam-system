package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.ExamQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamQuestionRepository extends JpaRepository<ExamQuestion, Long> {
    
    // یافتن سوالات یک آزمون (به ترتیب)
    List<ExamQuestion> findByExamIdOrderByOrderIndexAsc(Long examId);
    
    // یافتن یک سوال خاص در یک آزمون خاص
    Optional<ExamQuestion> findByExamIdAndQuestionId(Long examId, Long questionId);
    
    // بررسی وجود سوال در آزمون
    boolean existsByExamIdAndQuestionId(Long examId, Long questionId);
    
    // شمارش سوالات یک آزمون
    Long countByExamId(Long examId);
    
    // محاسبه مجموع نمرات یک آزمون
    @Query("SELECT COALESCE(SUM(eq.score), 0) FROM ExamQuestion eq WHERE eq.exam.id = :examId")
    Double calculateTotalScore(@Param("examId") Long examId);
    
    // حذف تمام سوالات یک آزمون
    void deleteByExamId(Long examId);
    
    // یافتن بیشترین orderIndex در یک آزمون
    @Query("SELECT COALESCE(MAX(eq.orderIndex), 0) FROM ExamQuestion eq WHERE eq.exam.id = :examId")
    Optional<Integer> findMaxOrderIndexByExamId(@Param("examId") Long examId);
}