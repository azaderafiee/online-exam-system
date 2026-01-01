package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO برای پاسخ شروع آزمون
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartExamResponseDTO {
    
    private Long studentExamId;
    private Long examId;
    private String examTitle;
    private String examDescription;
    private Integer durationMinutes;
    private LocalDateTime startTime;
    private Long remainingSeconds;
    private Integer totalQuestions;
    private Double maxScore;
    
    // لیست سوالات (بدون نمایش پاسخ صحیح)
    private List<ExamQuestionForStudentDTO> questions;
}