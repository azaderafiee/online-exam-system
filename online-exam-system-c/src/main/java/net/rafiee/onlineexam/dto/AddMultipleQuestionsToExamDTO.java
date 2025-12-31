package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddMultipleQuestionsToExamDTO {
    
    @NotEmpty(message = "حداقل یک سوال باید انتخاب شود")
    private List<AddQuestionToExamDTO> questions;
}