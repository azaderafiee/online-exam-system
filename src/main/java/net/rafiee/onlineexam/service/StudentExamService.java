package net.rafiee.onlineexam.service;

import net.rafiee.onlineexam.dto.*;

import java.util.List;

/**
 * سرویس مدیریت شرکت دانشجو در آزمون
 */
public interface StudentExamService {

    /**
     * دریافت لیست دوره‌های دانشجو
     */
    List<CourseResponseDTO> getStudentCourses(Long studentId);

    /**
     * دریافت لیست آزمون‌های یک دوره برای دانشجو
     */
    List<ExamForStudentDTO> getCourseExamsForStudent(Long studentId, Long courseId);

    /**
     * شروع آزمون توسط دانشجو
     */
    StartExamResponseDTO startExam(Long studentId, Long examId);

    /**
     * دریافت وضعیت آزمون در حال انجام
     */
    StartExamResponseDTO getExamProgress(Long studentId, Long studentExamId);

    /**
     * ارسال/به‌روزرسانی پاسخ به یک سوال
     */
    void submitAnswer(Long studentId, Long studentExamId, SubmitAnswerDTO answerDTO);

    /**
     * ارسال/به‌روزرسانی چندین پاسخ یکجا (برای ذخیره موقت)
     */
    void submitMultipleAnswers(Long studentId, Long studentExamId, List<SubmitAnswerDTO> answers);

    /**
     * پایان دادن به آزمون
     */
    ExamResultDTO completeExam(Long studentId, Long studentExamId);

    /**
     * دریافت نتیجه آزمون
     */
    ExamResultDTO getExamResult(Long studentId, Long studentExamId);

    /**
     * بررسی اینکه آیا دانشجو می‌تواند در آزمون شرکت کند
     */
    boolean canStudentParticipate(Long studentId, Long examId);

    /**
     * دریافت لیست آزمون‌های تکمیل شده دانشجو
     */
    List<ExamResultDTO> getCompletedExams(Long studentId);
}