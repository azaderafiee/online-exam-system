package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rafiee.onlineexam.enumuration.QuestionType;

/**
 * DTO برای نمایش جزئیات پاسخ دانشجو
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentAnswerDetailDTO {
    
    private Long answerId;
    private String questionTitle;
    private String questionText;
    private QuestionType questionType;
    private Double maxScore;
    private Double score;  // نمره دریافتی
    private Boolean isCorrect;
    private Boolean isGraded;
    
    // برای سوال چندگزینه‌ای
    private Long selectedOptionId;
    private String selectedOption;  // متن گزینه انتخاب شده
    
    // برای سوال تشریحی
    private String textAnswer;
    
    // نظر استاد (برای سوال تشریحی)
    private String instructorComment;
}