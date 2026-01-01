package net.rafiee.onlineexam.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.*;
import net.rafiee.onlineexam.enumuration.QuestionType;
import net.rafiee.onlineexam.service.ExamQuestionService;
import net.rafiee.onlineexam.service.QuestionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@Slf4j
public class QuestionController {

    private final QuestionService questionService;
    private final ExamQuestionService examQuestionService;

    // ==================== ایجاد سوالات ====================

    @PostMapping("/multiple-choice")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<QuestionResponseDTO> createMultipleChoiceQuestion(
            @Valid @RequestBody MultipleChoiceQuestionDTO dto,
            Authentication authentication) {

        log.info("REST: Creating multiple choice question by {}", authentication.getName());

        QuestionResponseDTO response = questionService.createMultipleChoiceQuestion(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/descriptive")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<QuestionResponseDTO> createDescriptiveQuestion(
            @Valid @RequestBody DescriptiveQuestionDTO dto,
            Authentication authentication) {

        log.info("REST: Creating descriptive question by {}", authentication.getName());
        QuestionResponseDTO response = questionService.createDescriptiveQuestion(dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== دریافت سوالات ====================

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<QuestionResponseDTO> getQuestionById(@PathVariable Long id) {
        log.info("REST: Fetching question {}", id);
        QuestionResponseDTO response = questionService.getQuestionById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByCourse(@PathVariable Long courseId) {
        log.info("REST: Fetching questions for course {}", courseId);
        List<QuestionResponseDTO> questions = questionService.getQuestionsByCourse(courseId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/course/{courseId}/type/{type}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<QuestionResponseDTO>> getQuestionsByCourseAndType(
            @PathVariable Long courseId,
            @PathVariable QuestionType type) {

        log.info("REST: Fetching {} questions for course {}", type, courseId);
        List<QuestionResponseDTO> questions = questionService.getQuestionsByCourseAndType(courseId, type);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/course/{courseId}/search")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<QuestionResponseDTO>> searchQuestionsInCourse(
            @PathVariable Long courseId,
            @RequestParam String keyword) {

        log.info("REST: Searching questions in course {} with keyword: {}", courseId, keyword);
        List<QuestionResponseDTO> questions = questionService.searchQuestionsInCourse(courseId, keyword);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/course/{courseId}/my-questions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<QuestionResponseDTO>> getMyQuestionsInCourse(
            @PathVariable Long courseId,
            Authentication authentication) {

        log.info("REST: Fetching questions for instructor {} in course {}", authentication.getName(), courseId);
        List<QuestionResponseDTO> questions = questionService.getQuestionsByInstructor(authentication.getName(), courseId);
        return ResponseEntity.ok(questions);
    }


    @GetMapping("/get-all/{courseCode}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<QuestionResponseDTO>> getMyQuestions(@PathVariable String courseCode, Authentication authentication) {
        String username = authentication.getName();
        log.info("REST: Fetching questions for instructor {}", username);
        List<QuestionResponseDTO> questions = questionService.getQuestionsByInstructorIdAndCourseCode(username, courseCode);
        return ResponseEntity.ok(questions);
    }

    // ==================== ویرایش سوالات ====================

    @PutMapping("/{id}/multiple-choice")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<QuestionResponseDTO> updateMultipleChoiceQuestion(
            @PathVariable Long id,
            @Valid @RequestBody MultipleChoiceQuestionDTO dto,
            Authentication authentication) {

        log.info("REST: Updating multiple choice question {} by {}", id, authentication.getName());

        QuestionResponseDTO response = questionService.updateMultipleChoiceQuestion(id, dto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/descriptive/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<QuestionResponseDTO> updateDescriptiveQuestion(
            @PathVariable Long id,
            @Valid @RequestBody DescriptiveQuestionDTO dto,
            Authentication authentication) {

        log.info("REST: Updating descriptive question {} by {}", id, authentication.getName());

        QuestionResponseDTO response = questionService.updateDescriptiveQuestion(id, dto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    // ==================== حذف سوال ====================

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteQuestion(
            @PathVariable Long id,
            Authentication authentication) {

        log.info("REST: Deleting question {} by {}", id, authentication.getName());

        questionService.deleteQuestion(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    // ==================== آمار ====================

    @GetMapping("/course/{courseId}/count")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Long> countQuestionsByCourse(@PathVariable Long courseId) {
        log.info("REST: Counting questions for course {}", courseId);
        Long count = questionService.countQuestionsByCourse(courseId);
        return ResponseEntity.ok(count);
    }

    @GetMapping("/course/{courseId}/count/type/{type}")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Long> countQuestionsByCourseAndType(
            @PathVariable Long courseId,
            @PathVariable QuestionType type) {

        log.info("REST: Counting {} questions for course {}", type, courseId);
        Long count = questionService.countQuestionsByCourseAndType(courseId, type);
        return ResponseEntity.ok(count);
    }

    // ==================== مدیریت سوالات در آزمون ====================

    @PostMapping("/exam/{examId}/add-question")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ExamQuestionResponseDTO> addQuestionToExam(
            @PathVariable Long examId,
            @Valid @RequestBody AddQuestionToExamDTO dto,
            Authentication authentication) {

        log.info("REST: Adding question {} to exam {} by {}", dto.getQuestionId(), examId, authentication.getName());

        ExamQuestionResponseDTO response = examQuestionService.addQuestionToExam(examId, dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/exam/{examId}/add-multiple-questions")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<ExamQuestionResponseDTO>> addMultipleQuestionsToExam(
            @PathVariable Long examId,
            @Valid @RequestBody AddMultipleQuestionsToExamDTO dto,
            Authentication authentication) {

        log.info("REST: Adding multiple questions to exam {} by {}", examId, authentication.getName());

        List<ExamQuestionResponseDTO> response = examQuestionService.addMultipleQuestionsToExam(examId, dto, authentication.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/exam/{examId}/questions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<List<ExamQuestionResponseDTO>> getExamQuestions(@PathVariable Long examId) {
        log.info("REST: Fetching questions for exam {}", examId);
        List<ExamQuestionResponseDTO> questions = examQuestionService.getExamQuestions(examId);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/exam/{examId}/with-questions")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<ExamWithQuestionsResponseDTO> getExamWithQuestions(@PathVariable Long examId) {
        log.info("REST: Fetching exam with questions for exam {}", examId);
        ExamWithQuestionsResponseDTO response = examQuestionService.getExamWithQuestions(examId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/exam/{examId}/questions/{questionId}/score")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ExamQuestionResponseDTO> updateQuestionScore(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            @Valid @RequestBody UpdateQuestionScoreDTO dto,
            Authentication authentication) {

        log.info("REST: Updating score for question {} in exam {} by {}", questionId, examId, authentication.getName());

        ExamQuestionResponseDTO response = examQuestionService.updateQuestionScore(examId, questionId, dto, authentication.getName());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/exam/{examId}/questions/{questionId}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> removeQuestionFromExam(
            @PathVariable Long examId,
            @PathVariable Long questionId,
            Authentication authentication) {

        log.info("REST: Removing question {} from exam {} by {}", questionId, examId, authentication.getName());

        examQuestionService.removeQuestionFromExam(examId, questionId, authentication.getName());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/exam/{examId}/questions/reorder")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> reorderQuestions(
            @PathVariable Long examId,
            @RequestBody List<Long> questionIds,
            Authentication authentication) {

        log.info("REST: Reordering questions in exam {} by {}", examId, authentication.getName());

        examQuestionService.reorderQuestions(examId, questionIds, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/exam/{examId}/total-score")
    @PreAuthorize("hasAnyRole('INSTRUCTOR', 'ADMIN')")
    public ResponseEntity<Double> calculateExamTotalScore(@PathVariable Long examId) {
        log.info("REST: Calculating total score for exam {}", examId);
        Double totalScore = examQuestionService.calculateExamTotalScore(examId);
        return ResponseEntity.ok(totalScore);
    }
}