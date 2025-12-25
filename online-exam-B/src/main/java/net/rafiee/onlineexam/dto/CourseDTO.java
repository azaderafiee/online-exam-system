package net.rafiee.onlineexam.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    
    @NotBlank(message = "عنوان دوره الزامی است")
    @Size(max = 200, message = "عنوان دوره نباید بیشتر از 200 کاراکتر باشد")
    private String title;
    
    @NotBlank(message = "کد دوره الزامی است")
    @Size(max = 50, message = "کد دوره نباید بیشتر از 50 کاراکتر باشد")
    private String courseCode;
    
    @Size(max = 1000, message = "توضیحات نباید بیشتر از 1000 کاراکتر باشد")
    private String description;
    
    @NotNull(message = "تاریخ شروع الزامی است")
    @FutureOrPresent(message = "تاریخ شروع نمی‌تواند در گذشته باشد")
    private LocalDate startDate;
    
    @NotNull(message = "تاریخ پایان الزامی است")
    private LocalDate endDate;
}