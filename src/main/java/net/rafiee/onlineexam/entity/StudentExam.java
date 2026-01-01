package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity برای ذخیره اطلاعات شرکت دانشجو در آزمون
 */
@Entity
@Table(name = "student_exams", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"student_id", "exam_id"})
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentExam {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    // زمان شروع آزمون توسط دانشجو
    @Column(nullable = false)
    private LocalDateTime startTime;

    // زمان پایان آزمون توسط دانشجو
    private LocalDateTime endTime;

    // آیا آزمون تمام شده است؟
    @Column(nullable = false)
    @Builder.Default
    private Boolean isCompleted = false;

    // نمره کل دریافتی (محاسبه می‌شود)
    @Column
    private Double totalScore;

    // نمره کل آزمون (سقف نمره)
    @Column
    private Double maxScore;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // پاسخ‌های دانشجو
    @OneToMany(mappedBy = "studentExam", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StudentAnswer> answers = new ArrayList<>();

    /**
     * آیا آزمون هنوز در حال برگزاری است؟
     */
    public boolean isInProgress() {
        if (isCompleted) {
            return false;
        }
        
        // محاسبه زمان پایان بر اساس مدت زمان آزمون
        LocalDateTime expectedEndTime = startTime.plusMinutes(exam.getDurationMinutes());
        return LocalDateTime.now().isBefore(expectedEndTime);
    }

    /**
     * آیا زمان آزمون تمام شده است؟
     */
    public boolean isTimeExpired() {
        if (startTime == null) {
            return false;
        }
        LocalDateTime expectedEndTime = startTime.plusMinutes(exam.getDurationMinutes());
        return LocalDateTime.now().isAfter(expectedEndTime);
    }

    /**
     * محاسبه زمان باقی‌مانده به ثانیه
     */
    public long getRemainingSeconds() {
        if (isCompleted || startTime == null) {
            return 0;
        }
        
        LocalDateTime expectedEndTime = startTime.plusMinutes(exam.getDurationMinutes());
        long secondsRemaining = java.time.Duration.between(LocalDateTime.now(), expectedEndTime).getSeconds();
        return Math.max(0, secondsRemaining);
    }

    /**
     * اضافه کردن پاسخ
     */
    public void addAnswer(StudentAnswer answer) {
        answers.add(answer);
        answer.setStudentExam(this);
    }

    /**
     * پایان دادن به آزمون
     */
    public void completeExam() {
        this.isCompleted = true;
        this.endTime = LocalDateTime.now();
    }
}