package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO برای نمایش گزینه سوال به دانشجو (بدون نمایش پاسخ صحیح)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionForStudentDTO {
    
    private Long id;
    private String optionText;
    private Integer orderIndex;
    // isCorrect را نمایش نمی‌دهیم
}