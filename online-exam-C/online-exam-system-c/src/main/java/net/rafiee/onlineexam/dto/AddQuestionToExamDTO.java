package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionToExamDTO {
    
    @NotNull(message = "شناسه سوال الزامی است")
    private Long questionId;
    
    @NotNull(message = "نمره سوال الزامی است")
    @Min(value = 0, message = "نمره نمی‌تواند منفی باشد")
    private Double score;
    
    private Integer orderIndex;
}