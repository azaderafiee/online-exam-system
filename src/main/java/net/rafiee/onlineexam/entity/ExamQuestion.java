package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "exam_question")
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
    
    @Column(name = "order_number", nullable = false)
    private Integer orderNumber;  // ترتیب سوال در آزمون


}