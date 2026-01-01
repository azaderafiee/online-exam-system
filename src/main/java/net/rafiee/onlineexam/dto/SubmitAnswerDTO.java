package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO برای ارسال/به‌روزرسانی پاسخ دانشجو
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubmitAnswerDTO {
    
    @NotNull(message = "شناسه سوال الزامی است")
    private Long examQuestionId;
    
    // برای سوال چندگزینه‌ای
    private Long selectedOptionId;
    
    // برای سوال تشریحی
    private String textAnswer;
}