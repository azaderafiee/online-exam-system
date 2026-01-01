package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.DescriptiveQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DescriptiveQuestionRepository extends JpaRepository<DescriptiveQuestion, Long> {
    
    List<DescriptiveQuestion> findByCourseId(Long courseId);
    
    List<DescriptiveQuestion> findByCreatedById(Long instructorId);
}