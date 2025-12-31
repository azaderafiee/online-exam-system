package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DescriptiveQuestionDTO {
    
    @NotNull(message = "شناسه دوره الزامی است")
    private Long courseId;
    
    @NotBlank(message = "عنوان سوال الزامی است")
    @Size(max = 200, message = "عنوان نباید بیشتر از 200 کاراکتر باشد")
    private String title;
    
    @NotBlank(message = "صورت سوال الزامی است")
    private String questionText;
    
    private String sampleAnswer;
    
    private Integer maxWords;
}