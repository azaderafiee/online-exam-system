package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * Entity برای ذخیره پاسخ‌های دانشجو به سوالات
 */
@Entity
@Table(name = "student_answers", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_exam_id", "exam_question_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_exam_id", nullable = false)
    private StudentExam studentExam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_question_id", nullable = false)
    private ExamQuestion examQuestion;

    // برای سوال چندگزینه‌ای: ID گزینه انتخاب شده
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_option_id")
    private QuestionOption selectedOption;

    // برای سوال تشریحی: متن پاسخ
    @Column(columnDefinition = "TEXT")
    private String textAnswer;

    // آیا پاسخ صحیح است؟ (برای سوال چندگزینه‌ای محاسبه می‌شود، برای تشریحی بعداً توسط استاد تعیین می‌شود)
    @Column
    private Boolean isCorrect;

    // نمره دریافتی برای این سوال
    @Column
    private Double score;

    // آیا نمره‌دهی شده است؟
    @Column(nullable = false)
    @Builder.Default
    private Boolean isGraded = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * محاسبه خودکار نمره برای سوال چندگزینه‌ای
     */
    public void calculateScoreForMultipleChoice() {
        if (selectedOption != null && examQuestion != null) {
            this.isCorrect = selectedOption.getIsCorrect();
            this.score = isCorrect ? examQuestion.getScore() : 0.0;
            this.isGraded = true;
        }
    }

    /**
     * تعیین نمره دستی برای سوال تشریحی
     */
    public void setManualScore(Double score) {
        if (score < 0 || score > examQuestion.getScore()) {
            throw new IllegalArgumentException("نمره باید بین 0 تا " + examQuestion.getScore() + " باشد");
        }
        this.score = score;
        this.isGraded = true;
    }
}