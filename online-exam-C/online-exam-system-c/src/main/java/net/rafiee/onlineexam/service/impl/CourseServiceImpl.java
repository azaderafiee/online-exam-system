package net.rafiee.onlineexam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.CourseDTO;
import net.rafiee.onlineexam.dto.CourseResponseDTO;
import net.rafiee.onlineexam.dto.UserResponseDTO;
import net.rafiee.onlineexam.entity.Course;
import net.rafiee.onlineexam.entity.User;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;
import net.rafiee.onlineexam.exception.BadRequestException;
import net.rafiee.onlineexam.exception.DuplicateResourceException;
import net.rafiee.onlineexam.exception.ResourceNotFoundException;
import net.rafiee.onlineexam.repository.CourseRepository;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.CourseService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CourseServiceImpl implements CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public CourseResponseDTO createCourse(CourseDTO courseDTO) {
        log.info("Creating new course: {}", courseDTO.getCourseCode());

        if (courseRepository.existsByCourseCode(courseDTO.getCourseCode())) {
            throw new DuplicateResourceException("کد دوره قبلاً استفاده شده است");
        }

        if (courseDTO.getEndDate().isBefore(courseDTO.getStartDate())) {
            throw new BadRequestException("تاریخ پایان نمی‌تواند قبل از تاریخ شروع باشد");
        }

        Course course = Course.builder()
                .title(courseDTO.getTitle())
                .courseCode(courseDTO.getCourseCode())
                .description(courseDTO.getDescription())
                .startDate(courseDTO.getStartDate())
                .endDate(courseDTO.getEndDate())
                .instructors(new HashSet<>())
                .students(new HashSet<>())
                .build();

        Course savedCourse = courseRepository.save(course);
        log.info("Course created successfully with ID: {}", savedCourse.getId());

        return mapToCourseResponseDTO(savedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseById(Long id) {
        Course course = courseRepository.findById(id)
                .orElseGet(Course::new);
        return mapToCourseResponseDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public CourseResponseDTO getCourseByCourseCode(String courseCode) {
        Course course = courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new ResourceNotFoundException("دوره با کد " + courseCode + " یافت نشد"));
        return mapToCourseResponseDTO(course);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::mapToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByInstructor(Long instructorId) {
        if (!userRepository.existsById(instructorId)) {
            throw new ResourceNotFoundException("استاد یافت نشد");
        }
        return courseRepository.findByInstructorId(instructorId).stream()
                .map(this::mapToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getCoursesByStudent(Long studentId) {
        if (!userRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("دانشجو یافت نشد");
        }
        return courseRepository.findByStudentId(studentId).stream()
                .map(this::mapToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> searchCourses(String keyword) {
        return courseRepository.searchCourses(keyword).stream()
                .map(this::mapToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CourseResponseDTO updateCourse(Long id, CourseDTO courseDTO) {
        log.info("Updating course with ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        if (!course.getCourseCode().equals(courseDTO.getCourseCode()) &&
                courseRepository.existsByCourseCode(courseDTO.getCourseCode())) {
            throw new DuplicateResourceException("کد دوره قبلاً توسط دوره دیگری استفاده شده است");
        }

        if (courseDTO.getEndDate().isBefore(courseDTO.getStartDate())) {
            throw new BadRequestException("تاریخ پایان نمی‌تواند قبل از تاریخ شروع باشد");
        }

        course.setTitle(courseDTO.getTitle());
        course.setCourseCode(courseDTO.getCourseCode());
        course.setDescription(courseDTO.getDescription());
        course.setStartDate(courseDTO.getStartDate());
        course.setEndDate(courseDTO.getEndDate());

        Course updatedCourse = courseRepository.save(course);
        log.info("Course updated successfully");

        return mapToCourseResponseDTO(updatedCourse);
    }

    @Override
    public void deleteCourse(Long id) {
        log.info("Deleting course with ID: {}", id);

        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        courseRepository.delete(course);
        log.info("Course deleted successfully");
    }

    @Override
    public CourseResponseDTO addInstructorToCourse(Long courseId, Long instructorId) {
        log.info("Adding instructor {} to course {}", instructorId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new BadRequestException("کاربر انتخاب شده استاد نیست");
        }

        if (instructor.getStatus() != UserStatus.APPROVED) {
            throw new BadRequestException("استاد هنوز تأیید نشده است");
        }

        if (!course.getInstructors().isEmpty()) {
            throw new BadRequestException("این دوره قبلاً یک استاد دارد. ابتدا استاد فعلی را حذف کنید");
        }

        course.addInstructor(instructor);
        Course updatedCourse = courseRepository.save(course);

        log.info("Instructor added successfully");
        return mapToCourseResponseDTO(updatedCourse);
    }

    @Override
    public CourseResponseDTO removeInstructorFromCourse(Long courseId, Long instructorId) {
        log.info("Removing instructor {} from course {}", instructorId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        course.removeInstructor(instructor);
        Course updatedCourse = courseRepository.save(course);

        log.info("Instructor removed successfully");
        return mapToCourseResponseDTO(updatedCourse);
    }

    @Override
    public CourseResponseDTO addStudentToCourse(Long courseId, Long studentId) {
        log.info("Adding student {} to course {}", studentId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("دانشجو یافت نشد"));

        if (student.getRole() != UserRole.STUDENT) {
            throw new BadRequestException("کاربر انتخاب شده دانشجو نیست");
        }

        if (student.getStatus() != UserStatus.APPROVED) {
            throw new BadRequestException("دانشجو هنوز تأیید نشده است");
        }

        if (course.getStudents().contains(student)) {
            throw new BadRequestException("این دانشجو قبلاً به دوره اضافه شده است");
        }

        course.addStudent(student);
        Course updatedCourse = courseRepository.save(course);

        log.info("Student added successfully");
        return mapToCourseResponseDTO(updatedCourse);
    }

    @Override
    public CourseResponseDTO removeStudentFromCourse(Long courseId, Long studentId) {
        log.info("Removing student {} from course {}", studentId, courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("دانشجو یافت نشد"));

        course.removeStudent(student);
        Course updatedCourse = courseRepository.save(course);

        log.info("Student removed successfully");
        return mapToCourseResponseDTO(updatedCourse);
    }

    @Override
    public CourseResponseDTO addMultipleStudentsToCourse(Long courseId, List<Long> studentIds) {
        log.info("Adding {} students to course {}", studentIds.size(), courseId);

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        int addedCount = 0;
        for (Long studentId : studentIds) {
            User student = userRepository.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("دانشجو با شناسه " + studentId + " یافت نشد"));

            if (student.getRole() != UserRole.STUDENT) {
                log.warn("User {} is not a student, skipping", studentId);
                continue;
            }

            if (student.getStatus() != UserStatus.APPROVED) {
                log.warn("Student {} is not approved, skipping", studentId);
                continue;
            }

            if (course.getStudents().contains(student)) {
                log.warn("Student {} already in course, skipping", studentId);
                continue;
            }

            course.addStudent(student);
            addedCount++;
        }

        Course updatedCourse = courseRepository.save(course);
        log.info("{} students added successfully out of {}", addedCount, studentIds.size());

        return mapToCourseResponseDTO(updatedCourse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getCourseInstructors(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        return course.getInstructors().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> getCourseStudents(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        return course.getStudents().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toList());
    }

    private CourseResponseDTO mapToCourseResponseDTO(Course course) {
        Set<UserResponseDTO> instructorDTOs = course.getInstructors().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toSet());

        Set<UserResponseDTO> studentDTOs = course.getStudents().stream()
                .map(this::mapToUserResponseDTO)
                .collect(Collectors.toSet());

        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .courseCode(course.getCourseCode())
                .description(course.getDescription())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .createdAt(course.getCreatedAt())
                .instructors(instructorDTOs)
                .students(studentDTOs)
                .instructorCount(instructorDTOs.size())
                .studentCount(studentDTOs.size())
                .build();
    }

    private UserResponseDTO mapToUserResponseDTO(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .status(user.getStatus())
                .phoneNumber(user.getPhoneNumber())
                .createdAt(user.getCreatedAt())
                .build();
    }
}