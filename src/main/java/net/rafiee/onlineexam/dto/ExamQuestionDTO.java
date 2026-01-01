package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO برای نمایش سوالات یک آزمون (برای instructor)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionDTO {
    
    private Long id; // ExamQuestion ID
    private Long questionId;
    private String questionText;
    private String questionType; // MULTIPLE_CHOICE or DESCRIPTIVE
    private Double score;
    private Integer orderNumber;
    private Boolean hasOptions; // برای MULTIPLE_CHOICE
    private Integer optionsCount; // تعداد گزینه‌ها
}