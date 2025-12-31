package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_questions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(nullable = false)
    private Double score;  // نمره این سوال در این آزمون
    
    @Column(nullable = false)
    private Integer orderIndex;  // ترتیب سوال در آزمون
    
    // اضافه کردن constraint برای جلوگیری از سوال تکراری در یک آزمون
    @PrePersist
    @PreUpdate
    private void validateUniqueQuestionInExam() {
        // در Service بررسی می‌شود
    }
}