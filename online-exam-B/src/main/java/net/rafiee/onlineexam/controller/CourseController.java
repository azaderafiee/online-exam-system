package net.rafiee.onlineexam.controller;

import net.rafiee.onlineexam.dto.CourseDTO;
import net.rafiee.onlineexam.dto.CourseResponseDTO;
import net.rafiee.onlineexam.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class CourseController {
    
    private final CourseService courseService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> createCourse(@Valid @RequestBody CourseDTO courseDTO) {
        CourseResponseDTO course = courseService.createCourse(courseDTO);
        return new ResponseEntity<>(course, HttpStatus.CREATED);
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<CourseResponseDTO>> getAllCourses() {
        return ResponseEntity.ok(courseService.getAllCourses());
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<CourseResponseDTO> getCourseById(@PathVariable Long id) {
        return ResponseEntity.ok(courseService.getCourseById(id));
    }
    
    @GetMapping("/code/{courseCode}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<CourseResponseDTO> getCourseByCourseCode(@PathVariable String courseCode) {
        return ResponseEntity.ok(courseService.getCourseByCourseCode(courseCode));
    }
    
    @GetMapping("/instructor/{instructorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR')")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByInstructor(@PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.getCoursesByInstructor(instructorId));
    }
    
    @GetMapping("/student/{studentId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    public ResponseEntity<List<CourseResponseDTO>> getCoursesByStudent(@PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.getCoursesByStudent(studentId));
    }
    
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'INSTRUCTOR', 'STUDENT')")
    public ResponseEntity<List<CourseResponseDTO>> searchCourses(@RequestParam String keyword) {
        return ResponseEntity.ok(courseService.searchCourses(keyword));
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> updateCourse(
            @PathVariable Long id,
            @Valid @RequestBody CourseDTO courseDTO) {
        return ResponseEntity.ok(courseService.updateCourse(id, courseDTO));
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{courseId}/instructors/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> addInstructorToCourse(
            @PathVariable Long courseId,
            @PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.addInstructorToCourse(courseId, instructorId));
    }
    
    @DeleteMapping("/{courseId}/instructors/{instructorId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> removeInstructorFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long instructorId) {
        return ResponseEntity.ok(courseService.removeInstructorFromCourse(courseId, instructorId));
    }
    
    @PostMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> addStudentToCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.addStudentToCourse(courseId, studentId));
    }
    
    @DeleteMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> removeStudentFromCourse(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.removeStudentFromCourse(courseId, studentId));
    }
    
    @PostMapping("/{courseId}/students/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CourseResponseDTO> addMultipleStudentsToCourse(
            @PathVariable Long courseId,
            @RequestBody List<Long> studentIds) {
        return ResponseEntity.ok(courseService.addMultipleStudentsToCourse(courseId, studentIds));
    }
}