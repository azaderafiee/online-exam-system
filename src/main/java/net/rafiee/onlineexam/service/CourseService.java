package net.rafiee.onlineexam.service;


import net.rafiee.onlineexam.dto.CourseDTO;
import net.rafiee.onlineexam.dto.CourseResponseDTO;
import net.rafiee.onlineexam.dto.UserResponseDTO;

import java.util.List;

public interface CourseService {

    CourseResponseDTO createCourse(CourseDTO courseDTO);

    CourseResponseDTO getCourseById(Long id);

    CourseResponseDTO getCourseByCourseCode(String courseCode);

    List<CourseResponseDTO> getAllCourses();

    List<CourseResponseDTO> getCoursesByInstructor(Long instructorId);

    List<CourseResponseDTO> getCoursesByStudent(Long studentId);

    List<CourseResponseDTO> searchCourses(String keyword);

    CourseResponseDTO updateCourse(Long id, CourseDTO courseDTO);

    void deleteCourse(Long id);

    // مدیریت استاد
    CourseResponseDTO addInstructorToCourse(Long courseId, Long instructorId);

    CourseResponseDTO removeInstructorFromCourse(Long courseId, Long instructorId);

    // مدیریت دانشجو
    CourseResponseDTO addStudentToCourse(Long courseId, Long studentId);

    CourseResponseDTO removeStudentFromCourse(Long courseId, Long studentId);

    CourseResponseDTO addMultipleStudentsToCourse(Long courseId, List<Long> studentIds);

    // مشاهده اعضای دوره
    List<UserResponseDTO> getCourseInstructors(Long courseId);

    List<UserResponseDTO> getCourseStudents(Long courseId);
}