package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {

    @NotNull(message = "شناسه دوره الزامی است")
    private Long courseId;

    @NotBlank(message = "عنوان آزمون الزامی است")
    @Size(max = 200, message = "عنوان نباید بیشتر از 200 کاراکتر باشد")
    private String title;

    @Size(max = 1000, message = "توضیحات نباید بیشتر از 1000 کاراکتر باشد")
    private String description;

    @NotNull(message = "مدت زمان آزمون الزامی است")
    @Min(value = 1, message = "مدت زمان آزمون باید حداقل 1 دقیقه باشد")
    private Integer durationMinutes;

    @NotNull(message = "تاریخ و زمان برگزاری آزمون الزامی است")
    private LocalDateTime examDateTime;
}