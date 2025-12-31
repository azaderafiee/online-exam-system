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
import net.rafiee.onlineexam.repository.*;
import net.rafiee.onlineexam.service.QuestionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final MultipleChoiceQuestionRepository multipleChoiceQuestionRepository;
    private final DescriptiveQuestionRepository descriptiveQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    @Override
    public QuestionResponseDTO createMultipleChoiceQuestion(MultipleChoiceQuestionDTO dto, String username) {
        log.info("Creating multiple choice question by instructor {}", username);

        // بررسی دوره
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        // بررسی استاد
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        validateInstructor(instructor, course);

        // Validation: حداقل یک گزینه صحیح
        long correctOptionsCount = dto.getOptions().stream()
                .filter(QuestionOptionDTO::getIsCorrect)
                .count();

        if (correctOptionsCount == 0) {
            throw new BadRequestException("حداقل یک گزینه صحیح باید انتخاب شود");
        }

        if (correctOptionsCount > 1) {
            throw new BadRequestException("فقط یک گزینه می‌تواند صحیح باشد");
        }

        // ایجاد سوال
        MultipleChoiceQuestion question = MultipleChoiceQuestion.builder()
                .course(course)
                .createdBy(instructor)
                .title(dto.getTitle())
                .questionText(dto.getQuestionText())
                .options(new ArrayList<>())
                .build();

        // اضافه کردن گزینه‌ها
        for (int i = 0; i < dto.getOptions().size(); i++) {
            QuestionOptionDTO optionDTO = dto.getOptions().get(i);
            QuestionOption option = QuestionOption.builder()
                    .question(question)
                    .optionText(optionDTO.getOptionText())
                    .isCorrect(optionDTO.getIsCorrect())
                    .orderIndex(optionDTO.getOrderIndex() != null ? optionDTO.getOrderIndex() : i)
                    .build();
            question.addOption(option);
        }

        MultipleChoiceQuestion savedQuestion = multipleChoiceQuestionRepository.save(question);
        log.info("Multiple choice question created successfully with ID: {}", savedQuestion.getId());

        return mapToQuestionResponseDTO(savedQuestion);
    }

    @Override
    public QuestionResponseDTO createDescriptiveQuestion(DescriptiveQuestionDTO dto, String username) {
        log.info("Creating descriptive question by instructor {}", username);

        // بررسی دوره
        Course course = courseRepository.findById(dto.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("دوره یافت نشد"));

        // بررسی استاد
        User instructor = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("استاد یافت نشد"));

        validateInstructor(instructor, course);

        // ایجاد سوال
        DescriptiveQuestion question = DescriptiveQuestion.builder()
                .course(course)
                .createdBy(instructor)
                .title(dto.getTitle())
                .questionText(dto.getQuestionText())
                .sampleAnswer(dto.getSampleAnswer())
                .maxWords(dto.getMaxWords())
                .build();

        DescriptiveQuestion savedQuestion = descriptiveQuestionRepository.save(question);
        log.info("Descriptive question created successfully with ID: {}", savedQuestion.getId());

        return mapToQuestionResponseDTO(savedQuestion);
    }

    @Override
    @Transactional(readOnly = true)
    public QuestionResponseDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("سوال یافت نشد"));
        return mapToQuestionResponseDTO(question);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByCourse(Long courseId) {
        log.info("Fetching questions for course {}", courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("دوره یافت نشد");
        }

        return questionRepository.findByCourseId(courseId).stream()
                .map(this::mapToQuestionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Question> getByCourseId(Long courseId) {
        return questionRepository.findByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByCourseAndType(Long courseId, QuestionType type) {
        log.info("Fetching {} questions for course {}", type, courseId);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("دوره یافت نشد");
        }

        return questionRepository.findByCourseId(courseId).stream()
                .map(this::mapToQuestionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> getQuestionsByInstructor(String username, Long courseId) {
        log.info("Fetching questions for instructor {} in course {}", username, courseId);

        return questionRepository.findByCourseIdAndInstructorId(courseId, username).stream()
                .map(this::mapToQuestionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> searchQuestionsInCourse(Long courseId, String keyword) {
        log.info("Searching questions in course {} with keyword: {}", courseId, keyword);

        if (!courseRepository.existsById(courseId)) {
            throw new ResourceNotFoundException("دوره یافت نشد");
        }

        return questionRepository.searchInCourse(courseId, keyword).stream()
                .map(this::mapToQuestionResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public QuestionResponseDTO updateMultipleChoiceQuestion(Long id, MultipleChoiceQuestionDTO dto, String username) {
        log.info("Updating multiple choice question {} by instructor {}", id, username);

        MultipleChoiceQuestion question = multipleChoiceQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("سوال یافت نشد"));

        // بررسی مالکیت
        if (!question.getCreatedBy().getUsername().equals(username)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این سوال نیستید");
        }

        // Validation گزینه صحیح
        long correctOptionsCount = dto.getOptions().stream()
                .filter(QuestionOptionDTO::getIsCorrect)
                .count();

        if (correctOptionsCount != 1) {
            throw new BadRequestException("دقیقاً یک گزینه باید صحیح باشد");
        }

        // به‌روزرسانی فیلدها
        question.setTitle(dto.getTitle());
        question.setQuestionText(dto.getQuestionText());

        // حذف گزینه‌های قدیمی
        question.getOptions().clear();

        // اضافه کردن گزینه‌های جدید
        for (int i = 0; i < dto.getOptions().size(); i++) {
            QuestionOptionDTO optionDTO = dto.getOptions().get(i);
            QuestionOption option = QuestionOption.builder()
                    .question(question)
                    .optionText(optionDTO.getOptionText())
                    .isCorrect(optionDTO.getIsCorrect())
                    .orderIndex(optionDTO.getOrderIndex() != null ? optionDTO.getOrderIndex() : i)
                    .build();
            question.addOption(option);
        }

        MultipleChoiceQuestion updatedQuestion = multipleChoiceQuestionRepository.save(question);
        log.info("Question updated successfully");

        return mapToQuestionResponseDTO(updatedQuestion);
    }

    @Override
    public QuestionResponseDTO updateDescriptiveQuestion(Long id, DescriptiveQuestionDTO dto, String username) {
        log.info("Updating descriptive question {} by instructor {}", id, username);

        DescriptiveQuestion question = descriptiveQuestionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("سوال یافت نشد"));

        // بررسی مالکیت
        if (!question.getCreatedBy().getUsername().equals(username)) {
            throw new UnauthorizedException("شما مجاز به ویرایش این سوال نیستید");
        }

        // به‌روزرسانی
        question.setTitle(dto.getTitle());
        question.setQuestionText(dto.getQuestionText());
        question.setSampleAnswer(dto.getSampleAnswer());
        question.setMaxWords(dto.getMaxWords());

        DescriptiveQuestion updatedQuestion = descriptiveQuestionRepository.save(question);
        log.info("Question updated successfully");

        return mapToQuestionResponseDTO(updatedQuestion);
    }

    @Override
    public void deleteQuestion(Long id, String username) {
        log.info("Deleting question {} by instructor {}", id, username);

        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("سوال یافت نشد"));

        // بررسی مالکیت
        if (!question.getCreatedBy().getUsername().equals(username)) {
            throw new UnauthorizedException("شما مجاز به حذف این سوال نیستید");
        }

        // بررسی: آیا سوال در آزمونی استفاده شده؟
        if (!question.getExamQuestions().isEmpty()) {
            throw new BadRequestException(
                    "این سوال در " + question.getExamQuestions().size() +
                            " آزمون استفاده شده است. ابتدا آن را از آزمون‌ها حذف کنید"
            );
        }

        questionRepository.delete(question);
        log.info("Question deleted successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public Long countQuestionsByCourse(Long courseId) {
        return questionRepository.countByCourseId(courseId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long countQuestionsByCourseAndType(Long courseId, QuestionType type) {
        return questionRepository.countByCourseId(courseId);
    }

    // Helper Methods

    private void validateInstructor(User instructor, Course course) {
        if (instructor.getRole() != UserRole.INSTRUCTOR) {
            throw new UnauthorizedException("فقط استادان مجاز به ایجاد سوال هستند");
        }

        if (!course.getInstructors().contains(instructor)) {
            throw new UnauthorizedException("شما استاد این دوره نیستید");
        }
    }

    private QuestionResponseDTO mapToQuestionResponseDTO(Question question) {
        QuestionResponseDTO dto = QuestionResponseDTO.builder()
                .id(question.getId())
                .courseId(question.getCourse().getId())
                .courseTitle(question.getCourse().getTitle())
                .title(question.getTitle())
                .questionText(question.getQuestionText())
                .type(question.getType())
                .createdByName(question.getCreatedBy().getFullName())
                .createdById(question.getCreatedBy().getId())
                .createdAt(question.getCreatedAt())
                .updatedAt(question.getUpdatedAt())
                .build();

        if (question instanceof MultipleChoiceQuestion mcq) {
            dto.setOptions(mcq.getOptions().stream()
                    .map(this::mapToOptionResponseDTO)
                    .collect(Collectors.toList()));
        } else if (question instanceof DescriptiveQuestion dq) {
            dto.setSampleAnswer(dq.getSampleAnswer());
            dto.setMaxWords(dq.getMaxWords());
        }

        return dto;
    }

    private QuestionOptionResponseDTO mapToOptionResponseDTO(QuestionOption option) {
        return QuestionOptionResponseDTO.builder()
                .id(option.getId())
                .optionText(option.getOptionText())
                .isCorrect(option.getIsCorrect())
                .orderIndex(option.getOrderIndex())
                .build();
    }
}