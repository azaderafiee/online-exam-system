package net.rafiee.onlineexam.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, length = 50)
    private String courseCode;
    
    @Column(length = 1000)
    private String description;
    
    @Column(nullable = false)
    private LocalDate startDate;
    
    @Column(nullable = false)
    private LocalDate endDate;
    
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @ManyToMany
    @JoinTable(
        name = "course_instructors",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "instructor_id")
    )
    @Builder.Default
    private Set<User> instructors = new HashSet<>();
    
    @ManyToMany
    @JoinTable(
        name = "course_students",
        joinColumns = @JoinColumn(name = "course_id"),
        inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @Builder.Default
    private Set<User> students = new HashSet<>();
    
    public void addInstructor(User instructor) {
        this.instructors.add(instructor);
        instructor.getInstructedCourses().add(this);
    }

    public void removeInstructor(User instructor) {
        this.instructors.remove(instructor);
        instructor.getInstructedCourses().remove(this);
    }

    public void addStudent(User student) {
        this.students.add(student);
        student.getEnrolledCourses().add(this);
    }
    
    public void removeStudent(User student) {
        this.students.remove(student);
        student.getEnrolledCourses().remove(this);
    }
}