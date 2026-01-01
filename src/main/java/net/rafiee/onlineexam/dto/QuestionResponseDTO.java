package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rafiee.onlineexam.enumuration.QuestionType;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionResponseDTO {
    private Long id;
    private Long courseId;
    private String courseTitle;
    private String title;
    private String questionText;
    private QuestionType type;
    private String createdByName;
    private Long createdById;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // برای سوال چندگزینه‌ای
    private List<QuestionOptionResponseDTO> options;
    
    // برای سوال تشریحی
    private String sampleAnswer;
    private Integer maxWords;
}