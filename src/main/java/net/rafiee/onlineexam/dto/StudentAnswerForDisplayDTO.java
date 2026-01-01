package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO برای نمایش پاسخ قبلی دانشجو
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerForDisplayDTO {
    
    private Long answerId;
    private Long examQuestionId;
    
    // برای سوال چندگزینه‌ای
    private Long selectedOptionId;
    
    // برای سوال تشریحی
    private String textAnswer;
}