package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceQuestionDTO {
    
    @NotNull(message = "شناسه دوره الزامی است")
    private Long courseId;
    
    @NotBlank(message = "عنوان سوال الزامی است")
    @Size(max = 200, message = "عنوان نباید بیشتر از 200 کاراکتر باشد")
    private String title;
    
    @NotBlank(message = "صورت سوال الزامی است")
    private String questionText;
    
    @NotEmpty(message = "حداقل یک گزینه الزامی است")
    @Size(min = 2, message = "حداقل دو گزینه برای سوال چندگزینه‌ای الزامی است")
    private List<QuestionOptionDTO> options;
}