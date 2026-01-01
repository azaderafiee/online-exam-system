package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO برای نمایش نتیجه آزمون
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResultDTO {
    
    private Long studentExamId;
    private String examTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer durationMinutes;
    
    // نتایج
    private Double totalScore;
    private Double maxScore;
    private Double percentage;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    
    // وضعیت نمره‌دهی
    private Boolean isFullyGraded;
    private Integer gradedQuestions;
    private Integer ungradedQuestions;
    
    // جزئیات پاسخ‌ها (اختیاری)
    private List<StudentAnswerDetailDTO> answerDetails;
}