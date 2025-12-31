package net.rafiee.onlineexam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.ExamDTO;
import net.rafiee.onlineexam.dto.ExamResponseDTO;
import net.rafiee.onlineexam.dto.ExamUpdateDTO;
import net.rafiee.onlineexam.entity.Course;
import net.rafiee.onlineexam.entity.Exam;
import net.rafiee.onlineexam.entity.User;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.exception.BadRequestException;
import net.rafiee.onlineexam.exception.ResourceNotFoundException;
import net.rafiee.onlineexam.exception.UnauthorizedException;
import net.rafiee.onlineexam.repository.CourseRepository;
import net.rafiee.onlineexam.repository.ExamRepository;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.ExamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public ExamResponseDTO createExam(ExamDTO examDTO, Long instructorId) {
        log.info("Creating exam for course {} by instructor {}", examDTO.getCourseId(), instructorId);

        Course course = courseRepository.findById(examDTO.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new UnauthorizedException("فقط استادان مجاز به ایجاد آزمون هستند");
        }

        if (!course.getInstructors().contains(instructor)) {
            throw new UnauthorizedException("شما استاد این دوره نیستید");
        }

        validateExamDateTime(examDTO.getExamDateTime(), course);

        Exam exam = Exam.builder()
                .course(course)
                .createdBy(instructor)
                .title(examDTO.getTitle())
                .description(examDTO.getDescription())
                .durationMinutes(examDTO.getDurationMinutes())
                .examDateTime(examDTO.getExamDateTime())
                .build();

        Exam savedExam = examRepository.save(exam);
        log.info("Exam created successfully with ID: {}", savedExam.getId());

        return mapToExamResponseDTO(savedExam);
    }

    @Override
    public ExamResponseDTO updateExam(Long id, ExamUpdateDTO updateDTO, Long instructorId) {
        log.info("Updating exam {} by instructor {}", id, instructorId);

        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));

        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این آزمون نیستید");
        }

        validateExamDateTime(updateDTO.getExamDateTime(), exam.getCourse());

        exam.setTitle(updateDTO.getTitle());
        exam.setDescription(updateDTO.getDescription());
        exam.setDurationMinutes(updateDTO.getDurationMinutes());
        exam.setExamDateTime(updateDTO.getExamDateTime());

        Exam updatedExam = examRepository.save(exam);
        log.info("Exam updated successfully");

        return mapToExamResponseDTO(updatedExam);
    }

    private void validateExamDateTime(LocalDateTime examDateTime, Course course) {
        LocalDate examDate = examDateTime.toLocalDate();

        if (examDateTime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("تاریخ و زمان آزمون نمی‌تواند در گذشته باشد");
        }

        if (examDate.isBefore(course.getStartDate())) {
            throw new BadRequestException(
                    String.format("تاریخ آزمون (%s) نمی‌تواند قبل از تاریخ شروع دوره (%s) باشد",
                            examDate, course.getStartDate())
            );
        }

        if (examDate.isAfter(course.getEndDate())) {
            throw new BadRequestException(
                    String.format("تاریخ آزمون (%s) نمی‌تواند بعد از تاریخ پایان دوره (%s) باشد",
                            examDate, course.getEndDate())
            );
        }

        log.info("Exam date validation passed: {} is within course dates {} to {}",
                examDate, course.getStartDate(), course.getEndDate());
    }


    @Override
    @Transactional(readOnly = true)
    public ExamResponseDTO getExamById(Long id) {
        log.info("Fetching exam with ID: {}", id);
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون با شناسه " + id + " یافت نشد"));
        return mapToExamResponseDTO(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDTO> getAllExams() {
        log.info("Fetching all exams");
        return examRepository.findAll().stream()
                .map(this::mapToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDTO> getExamsByCourse(Long courseId) {
        log.info("Fetching exams for course {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("دوره یافت نشد");
        }

        return examRepository.findByCourseId(courseId).stream()
                .map(this::mapToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDTO> getExamsByInstructor(Long instructorId) {
        log.info("Fetching exams for instructor {}", instructorId);

        if (!userRepository.existsById(instructorId)) {
            throw new ResourceNotFoundException("استاد یافت نشد");
        }

        return examRepository.findAllByInstructorId(instructorId).stream()
                .map(this::mapToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResponseDTO> getExamsByCourseAndInstructor(Long courseId, Long instructorId) {
        log.info("Fetching exams for course {} and instructor {}", courseId, instructorId);

        return examRepository.findByCourseIdAndInstructorId(courseId, instructorId).stream()
                .map(this::mapToExamResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteExam(Long id, Long instructorId) {
        log.info("Deleting exam {} by instructor {}", id, instructorId);

        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));

        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به حذف این آزمون نیستید");
        }

        examRepository.delete(exam);
        log.info("Exam deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public Long countExamsByCourse(Long courseId) {
        return examRepository.countByCourseId(courseId);
    }

    private ExamResponseDTO mapToExamResponseDTO(Exam exam) {
        return ExamResponseDTO.builder()
                .id(exam.getId())
                .courseId(exam.getCourse().getId())
                .courseTitle(exam.getCourse().getTitle())
                .courseCode(exam.getCourse().getCourseCode())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .examDateTime(exam.getExamDateTime())
                .createdByName(exam.getCreatedBy().getFullName())
                .createdById(exam.getCreatedBy().getId())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }
}