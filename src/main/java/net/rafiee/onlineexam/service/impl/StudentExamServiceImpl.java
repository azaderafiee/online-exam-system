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
import net.rafiee.onlineexam.service.StudentExamService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StudentExamServiceImpl implements StudentExamService {

    private final StudentExamRepository studentExamRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;
    private final ExamQuestionRepository examQuestionRepository;
    private final QuestionOptionRepository questionOptionRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional(readOnly = true)
    public List<CourseResponseDTO> getStudentCourses(Long studentId) {
        User student = getUserById(studentId);
        validateStudentRole(student);

        return student.getEnrolledCourses().stream()
                .map(this::convertToCourseResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamForStudentDTO> getCourseExamsForStudent(Long studentId, Long courseId) {
        User student = getUserById(studentId);
        validateStudentRole(student);

        Course course = getCourseById(courseId);
        
        // بررسی اینکه دانشجو در این دوره ثبت‌نام کرده باشد
        if (!student.getEnrolledCourses().contains(course)) {
            throw new UnauthorizedException("شما در این دوره ثبت‌نام نکرده‌اید");
        }

        List<Exam> exams = examRepository.findByCourseId(course.getId());
        
        return exams.stream()
                .map(exam -> convertToExamForStudentDTO(exam, student))
                .collect(Collectors.toList());
    }

    @Override
    public StartExamResponseDTO startExam(Long studentId, Long examId) {
        User student = getUserById(studentId);
        validateStudentRole(student);

        Exam exam = getExamById(examId);

        // بررسی اینکه دانشجو در دوره این آزمون ثبت‌نام کرده باشد
        if (!student.getEnrolledCourses().contains(exam.getCourse())) {
            throw new UnauthorizedException("شما در این دوره ثبت‌نام نکرده‌اید");
        }

        // بررسی اینکه قبلاً در آزمون شرکت نکرده باشد
        if (studentExamRepository.existsByStudentAndExam(student, exam)) {
            throw new BadRequestException("شما قبلاً در این آزمون شرکت کرده‌اید");
        }

        // ایجاد رکورد جدید شرکت در آزمون
        StudentExam studentExam = StudentExam.builder()
                .student(student)
                .exam(exam)
                .startTime(LocalDateTime.now())
                .isCompleted(false)
                .maxScore(exam.getTotalScore())
                .answers(new ArrayList<>())
                .build();

        studentExam = studentExamRepository.save(studentExam);
        log.info("Student {} started exam {}", studentId, examId);

        return convertToStartExamResponseDTO(studentExam);
    }

    @Override
    @Transactional(readOnly = true)
    public StartExamResponseDTO getExamProgress(Long studentId, Long studentExamId) {
        StudentExam studentExam = getStudentExamById(studentExamId);
        validateStudentOwnership(studentId, studentExam);

        if (studentExam.getIsCompleted()) {
            throw new BadRequestException("این آزمون قبلاً تکمیل شده است");
        }

        // بررسی اینکه زمان آزمون تمام نشده باشد
        if (studentExam.isTimeExpired()) {
            // اگر زمان تمام شده، خودکار آزمون را تمام می‌کنیم
            completeExam(studentId, studentExamId);
            throw new BadRequestException("زمان آزمون به پایان رسیده است");
        }

        return convertToStartExamResponseDTO(studentExam);
    }

    @Override
    public void submitAnswer(Long studentId, Long studentExamId, SubmitAnswerDTO answerDTO) {
        StudentExam studentExam = getStudentExamById(studentExamId);
        validateStudentOwnership(studentId, studentExam);

        if (studentExam.getIsCompleted()) {
            throw new BadRequestException("آزمون تکمیل شده است و امکان تغییر پاسخ وجود ندارد");
        }

        if (studentExam.isTimeExpired()) {
            completeExam(studentId, studentExamId);
            throw new BadRequestException("زمان آزمون به پایان رسیده است");
        }

        ExamQuestion examQuestion = examQuestionRepository.findById(answerDTO.getExamQuestionId())
                .orElseThrow(() -> new ResourceNotFoundException("سوال با شناسه " + answerDTO.getExamQuestionId() + " یافت نشد"));

        // بررسی اینکه سوال متعلق به همین آزمون باشد
        if (!examQuestion.getExam().getId().equals(studentExam.getExam().getId())) {
            throw new BadRequestException("این سوال متعلق به این آزمون نیست");
        }

        // بررسی اینکه آیا قبلاً پاسخی ثبت شده یا نه
        StudentAnswer existingAnswer = studentAnswerRepository
                .findByStudentExamAndExamQuestion(studentExam, examQuestion)
                .orElse(null);

        if (existingAnswer != null) {
            // به‌روزرسانی پاسخ موجود
            updateAnswer(existingAnswer, answerDTO, examQuestion.getQuestion());
        } else {
            // ایجاد پاسخ جدید
            createNewAnswer(studentExam, examQuestion, answerDTO);
        }

        log.info("Answer submitted for student {} in exam {} for question {}", 
                studentId, studentExamId, answerDTO.getExamQuestionId());
    }

    @Override
    public void submitMultipleAnswers(Long studentId, Long studentExamId, List<SubmitAnswerDTO> answers) {
        for (SubmitAnswerDTO answer : answers) {
            try {
                submitAnswer(studentId, studentExamId, answer);
            } catch (Exception e) {
                log.error("Error submitting answer for question {}: {}", 
                        answer.getExamQuestionId(), e.getMessage());
                // ادامه می‌دهیم تا سایر پاسخ‌ها ذخیره شوند
            }
        }
    }

    @Override
    public ExamResultDTO completeExam(Long studentId, Long studentExamId) {
        StudentExam studentExam = getStudentExamById(studentExamId);
        validateStudentOwnership(studentId, studentExam);

        if (studentExam.getIsCompleted()) {
            throw new BadRequestException("این آزمون قبلاً تکمیل شده است");
        }

        // تکمیل آزمون
        studentExam.completeExam();

        // محاسبه نمره سوالات چندگزینه‌ای
        gradeMultipleChoiceAnswers(studentExam);

        // محاسبه نمره کل
        calculateTotalScore(studentExam);

        studentExamRepository.save(studentExam);
        log.info("Exam {} completed for student {}", studentExamId, studentId);

        return convertToExamResultDTO(studentExam);
    }

    @Override
    @Transactional(readOnly = true)
    public ExamResultDTO getExamResult(Long studentId, Long studentExamId) {
        StudentExam studentExam = getStudentExamById(studentExamId);
        validateStudentOwnership(studentId, studentExam);

        if (!studentExam.getIsCompleted()) {
            throw new BadRequestException("این آزمون هنوز تکمیل نشده است");
        }

        return convertToExamResultDTO(studentExam);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean canStudentParticipate(Long studentId, Long examId) {
        User student = getUserById(studentId);
        Exam exam = getExamById(examId);

        // بررسی اینکه دانشجو در دوره ثبت‌نام کرده باشد
        if (!student.getEnrolledCourses().contains(exam.getCourse())) {
            return false;
        }

        // بررسی اینکه قبلاً شرکت نکرده باشد
        return !studentExamRepository.existsByStudentAndExam(student, exam);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExamResultDTO> getCompletedExams(Long studentId) {
        User student = getUserById(studentId);
        validateStudentRole(student);

        List<StudentExam> completedExams = studentExamRepository.findByStudentAndIsCompletedTrue(student);

        return completedExams.stream()
                .map(this::convertToExamResultDTO)
                .collect(Collectors.toList());
    }

    // ========== Helper Methods ==========

    private void createNewAnswer(StudentExam studentExam, ExamQuestion examQuestion, SubmitAnswerDTO answerDTO) {
        Question question = examQuestion.getQuestion();
        
        StudentAnswer answer = StudentAnswer.builder()
                .studentExam(studentExam)
                .examQuestion(examQuestion)
                .build();

        if (question instanceof MultipleChoiceQuestion) {
            // سوال چندگزینه‌ای
            if (answerDTO.getSelectedOptionId() == null) {
                throw new BadRequestException("برای سوال چندگزینه‌ای باید گزینه‌ای انتخاب شود");
            }

            QuestionOption option = questionOptionRepository.findById(answerDTO.getSelectedOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("گزینه با شناسه " + answerDTO.getSelectedOptionId() + " یافت نشد"));

            // بررسی اینکه گزینه متعلق به این سوال باشد
            if (!option.getQuestion().getId().equals(question.getId())) {
                throw new BadRequestException("این گزینه متعلق به این سوال نیست");
            }

            answer.setSelectedOption(option);
            // نمره‌دهی خودکار
            answer.calculateScoreForMultipleChoice();

        } else if (question instanceof DescriptiveQuestion) {
            // سوال تشریحی
            if (answerDTO.getTextAnswer() == null || answerDTO.getTextAnswer().trim().isEmpty()) {
                throw new BadRequestException("برای سوال تشریحی باید پاسخ متنی وارد شود");
            }

            answer.setTextAnswer(answerDTO.getTextAnswer().trim());
            answer.setIsGraded(false); // نمره‌دهی دستی توسط استاد
        }

        studentAnswerRepository.save(answer);
        studentExam.addAnswer(answer);
    }

    private void updateAnswer(StudentAnswer answer, SubmitAnswerDTO answerDTO, Question question) {
        if (question instanceof MultipleChoiceQuestion) {
            if (answerDTO.getSelectedOptionId() == null) {
                throw new BadRequestException("برای سوال چندگزینه‌ای باید گزینه‌ای انتخاب شود");
            }

            QuestionOption option = questionOptionRepository.findById(answerDTO.getSelectedOptionId())
                    .orElseThrow(() -> new ResourceNotFoundException("گزینه با شناسه " + answerDTO.getSelectedOptionId() + " یافت نشد"));

            if (!option.getQuestion().getId().equals(question.getId())) {
                throw new BadRequestException("این گزینه متعلق به این سوال نیست");
            }

            answer.setSelectedOption(option);
            answer.calculateScoreForMultipleChoice();

        } else if (question instanceof DescriptiveQuestion) {
            if (answerDTO.getTextAnswer() == null || answerDTO.getTextAnswer().trim().isEmpty()) {
                throw new BadRequestException("برای سوال تشریحی باید پاسخ متنی وارد شود");
            }

            answer.setTextAnswer(answerDTO.getTextAnswer().trim());
            // نمره قبلی را حفظ می‌کنیم (اگر استاد نمره داده باشد)
        }

        studentAnswerRepository.save(answer);
    }

    private void gradeMultipleChoiceAnswers(StudentExam studentExam) {
        List<StudentAnswer> answers = studentExam.getAnswers();
        
        for (StudentAnswer answer : answers) {
            Question question = answer.getExamQuestion().getQuestion();
            
            if (question instanceof MultipleChoiceQuestion && !answer.getIsGraded()) {
                answer.calculateScoreForMultipleChoice();
                studentAnswerRepository.save(answer);
            }
        }
    }

    private void calculateTotalScore(StudentExam studentExam) {
        Double totalScore = studentAnswerRepository.calculateTotalScore(studentExam);
        studentExam.setTotalScore(totalScore != null ? totalScore : 0.0);
    }

    // ========== Conversion Methods ==========

    private CourseResponseDTO convertToCourseResponseDTO(Course course) {
        return CourseResponseDTO.builder()
                .id(course.getId())
                .title(course.getTitle())
                .courseCode(course.getCourseCode())
                .description(course.getDescription())
                .startDate(course.getStartDate())
                .endDate(course.getEndDate())
                .instructorCount(course.getInstructors().size())
                .studentCount(course.getStudents().size())
                .createdAt(course.getCreatedAt())
                .build();
    }

    private ExamForStudentDTO convertToExamForStudentDTO(Exam exam, User student) {
        StudentExam studentExam = studentExamRepository
                .findByStudentAndExam(student, exam)
                .orElse(null);

        // Get instructor name
        String instructorName = exam.getCreatedBy() != null ? exam.getCreatedBy().getFullName() : "نامشخص";

        ExamForStudentDTO dto = ExamForStudentDTO.builder()
                .examId(exam.getId())
                .title(exam.getTitle())
                .description(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .examDateTime(exam.getExamDateTime())
                .totalQuestions(exam.getExamQuestions().size())
                .totalScore(exam.getTotalScore())
                .courseId(exam.getCourse().getId())
                .courseTitle(exam.getCourse().getTitle())
                .instructorName(instructorName)
                .hasParticipated(studentExam != null)
                .canParticipate(studentExam == null)
                .build();

        if (studentExam != null) {
            dto.setStudentExamId(studentExam.getId());
            dto.setIsCompleted(studentExam.getIsCompleted());
            dto.setIsInProgress(studentExam.isInProgress());
            dto.setRemainingSeconds(studentExam.getRemainingSeconds());
            dto.setStudentScore(studentExam.getTotalScore());
            dto.setIsFullyGraded(studentAnswerRepository.areAllQuestionsGraded(studentExam));
        }

        return dto;
    }

    private StartExamResponseDTO convertToStartExamResponseDTO(StudentExam studentExam) {
        Exam exam = studentExam.getExam();
        
        List<ExamQuestionForStudentDTO> questions = exam.getExamQuestions().stream()
                .map(eq -> convertToExamQuestionForStudentDTO(eq, studentExam))
                .collect(Collectors.toList());

        return StartExamResponseDTO.builder()
                .studentExamId(studentExam.getId())
                .examId(exam.getId())
                .examTitle(exam.getTitle())
                .examDescription(exam.getDescription())
                .durationMinutes(exam.getDurationMinutes())
                .startTime(studentExam.getStartTime())
                .remainingSeconds(studentExam.getRemainingSeconds())
                .totalQuestions(questions.size())
                .maxScore(exam.getTotalScore())
                .questions(questions)
                .build();
    }

    private ExamQuestionForStudentDTO convertToExamQuestionForStudentDTO(ExamQuestion examQuestion, StudentExam studentExam) {
        Question question = examQuestion.getQuestion();
        
        ExamQuestionForStudentDTO dto = ExamQuestionForStudentDTO.builder()
                .examQuestionId(examQuestion.getId())
                .questionId(question.getId())
                .title(question.getTitle())
                .questionText(question.getQuestionText())
                .questionType(question.getType())
                .score(examQuestion.getScore())
                .orderNumber(examQuestion.getOrderNumber())
                .build();

        if (question instanceof MultipleChoiceQuestion) {
            MultipleChoiceQuestion mcq = (MultipleChoiceQuestion) question;
            List<QuestionOptionForStudentDTO> options = mcq.getOptions().stream()
                    .map(this::convertToQuestionOptionForStudentDTO)
                    .collect(Collectors.toList());
            dto.setOptions(options);
        } else if (question instanceof DescriptiveQuestion) {
            DescriptiveQuestion dq = (DescriptiveQuestion) question;
            dto.setMaxWords(dq.getMaxWords());
        }

        // پاسخ قبلی (اگر وجود داشته باشد)
        StudentAnswer previousAnswer = studentAnswerRepository
                .findByStudentExamAndExamQuestion(studentExam, examQuestion)
                .orElse(null);

        if (previousAnswer != null) {
            dto.setPreviousAnswer(convertToStudentAnswerForDisplayDTO(previousAnswer));
        }

        return dto;
    }

    private QuestionOptionForStudentDTO convertToQuestionOptionForStudentDTO(QuestionOption option) {
        return QuestionOptionForStudentDTO.builder()
                .id(option.getId())
                .optionText(option.getOptionText())
                .orderIndex(option.getOrderIndex())
                .build();
    }

    private StudentAnswerForDisplayDTO convertToStudentAnswerForDisplayDTO(StudentAnswer answer) {
        StudentAnswerForDisplayDTO dto = StudentAnswerForDisplayDTO.builder()
                .answerId(answer.getId())
                .examQuestionId(answer.getExamQuestion().getId())
                .textAnswer(answer.getTextAnswer())
                .build();

        if (answer.getSelectedOption() != null) {
            dto.setSelectedOptionId(answer.getSelectedOption().getId());
        }

        return dto;
    }

    private ExamResultDTO convertToExamResultDTO(StudentExam studentExam) {
        List<StudentAnswer> answers = studentExam.getAnswers();
        long answeredCount = answers.size();
        long correctCount = answers.stream().filter(a -> Boolean.TRUE.equals(a.getIsCorrect())).count();
        long gradedCount = answers.stream().filter(StudentAnswer::getIsGraded).count();
        long ungradedCount = answeredCount - gradedCount;

        Double totalScore = studentExam.getTotalScore() != null ? studentExam.getTotalScore() : 0.0;
        Double maxScore = studentExam.getMaxScore() != null ? studentExam.getMaxScore() : 0.0;
        Double percentage = maxScore > 0 ? (totalScore / maxScore) * 100 : 0.0;

        // Convert answers to detail DTOs
        List<StudentAnswerDetailDTO> answerDetails = answers.stream()
                .map(this::convertToStudentAnswerDetailDTO)
                .collect(Collectors.toList());

        return ExamResultDTO.builder()
                .studentExamId(studentExam.getId())
                .examTitle(studentExam.getExam().getTitle())
                .startTime(studentExam.getStartTime())
                .endTime(studentExam.getEndTime())
                .durationMinutes(studentExam.getExam().getDurationMinutes())
                .totalScore(totalScore)
                .maxScore(maxScore)
                .percentage(percentage)
                .totalQuestions(studentExam.getExam().getExamQuestions().size())
                .answeredQuestions((int) answeredCount)
                .correctAnswers((int) correctCount)
                .isFullyGraded(ungradedCount == 0)
                .gradedQuestions((int) gradedCount)
                .ungradedQuestions((int) ungradedCount)
                .answerDetails(answerDetails)
                .build();
    }

    private StudentAnswerDetailDTO convertToStudentAnswerDetailDTO(StudentAnswer answer) {
        Question question = answer.getExamQuestion().getQuestion();
        
        StudentAnswerDetailDTO dto = StudentAnswerDetailDTO.builder()
                .answerId(answer.getId())
                .questionTitle(question.getTitle())
                .questionText(question.getQuestionText())
                .questionType(question.getType())
                .maxScore(answer.getExamQuestion().getScore())
                .score(answer.getScore())
                .isCorrect(answer.getIsCorrect())
                .isGraded(answer.getIsGraded())
                .build();

        // For multiple choice questions
        if (question instanceof MultipleChoiceQuestion && answer.getSelectedOption() != null) {
            dto.setSelectedOptionId(answer.getSelectedOption().getId());
            dto.setSelectedOption(answer.getSelectedOption().getOptionText());
        }

        // For descriptive questions
        if (question instanceof DescriptiveQuestion) {
            dto.setTextAnswer(answer.getTextAnswer());
            // Instructor comment can be added here if needed
        }

        return dto;
    }

    // ========== Validation Methods ==========

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("کاربر با شناسه " + userId + " یافت نشد"));
    }

    private Exam getExamById(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("آزمون با شناسه " + examId + " یافت نشد"));
    }

    private Course getCourseById(Long courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("دوره با شناسه " + courseId + " یافت نشد"));
    }

    private StudentExam getStudentExamById(Long studentExamId) {
        return studentExamRepository.findById(studentExamId)
                .orElseThrow(() -> new ResourceNotFoundException("شرکت در آزمون با شناسه " + studentExamId + " یافت نشد"));
    }

    private void validateStudentRole(User user) {
        if (user.getRole() != UserRole.STUDENT) {
            throw new UnauthorizedException("فقط دانشجویان می‌توانند از این عملیات استفاده کنند");
        }
    }

    private void validateStudentOwnership(Long studentId, StudentExam studentExam) {
        if (!studentExam.getStudent().getId().equals(studentId)) {
            throw new UnauthorizedException("شما مجاز به دسترسی به این آزمون نیستید");
        }
    }
}