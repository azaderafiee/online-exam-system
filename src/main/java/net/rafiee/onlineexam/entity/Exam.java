package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "exam")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Exam {

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
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(nullable = false)
    private LocalDateTime examDateTime;

    // این فیلدها برای cache کردن مقادیر هستند
    // به صورت خودکار با updateExamTotals() به‌روز می‌شوند
    @Column(name = "total_questions")
    private Integer totalQuestions;

    @Column(name = "total_score")
    private Double totalScore;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // رابطه با سوالات - استفاده از orderNumber به جای orderIndex
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderNumber ASC")
    @Builder.Default
    private List<ExamQuestion> examQuestions = new ArrayList<>();

    /**
     * متد کمکی برای اضافه کردن سوال
     */
    public void addQuestion(Question question, Double score, Integer orderNumber) {
        ExamQuestion examQuestion = ExamQuestion.builder()
                .exam(this)
                .question(question)
                .score(score)
                .orderNumber(orderNumber)
                .build();
        examQuestions.add(examQuestion);
    }

    /**
     * متد کمکی برای حذف سوال
     */
    public void removeQuestion(ExamQuestion examQuestion) {
        examQuestions.remove(examQuestion);
        examQuestion.setExam(null);
    }

    /**
     * محاسبه و به‌روزرسانی مجموع نمرات و تعداد سوالات
     * این متد باید بعد از هر تغییر در سوالات فراخوانی شود
     */
    public void updateTotals() {
        this.totalQuestions = examQuestions.size();
        this.totalScore = examQuestions.stream()
                .mapToDouble(ExamQuestion::getScore)
                .sum();
    }
}