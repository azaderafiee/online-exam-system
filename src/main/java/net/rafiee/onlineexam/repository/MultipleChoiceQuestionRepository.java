package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.MultipleChoiceQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MultipleChoiceQuestionRepository extends JpaRepository<MultipleChoiceQuestion, Long> {
    
    List<MultipleChoiceQuestion> findByCourseId(Long courseId);
    
    List<MultipleChoiceQuestion> findByCreatedById(Long instructorId);
}