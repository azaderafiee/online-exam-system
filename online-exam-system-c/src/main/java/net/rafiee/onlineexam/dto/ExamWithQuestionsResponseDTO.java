package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamWithQuestionsResponseDTO {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseCode;
    private String title;
    private String description;
    private Integer durationMinutes;
    private LocalDateTime examDateTime;
    private String createdByName;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ✅ سوالات آزمون
    private List<ExamQuestionResponseDTO> questions;

    // ✅ آمار آزمون
    private Integer totalQuestions;
    private Double totalScore;
}