package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddQuestionToExamDTO {
    
    @NotNull(message = "شناسه سوال الزامی است")
    private Long questionId;
    
    @NotNull(message = "امتیاز سوال الزامی است")
    @Positive(message = "امتیاز باید مثبت باشد")
    private Double score;
    
    @NotNull(message = "ترتیب سوال الزامی است")
    @Positive(message = "ترتیب باید مثبت باشد")
    private Integer orderNumber;
}