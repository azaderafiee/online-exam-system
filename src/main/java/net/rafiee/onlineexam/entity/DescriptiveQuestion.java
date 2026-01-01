package net.rafiee.onlineexam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@DiscriminatorValue("DESCRIPTIVE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DescriptiveQuestion extends Question {

    @Column(columnDefinition = "TEXT")
    private String sampleAnswer;  // پاسخ نمونه (اختیاری)
    @Column
    private Integer maxWords;  // حداکثر تعداد کلمات (اختیاری)

    @Builder
    public DescriptiveQuestion(Long id, Course course, User createdBy, String title, String questionText, LocalDateTime createdAt, LocalDateTime updatedAt, List<ExamQuestion> examQuestions, String sampleAnswer, Integer maxWords) {
        super(id, course, createdBy, title, questionText, createdAt, updatedAt, examQuestions);
        this.sampleAnswer = sampleAnswer;
        this.maxWords = maxWords;
    }
}