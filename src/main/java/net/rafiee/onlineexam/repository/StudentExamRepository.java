package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.Exam;
import net.rafiee.onlineexam.entity.StudentExam;
import net.rafiee.onlineexam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentExamRepository extends JpaRepository<StudentExam, Long> {

    /**
     * یافتن شرکت دانشجو در آزمون خاص
     */
    Optional<StudentExam> findByStudentAndExam(User student, Exam exam);

    /**
     * بررسی اینکه آیا دانشجو قبلاً در آزمون شرکت کرده یا نه
     */
    boolean existsByStudentAndExam(User student, Exam exam);

    /**
     * لیست تمام شرکت‌کنندگان در یک آزمون
     */
    List<StudentExam> findByExam(Exam exam);

    /**
     * لیست تمام آزمون‌هایی که دانشجو در آن‌ها شرکت کرده
     */
    List<StudentExam> findByStudent(User student);

    /**
     * لیست آزمون‌های تمام شده دانشجو
     */
    List<StudentExam> findByStudentAndIsCompletedTrue(User student);

    /**
     * لیست آزمون‌های در حال انجام دانشجو
     */
    List<StudentExam> findByStudentAndIsCompletedFalse(User student);

    /**
     * تعداد شرکت‌کنندگان در یک آزمون
     */
    long countByExam(Exam exam);

    /**
     * تعداد افرادی که آزمون را تمام کرده‌اند
     */
    long countByExamAndIsCompletedTrue(Exam exam);

    /**
     * یافتن آزمون‌های یک دانشجو در یک دوره خاص
     */
    @Query("SELECT se FROM StudentExam se WHERE se.student = :student AND se.exam.course.id = :courseId")
    List<StudentExam> findByStudentAndCourseId(@Param("student") User student, @Param("courseId") Long courseId);

    /**
     * حذف تمام شرکت‌های یک دانشجو در آزمون‌های یک دوره
     */
    @Query("DELETE FROM StudentExam se WHERE se.student.id = :studentId AND se.exam.course.id = :courseId")
    void deleteByStudentIdAndCourseId(@Param("studentId") Long studentId, @Param("courseId") Long courseId);
}