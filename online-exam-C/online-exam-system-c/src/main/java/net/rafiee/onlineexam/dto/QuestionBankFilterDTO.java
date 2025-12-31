package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rafiee.onlineexam.enumuration.QuestionType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionBankFilterDTO {
    private Long courseId;
    private QuestionType type;
    private String keyword;  // جستجو در عنوان و صورت سوال
}