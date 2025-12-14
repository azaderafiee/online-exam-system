package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {
    
    Optional<Course> findByCourseCode(String courseCode);
    
    boolean existsByCourseCode(String courseCode);
    
    @Query("SELECT c FROM Course c JOIN c.instructors i WHERE i.id = :instructorId")
    List<Course> findByInstructorId(@Param("instructorId") Long instructorId);
    
    @Query("SELECT c FROM Course c JOIN c.students s WHERE s.id = :studentId")
    List<Course> findByStudentId(@Param("studentId") Long studentId);
    
    @Query("SELECT c FROM Course c WHERE " +
           "LOWER(c.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.courseCode) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Course> searchCourses(@Param("keyword") String keyword);
}