package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rafiee.onlineexam.enumuration.QuestionType;

import java.util.List;

/**
 * DTO برای نمایش سوال به دانشجو (بدون نمایش پاسخ صحیح)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionForStudentDTO {
    
    private Long examQuestionId;
    private Long questionId;
    private String title;
    private String questionText;
    private QuestionType questionType;
    private Double score;
    private Integer orderNumber;
    
    // برای سوال چندگزینه‌ای: لیست گزینه‌ها (بدون نمایش پاسخ صحیح)
    private List<QuestionOptionForStudentDTO> options;
    
    // برای سوال تشریحی: محدودیت تعداد کلمات (اختیاری)
    private Integer maxWords;
    
    // پاسخ قبلی دانشجو (اگر وجود داشته باشد)
    private StudentAnswerForDisplayDTO previousAnswer;
}