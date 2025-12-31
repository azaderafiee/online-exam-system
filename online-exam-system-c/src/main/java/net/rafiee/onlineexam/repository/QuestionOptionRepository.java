package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    
    List<QuestionOption> findByQuestionIdOrderByOrderIndexAsc(Long questionId);
    
    @Query("SELECT o FROM QuestionOption o WHERE o.question.id = :questionId AND o.isCorrect = true")
    Optional<QuestionOption> findCorrectOptionByQuestionId(@Param("questionId") Long questionId);
    
    void deleteByQuestionId(Long questionId);
}