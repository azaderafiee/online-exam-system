package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.rafiee.onlineexam.enumuration.QuestionType;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "question_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(nullable = false, length = 200)
    private String title;  // عنوان کوتاه (الزامی)

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;  // صورت سوال

    // Note: type is automatically managed by @DiscriminatorColumn
    // No need for explicit field when using Single Table Inheritance

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // رابطه با ExamQuestion (سوالات در آزمون‌ها)
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    /**
     * Returns the question type based on the discriminator value
     * This is automatically determined by Hibernate based on the actual class type
     */
    public QuestionType getType() {
        if (this instanceof MultipleChoiceQuestion) {
            return QuestionType.MULTIPLE_CHOICE;
        } else if (this instanceof DescriptiveQuestion) {
            return QuestionType.DESCRIPTIVE;
        }
        throw new IllegalStateException("Unknown question type");
    }
}