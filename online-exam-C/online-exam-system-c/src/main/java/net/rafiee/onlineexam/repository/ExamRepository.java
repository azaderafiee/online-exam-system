package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {
    
    List<Exam> findByCourseId(Long courseId);
    
    List<Exam> findByCreatedById(Long instructorId);
    
    @Query("SELECT e FROM Exam e WHERE e.course.id = :courseId AND e.createdBy.id = :instructorId")
    List<Exam> findByCourseIdAndInstructorId(
            @Param("courseId") Long courseId, 
            @Param("instructorId") Long instructorId);
    
    @Query("SELECT e FROM Exam e JOIN e.course c JOIN c.instructors i WHERE i.id = :instructorId")
    List<Exam> findAllByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT COUNT(e) FROM Exam e WHERE e.course.id = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
}