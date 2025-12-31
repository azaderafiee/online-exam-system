package net.rafiee.onlineexam.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.ExamDTO;
import net.rafiee.onlineexam.dto.ExamResponseDTO;
import net.rafiee.onlineexam.dto.ExamUpdateDTO;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.ExamService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Slf4j
public class ExamController {
    
    private final ExamService examService;
    private final UserRepository userRepository;
    
    @PostMapping
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ExamResponseDTO> createExam(@Valid @RequestBody ExamDTO examDTO) {
        log.info("POST /api/exams - Creating new exam");
        Long instructorId = getCurrentUserId();
        ExamResponseDTO exam = examService.createExam(examDTO, instructorId);
        return new ResponseEntity<>(exam, HttpStatus.CREATED);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<ExamResponseDTO>> getAllExams() {
        log.info("GET /api/exams - Fetching all exams");
        return ResponseEntity.ok(examService.getAllExams());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<ExamResponseDTO> getExamById(@PathVariable Long id) {
        log.info("GET /api/exams/{} - Fetching exam by ID", id);
        return ResponseEntity.ok(examService.getExamById(id));
    }
    
    @GetMapping("/course/{courseId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByCourse(@PathVariable Long courseId) {
        log.info("GET /api/exams/course/{} - Fetching exams by course", courseId);
        return ResponseEntity.ok(examService.getExamsByCourse(courseId));
    }
    
    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<ExamResponseDTO>> getExamsByInstructor(@PathVariable Long instructorId) {
        log.info("GET /api/exams/instructor/{} - Fetching exams by instructor", instructorId);
        return ResponseEntity.ok(examService.getExamsByInstructor(instructorId));
    }
    
    @GetMapping("/my-exams")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<List<ExamResponseDTO>> getMyExams() {
        log.info("GET /api/exams/my-exams - Fetching current instructor's exams");
        Long instructorId = getCurrentUserId();
        return ResponseEntity.ok(examService.getExamsByInstructor(instructorId));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ExamResponseDTO> updateExam(
            @PathVariable Long id,
            @Valid @RequestBody ExamUpdateDTO updateDTO) {
        log.info("PUT /api/exams/{} - Updating exam", id);
        Long instructorId = getCurrentUserId();
        return ResponseEntity.ok(examService.updateExam(id, updateDTO, instructorId));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) {
        log.info("DELETE /api/exams/{} - Deleting exam", id);
        Long instructorId = getCurrentUserId();
        examService.deleteExam(id, instructorId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/course/{courseId}/count")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<Long> countExamsByCourse(@PathVariable Long courseId) {
        return ResponseEntity.ok(examService.countExamsByCourse(courseId));
    }
    
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"))
                .getId();
    }
}