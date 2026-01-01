package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponseDTO {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String courseCode;
    private String title;
    private String description;
    private Integer durationMinutes;
    private Integer totalQuestions;
    private Double totalScore;
    private LocalDateTime examDateTime;
    private String createdByName;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}