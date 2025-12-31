package net.rafiee.onlineexam.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.*;
import net.rafiee.onlineexam.entity.*;
import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.exception.BadRequestException;
import net.rafiee.onlineexam.exception.ResourceNotFoundException;
import net.rafiee.onlineexam.exception.UnauthorizedException;
import net.rafiee.onlineexam.repository.ExamQuestionRepository;
import net.rafiee.onlineexam.repository.ExamRepository;
import net.rafiee.onlineexam.repository.QuestionRepository;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.ExamQuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ExamQuestionServiceImpl implements ExamQuestionService {

    private final ExamRepository examRepository;
    private final QuestionRepository questionRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final UserRepository userRepository;

    @Override
    public ExamQuestionResponseDTO addQuestionToExam(Long examId, AddQuestionToExamDTO dto, String username) {
        log.info("Adding question {} to exam {} by instructor {}", dto.getQuestionId(), examId, username);

        // بررسی آزمون
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));

        // بررسی استاد
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        validateInstructorAccess(exam, instructor);

        // بررسی سوال
        Question question = questionRepository.findById(dto.getQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("سوال یافت نشد"));

        // بررسی: سوال باید از همان دوره باشد
        if (!question.getCourse().getId().equals(exam.getCourse().getId())) {
            throw new BadRequestException("سوال باید از همان دوره آزمون باشد");
        }

        // بررسی: سوال تکراری نباشد
        boolean exists = examQuestionRepository.existsByExamIdAndQuestionId(examId, dto.getQuestionId());
        if (exists) {
            throw new BadRequestException("این سوال قبلاً به آزمون اضافه شده است");
        }

        // بررسی نمره
        if (dto.getScore() == null || dto.getScore() < 0) {
            throw new BadRequestException("نمره سوال نامعتبر است");
        }

        // تعیین orderIndex
        Integer orderIndex = dto.getOrderIndex();
        if (orderIndex == null) {
            // آخرین ترتیب
            orderIndex = examQuestionRepository.findMaxOrderIndexByExamId(examId)
                    .map(max -> max + 1)
                    .orElse(0);
        }

        // ایجاد ExamQuestion
        ExamQuestion examQuestion = ExamQuestion.builder()
                .exam(exam)
                .question(question)
                .score(dto.getScore())
                .orderIndex(orderIndex)
                .build();

        ExamQuestion saved = examQuestionRepository.save(examQuestion);
        log.info("Question added to exam successfully");

        return mapToExamQuestionResponseDTO(saved);
    }

    @Override
    public List<ExamQuestionResponseDTO> addMultipleQuestionsToExam(Long examId, AddMultipleQuestionsToExamDTO dto, String username) {
        log.info("Adding {} questions to exam {} by instructor {}", dto.getQuestions().size(), examId, username);

        List<ExamQuestionResponseDTO> results = new ArrayList<>();

        for (AddQuestionToExamDTO questionDto : dto.getQuestions()) {
            try {
                ExamQuestionResponseDTO result = addQuestionToExam(examId, questionDto, username);
                results.add(result);
            } catch (Exception e) {
                log.warn("Failed to add question {} to exam: {}", questionDto.getQuestionId(), e.getMessage());
                // ادامه با سوالات بعدی
            }
        }

        log.info("Successfully added {} out of {} questions to exam", results.size(), dto.getQuestions().size());
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamQuestionResponseDTO> getExamQuestions(Long examId) {
        log.info("Fetching questions for exam {}", examId);

        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("آزمون یافت نشد");
        }

        return examQuestionRepository.findByExamIdOrderByOrderIndexAsc(examId).stream()
                .map(this::mapToExamQuestionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ExamWithQuestionsResponseDTO getExamWithQuestions(Long examId) {
        log.info("Fetching exam with questions for exam {}", examId);

        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));

        List<ExamQuestionResponseDTO> questions = getExamQuestions(examId);

        return ExamWithQuestionsResponseDTO.builder()
                .id(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .examDateTime(exam.getExamDateTime())
                .courseId(exam.getCourse().getId())
                .courseTitle(exam.getCourse().getTitle())
                .courseCode(exam.getCourse().getCourseCode())
                .createdById(exam.getCreatedBy().getId())
                .createdByName(exam.getCreatedBy().getFullName())
                .createdAt(exam.getCreatedAt())
                .updatedAt(exam.getUpdatedAt())
                .questions(questions)
                .totalQuestions(questions.size())
                .totalScore(calculateExamTotalScore(examId))
                .build();
    }

    @Override
    public ExamQuestionResponseDTO updateQuestionScore(Long examId, Long questionId, UpdateQuestionScoreDTO dto, String username) {
        log.info("Updating score for question {} in exam {} by instructor {}", questionId, examId, username);

        // بررسی آزمون
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));

        // بررسی استاد
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        validateInstructorAccess(exam, instructor);

        // بررسی نمره
        if (dto.getScore() == null || dto.getScore() < 0) {
            throw new BadRequestException("نمره نامعتبر است");
        }

        // پیدا کردن ExamQuestion
        ExamQuestion examQuestion = examQuestionRepository.findByExamIdAndQuestionId(examId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException("سوال در این آزمون یافت نشد"));

        examQuestion.setScore(dto.getScore());
        ExamQuestion updated = examQuestionRepository.save(examQuestion);

        log.info("Question score updated successfully");
        return mapToExamQuestionResponseDTO(updated);
    }

    @Override
    public void removeQuestionFromExam(Long examId, Long questionId, String username) {
        log.info("Removing question {} from exam {} by instructor {}", questionId, examId, username);

        // بررسی آزمون
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));

        // بررسی استاد
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        validateInstructorAccess(exam, instructor);

        // پیدا کردن و حذف
        ExamQuestion examQuestion = examQuestionRepository.findByExamIdAndQuestionId(examId, questionId)
                .orElseThrow(() -> new ResourceNotFoundException("سوال در این آزمون یافت نشد"));

        examQuestionRepository.delete(examQuestion);
        log.info("Question removed from exam successfully");
    }

    @Override
    public void reorderQuestions(Long examId, List<Long> questionIds, String username) {
        log.info("Reordering {} questions in exam {} by instructor {}", questionIds.size(), examId, username);

        // بررسی آزمون
        Exam exam = examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون یافت نشد"));

        // بررسی استاد
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        validateInstructorAccess(exam, instructor);

        // به‌روزرسانی ترتیب
        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            int finalI = i;
            examQuestionRepository.findByExamIdAndQuestionId(examId, questionId)
                    .ifPresent(eq -> {
                        eq.setOrderIndex(finalI);
                        examQuestionRepository.save(eq);
                    });
        }

        log.info("Questions reordered successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public Double calculateExamTotalScore(Long examId) {
        if (!examRepository.existsById(examId)) {
            throw new ResourceNotFoundException("آزمون یافت نشد");
        }

        return examQuestionRepository.findByExamIdOrderByOrderIndexAsc(examId).stream()
                .mapToDouble(ExamQuestion::getScore)
                .sum();
    }

    // Helper Methods

    private void validateInstructorAccess(Exam exam, User instructor) {
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new UnauthorizedException("فقط استادان مجاز به این عملیات هستند");
        }

        if (!exam.getCourse().getInstructors().contains(instructor)) {
            throw new UnauthorizedException("شما استاد این دوره نیستید");
        }
    }

    private ExamQuestionResponseDTO mapToExamQuestionResponseDTO(ExamQuestion examQuestion) {
        Question question = examQuestion.getQuestion();

        ExamQuestionResponseDTO.ExamQuestionResponseDTOBuilder builder = ExamQuestionResponseDTO.builder()
                .examQuestionId(examQuestion.getId())
                .questionId(question.getId())
                .title(question.getTitle())
                .questionText(question.getQuestionText())
                .type(question.getType())
                .score(examQuestion.getScore())
                .orderIndex(examQuestion.getOrderIndex());

        // اضافه کردن جزئیات بر اساس نوع سوال
        if (question instanceof MultipleChoiceQuestion mcq) {
            List<QuestionOptionResponseDTO> options = mcq.getOptions().stream()
                    .map(opt -> QuestionOptionResponseDTO.builder()
                            .id(opt.getId())
                            .optionText(opt.getOptionText())
                            .isCorrect(opt.getIsCorrect())
                            .orderIndex(opt.getOrderIndex())
                            .build())
                    .collect(Collectors.toList());
            builder.options(options);
        } else if (question instanceof DescriptiveQuestion dq) {
            builder.sampleAnswer(dq.getSampleAnswer())
                    .maxWords(dq.getMaxWords());
        }

        return builder.build();
    }
}