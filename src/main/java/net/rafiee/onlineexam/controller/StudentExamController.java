package net.rafiee.onlineexam.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.rafiee.onlineexam.dto.*;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.StudentExamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API Controller برای مدیریت شرکت دانشجو در آزمون
 */
@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class StudentExamController {

    private final StudentExamService studentExamService;
    private final UserRepository userRepository;

    /**
     * دریافت لیست دوره‌های دانشجو
     * GET /api/student/courses
     */
    @GetMapping("/courses")
    public ResponseEntity<List<CourseResponseDTO>> getMyCourses() {
        Long studentId = getCurrentUserId();
        List<CourseResponseDTO> courses = studentExamService.getStudentCourses(studentId);
        return ResponseEntity.ok(courses);
    }

    /**
     * دریافت لیست آزمون‌های یک دوره
     * GET /api/student/courses/{courseId}/exams
     */
    @GetMapping("/courses/{courseId}/exams")
    public ResponseEntity<List<ExamForStudentDTO>> getCourseExams(@PathVariable Long courseId) {
        Long studentId = getCurrentUserId();
        List<ExamForStudentDTO> exams = studentExamService.getCourseExamsForStudent(studentId, courseId);
        return ResponseEntity.ok(exams);
    }

    /**
     * شروع آزمون
     * POST /api/student/exams/{examId}/start
     */
    @PostMapping("/exams/{examId}/start")
    public ResponseEntity<StartExamResponseDTO> startExam(@PathVariable Long examId) {
        Long studentId = getCurrentUserId();
        StartExamResponseDTO response = studentExamService.startExam(studentId, examId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * دریافت وضعیت آزمون در حال انجام
     * GET /api/student/student-exams/{studentExamId}/progress
     */
    @GetMapping("/student-exams/{studentExamId}/progress")
    public ResponseEntity<StartExamResponseDTO> getExamProgress(@PathVariable Long studentExamId) {
        Long studentId = getCurrentUserId();
        StartExamResponseDTO response = studentExamService.getExamProgress(studentId, studentExamId);
        return ResponseEntity.ok(response);
    }

    /**
     * ارسال پاسخ به یک سوال
     * POST /api/student/student-exams/{studentExamId}/answers
     */
    @PostMapping("/student-exams/{studentExamId}/answers")
    public ResponseEntity<Void> submitAnswer(
            @PathVariable Long studentExamId,
            @Valid @RequestBody SubmitAnswerDTO answerDTO) {
        Long studentId = getCurrentUserId();
        studentExamService.submitAnswer(studentId, studentExamId, answerDTO);
        return ResponseEntity.ok().build();
    }

    /**
     * ارسال چندین پاسخ یکجا (برای ذخیره موقت)
     * POST /api/student/student-exams/{studentExamId}/answers/batch
     */
    @PostMapping("/student-exams/{studentExamId}/answers/batch")
    public ResponseEntity<Void> submitMultipleAnswers(
            @PathVariable Long studentExamId,
            @Valid @RequestBody List<SubmitAnswerDTO> answers) {
        Long studentId = getCurrentUserId();
        studentExamService.submitMultipleAnswers(studentId, studentExamId, answers);
        return ResponseEntity.ok().build();
    }

    /**
     * پایان دادن به آزمون و دریافت نتیجه
     * POST /api/student/student-exams/{studentExamId}/complete
     */
    @PostMapping("/student-exams/{studentExamId}/complete")
    public ResponseEntity<ExamResultDTO> completeExam(@PathVariable Long studentExamId) {
        Long studentId = getCurrentUserId();
        ExamResultDTO result = studentExamService.completeExam(studentId, studentExamId);
        return ResponseEntity.ok(result);
    }

    /**
     * دریافت نتیجه یک آزمون تکمیل شده
     * GET /api/student/student-exams/{studentExamId}/result
     */
    @GetMapping("/student-exams/{studentExamId}/result")
    public ResponseEntity<ExamResultDTO> getExamResult(@PathVariable Long studentExamId) {
        Long studentId = getCurrentUserId();
        ExamResultDTO result = studentExamService.getExamResult(studentId, studentExamId);
        return ResponseEntity.ok(result);
    }

    /**
     * دریافت لیست آزمون‌های تکمیل شده
     * GET /api/student/completed-exams
     */
    @GetMapping("/completed-exams")
    public ResponseEntity<List<ExamResultDTO>> getCompletedExams() {
        Long studentId = getCurrentUserId();
        List<ExamResultDTO> results = studentExamService.getCompletedExams(studentId);
        return ResponseEntity.ok(results);
    }

    /**
     * بررسی امکان شرکت در آزمون
     * GET /api/student/exams/{examId}/can-participate
     */
    @GetMapping("/exams/{examId}/can-participate")
    public ResponseEntity<Boolean> canParticipate(@PathVariable Long examId) {
        Long studentId = getCurrentUserId();
        boolean canParticipate = studentExamService.canStudentParticipate(studentId, examId);
        return ResponseEntity.ok(canParticipate);
    }

    // Helper method to get current user ID from Security Context
    private Long getCurrentUserId() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getId();
    }
}