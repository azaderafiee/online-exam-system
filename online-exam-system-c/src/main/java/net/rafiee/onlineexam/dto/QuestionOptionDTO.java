package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionOptionDTO {
    
    @NotBlank(message = "متن گزینه الزامی است")
    private String optionText;
    
    @NotNull(message = "مشخص کردن گزینه صحیح الزامی است")
    private Boolean isCorrect;
    
    private Integer orderIndex;
}