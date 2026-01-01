package net.rafiee.onlineexam.repository;

import net.rafiee.onlineexam.entity.ExamQuestion;
import net.rafiee.onlineexam.entity.StudentAnswer;
import net.rafiee.onlineexam.entity.StudentExam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    /**
     * یافتن پاسخ دانشجو به یک سوال خاص
     */
    Optional<StudentAnswer> findByStudentExamAndExamQuestion(StudentExam studentExam, ExamQuestion examQuestion);

    /**
     * لیست تمام پاسخ‌های یک دانشجو در یک آزمون
     */
    List<StudentAnswer> findByStudentExam(StudentExam studentExam);

    /**
     * لیست پاسخ‌های نمره‌دهی نشده یک آزمون دانشجو
     */
    List<StudentAnswer> findByStudentExamAndIsGradedFalse(StudentExam studentExam);

    /**
     * تعداد سوالاتی که دانشجو پاسخ داده
     */
    long countByStudentExam(StudentExam studentExam);

    /**
     * تعداد پاسخ‌های صحیح
     */
    long countByStudentExamAndIsCorrectTrue(StudentExam studentExam);

    /**
     * محاسبه مجموع نمرات یک آزمون
     */
    @Query("SELECT SUM(sa.score) FROM StudentAnswer sa WHERE sa.studentExam = :studentExam AND sa.isGraded = true")
    Double calculateTotalScore(@Param("studentExam") StudentExam studentExam);

    /**
     * بررسی اینکه آیا همه سوالات نمره‌دهی شده‌اند یا نه
     */
    @Query("SELECT CASE WHEN COUNT(sa) = 0 THEN true ELSE false END FROM StudentAnswer sa WHERE sa.studentExam = :studentExam AND sa.isGraded = false")
    boolean areAllQuestionsGraded(@Param("studentExam") StudentExam studentExam);

    /**
     * حذف تمام پاسخ‌های یک آزمون دانشجو
     */
    void deleteByStudentExam(StudentExam studentExam);
}