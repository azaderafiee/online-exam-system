package net.rafiee.onlineexam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.*;
import net.rafiee.onlineexam.entity.*;
import net.rafiee.onlineexam.enumuration.QuestionType;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.exception.BadRequestException;
import net.rafiee.onlineexam.exception.ResourceNotFoundException;
import net.rafiee.onlineexam.exception.UnauthorizedException;
import net.rafiee.onlineexam.repository.CourseRepository;
import net.rafiee.onlineexam.repository.ExamRepository;
import net.rafiee.onlineexam.repository.QuestionRepository;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.ExamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private final QuestionRepository questionRepository;

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

        Exam exam = new Exam();
        exam.setCourse(course);
        exam.setTitle(examDTO.getTitle());
        exam.setDescription(examDTO.getDescription());
        exam.setDurationMinutes(examDTO.getDurationMinutes());
        exam.setExamDateTime(examDTO.getExamDateTime());
        exam.setCreatedBy(instructor);
        exam.setTotalQuestions(0);
        exam.setTotalScore(0.0);

        Exam savedExam = examRepository.save(exam);
        log.info("Exam created successfully with ID: {}", savedExam.getId());

        return convertToDTO(savedExam);
    }

    @Override
    public ExamResponseDTO getExamById(Long id) {
        log.info("Fetching exam by ID: {}", id);
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون با شناسه " + id + " یافت نشد"));
        return convertToDTO(exam);
    }

    @Override
    public List<ExamResponseDTO> getAllExams() {
        log.info("Fetching all exams");
        return examRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponseDTO> getExamsByCourse(Long courseId) {
        log.info("Fetching exams for course: {}", courseId);
        
        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("دوره با شناسه " + courseId + " یافت نشد");
        }
        
        return examRepository.findByCourseId(courseId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponseDTO> getExamsByInstructor(Long instructorId) {
        log.info("Fetching exams for instructor: {}", instructorId);
        
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));
        
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new BadRequestException("کاربر مورد نظر استاد نیست");
        }
        
        return examRepository.findByCreatedById(instructorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ExamResponseDTO> getExamsByCourseAndInstructor(Long courseId, Long instructorId) {
        log.info("Fetching exams for course {} and instructor {}", courseId, instructorId);
        return examRepository.findByCourseIdAndInstructorInstructorId(courseId, instructorId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ExamResponseDTO updateExam(Long id, ExamUpdateDTO updateDTO, Long instructorId) {
        log.info("Updating exam: {}", id);
        
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));
        
        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این آزمون نیستید");
        }
        
        if (updateDTO.getTitle() != null) {
            exam.setTitle(updateDTO.getTitle());
        }
        if (updateDTO.getDescription() != null) {
            exam.setDescription(updateDTO.getDescription());
        }
        if (updateDTO.getDurationMinutes() != null) {
            exam.setDurationMinutes(updateDTO.getDurationMinutes());
        }
        if (updateDTO.getExamDateTime() != null) {
            exam.setExamDateTime(updateDTO.getExamDateTime());
        }
        
        exam.setUpdatedAt(LocalDateTime.now());
        Exam updatedExam = examRepository.save(exam);
        
        log.info("Exam updated successfully: {}", id);
        return convertToDTO(updatedExam);
    }

    @Override
    public void deleteExam(Long id, Long instructorId) {
        log.info("Deleting exam: {}", id);
        
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));
        
        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به حذف این آزمون نیستید");
        }
        
        examRepository.delete(exam);
        log.info("Exam deleted successfully: {}", id);
    }

    @Override
    public Long countExamsByCourse(Long courseId) {
        return examRepository.countByCourseId(courseId);
    }

    @Override
    public void addQuestionToExam(Long examId, AddQuestionToExamDTO dto, Long instructorId) {
        log.info("Adding question {} to exam {}", dto.getQuestionId(), examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));
                
        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این آزمون نیستید");
        }
        
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("سوال یافت نشد"));
        
        // Check if question already exists in exam
        boolean exists = exam.getExamQuestions().stream()
                .anyMatch(eq -> eq.getQuestion().getId().equals(dto.getQuestionId()));
        
        if (exists) {
            throw new BadRequestException("این سوال قبلاً به آزمون اضافه شده است");
        }
        
        ExamQuestion examQuestion = new ExamQuestion();
        examQuestion.setExam(exam);
        examQuestion.setQuestion(question);
        examQuestion.setScore(dto.getScore());
        examQuestion.setOrderNumber(dto.getOrderNumber());
        
        exam.getExamQuestions().add(examQuestion);
        updateExamTotals(exam);
        
        examRepository.save(exam);
        log.info("Question added to exam successfully");
    }
    
    @Override
    public void addMultipleQuestionsToExam(Long examId, List<AddQuestionToExamDTO> questions, Long instructorId) {
        log.info("Adding {} questions to exam {}", questions.size(), examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));
                
        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این آزمون نیستید");
        }
        
        int addedCount = 0;
        for (AddQuestionToExamDTO dto : questions) {
            Question question = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new ResourceNotFoundException("سوال " + dto.getQuestionId() + " یافت نشد"));
            
            // Check if not already added
            boolean exists = exam.getExamQuestions().stream()
                    .anyMatch(eq -> eq.getQuestion().getId().equals(dto.getQuestionId()));
            
            if (!exists) {
                ExamQuestion examQuestion = new ExamQuestion();
                examQuestion.setExam(exam);
                examQuestion.setQuestion(question);
                examQuestion.setScore(dto.getScore());
                examQuestion.setOrderNumber(dto.getOrderNumber());
                
                exam.getExamQuestions().add(examQuestion);
                addedCount++;
            }
        }
        
        updateExamTotals(exam);
        examRepository.save(exam);
        log.info("Added {} questions to exam successfully", addedCount);
    }
    
    @Override
    public void removeQuestionFromExam(Long examId, Long questionId, Long instructorId) {
        log.info("Removing question {} from exam {}", questionId, examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));
                
        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این آزمون نیستید");
        }
        
        boolean removed = exam.getExamQuestions().removeIf(
            eq -> eq.getQuestion().getId().equals(questionId)
        );
        
        if (!removed) {
            throw new ResourceNotFoundException("این سوال در آزمون موجود نیست");
        }
        
        updateExamTotals(exam);
        examRepository.save(exam);
        log.info("Question removed from exam successfully");
    }
    
    @Override
    public void updateQuestionScore(Long examId, Long questionId, Double newScore, Long instructorId) {
        log.info("Updating question {} score in exam {} to {}", questionId, examId, newScore);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));
                
        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این آزمون نیستید");
        }
        
        ExamQuestion examQuestion = exam.getExamQuestions().stream()
                .filter(eq -> eq.getQuestion().getId().equals(questionId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("این سوال در آزمون موجود نیست"));
        
        examQuestion.setScore(newScore);
        updateExamTotals(exam);
        
        examRepository.save(exam);
        log.info("Question score updated successfully");
    }
    
    private void updateExamTotals(Exam exam) {
        int totalQuestions = exam.getExamQuestions().size();
        double totalScore = exam.getExamQuestions().stream()
                .mapToDouble(ExamQuestion::getScore)
                .sum();
        
        exam.setTotalQuestions(totalQuestions);
        exam.setTotalScore(totalScore);
    }
    
    @Override
    public List<net.rafiee.onlineexam.dto.ExamQuestionDTO> getExamQuestions(Long examId, Long instructorId) {
        log.info("Getting questions for exam {}", examId);
        
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));
                
        if (!exam.getCreatedBy().getId().equals(instructorId)) {
            throw new UnauthorizedException("شما مجاز به مشاهده این آزمون نیستید");
        }
        
        return exam.getExamQuestions().stream()
                .sorted((eq1, eq2) -> eq1.getOrderNumber().compareTo(eq2.getOrderNumber()))
                .map(this::convertToExamQuestionDTO)
                .collect(Collectors.toList());
    }
    
    private ExamQuestionDTO convertToExamQuestionDTO(ExamQuestion examQuestion) {
        Question question = examQuestion.getQuestion();
        boolean hasOptions = question.getType() == QuestionType.MULTIPLE_CHOICE;
        int optionsCount =0;
        if (question instanceof MultipleChoiceQuestion multipleChoiceQuestion) {
             optionsCount = hasOptions ? multipleChoiceQuestion.getOptions().size() : 0;
        }

        return ExamQuestionDTO.builder()
                .id(examQuestion.getId())
                .questionId(question.getId())
                .questionText(question.getQuestionText())
                .questionType(question.getType().name())
                .score(examQuestion.getScore())
                .orderNumber(examQuestion.getOrderNumber())
                .hasOptions(hasOptions)
                .optionsCount(optionsCount)
                .build();
    }

    private ExamResponseDTO convertToDTO(Exam exam) {
        return ExamResponseDTO.builder()
                .id(exam.getId())
                .courseId(exam.getCourse().getId())
                .courseTitle(exam.getCourse().getTitle())
                .courseCode(exam.getCourse().getCourseCode())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .totalQuestions(exam.getTotalQuestions())
                .totalScore(exam.getTotalScore())
                .examDateTime(exam.getExamDateTime())
                .createdByName(exam.getCreatedBy().getFullName())
                .createdById(exam.getCreatedBy().getId())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .build();
    }
}