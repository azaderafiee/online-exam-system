package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO برای نمایش اطلاعات آزمون به دانشجو
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamForStudentDTO {
    
    private Long examId;
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDateTime examDateTime;
    private Integer totalQuestions;
    private Double totalScore;
    
    // وضعیت شرکت دانشجو
    private Boolean hasParticipated;  // آیا قبلاً شرکت کرده؟
    private Boolean isCompleted;      // آیا آزمون را تمام کرده؟
    private Boolean isInProgress;     // آیا الان در حال شرکت است؟
    private Boolean canParticipate;   // آیا می‌تواند شرکت کند؟
    private Long remainingSeconds;    // زمان باقی‌مانده (اگر در حال شرکت باشد)
    private Long studentExamId;       // شناسه StudentExam (اگر شرکت کرده باشد)
    
    // نتیجه (اگر آزمون تمام شده باشد)
    private Double studentScore;
    private Boolean isFullyGraded;    // آیا همه سوالات نمره‌دهی شده؟
    
    // اطلاعات استاد
    private String instructorName;
    
    // اطلاعات دوره
    private Long courseId;
    private String courseTitle;
}