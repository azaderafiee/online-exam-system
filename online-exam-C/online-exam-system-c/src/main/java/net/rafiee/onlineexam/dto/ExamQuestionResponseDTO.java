package net.rafiee.onlineexam.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.rafiee.onlineexam.enumuration.QuestionType;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamQuestionResponseDTO {
    private Long examQuestionId;
    private Long questionId;
    private String title;
    private String questionText;
    private QuestionType type;
    private Double score;
    private Integer orderIndex;

    // برای سوال چندگزینه‌ای
    private List<QuestionOptionResponseDTO> options;

    // برای سوال تشریحی
    private String sampleAnswer;
    private Integer maxWords;
}