package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.rafiee.onlineexam.enumuration.QuestionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@DiscriminatorValue("MULTIPLE_CHOICE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MultipleChoiceQuestion extends Question {

    @Builder
    public MultipleChoiceQuestion(Long id, Course course, User createdBy, String title, String questionText, LocalDateTime createdAt, LocalDateTime updatedAt, List<ExamQuestion> examQuestions, List<QuestionOption> options) {
        super(id, course, createdBy, title, questionText, createdAt, updatedAt, examQuestions);
        this.options = options;
    }

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuestionOption> options = new ArrayList<>();
    
    public void addOption(QuestionOption option) {
        options.add(option);
        option.setQuestion(this);
    }
    
    public void removeOption(QuestionOption option) {
        options.remove(option);
        option.setQuestion(null);
    }
}