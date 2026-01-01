package net.rafiee.onlineexam.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.entity.DescriptiveQuestion;
import net.rafiee.onlineexam.entity.MultipleChoiceQuestion;
import net.rafiee.onlineexam.entity.Question;
import net.rafiee.onlineexam.enumuration.QuestionType;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.CourseService;
import net.rafiee.onlineexam.service.ExamQuestionService;
import net.rafiee.onlineexam.service.ExamService;
import net.rafiee.onlineexam.service.QuestionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
@RequiredArgsConstructor
@Slf4j
public class InstructorWebController {

    private final CourseService courseService;
    private final ExamService examService;
    private final UserRepository userRepository;
    private final QuestionService questionService;
    private final ExamQuestionService examQuestionService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("Loading instructor dashboard");
        Long instructorId = getCurrentUserId();

        model.addAttribute("courses", courseService.getCoursesByInstructor(instructorId));
        model.addAttribute("exams", examService.getExamsByInstructor(instructorId));

        return "instructor/dashboard";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        log.info("Loading instructor courses page");
        Long instructorId = getCurrentUserId();

        model.addAttribute("courses", courseService.getCoursesByInstructor(instructorId));

        return "instructor/courses";
    }

    @GetMapping("/courses/{courseId}/exams")
    public String courseExams(@PathVariable Long courseId, Model model) {
        log.info("Loading exams for course {}", courseId);
        Long instructorId = getCurrentUserId();

        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("exams", examService.getExamsByCourseAndInstructor(courseId, instructorId));
        model.addAttribute("courseId", courseId);

        return "instructor/course-exams";
    }

    @GetMapping("/exams")
    public String allExams(Model model) {
        log.info("Loading all instructor exams");
        Long instructorId = getCurrentUserId();

        model.addAttribute("exams", examService.getExamsByInstructor(instructorId));

        return "instructor/exams";
    }

    // ==================== فاز C: مدیریت سوالات ====================

    /**
     * صفحه بانک سوالات یک دوره
     */
    @GetMapping("/courses/{courseId}/questions")
    public String courseQuestionBank(
            @PathVariable Long courseId,
            @RequestParam(required = false) QuestionType type,
            @RequestParam(required = false) String keyword,
            Model model) {

        log.info("Loading question bank for course {}", courseId);
        Long instructorId = getCurrentUserId();

        model.addAttribute("course", courseService.getCourseById(courseId));

        // دریافت سوالات بر اساس فیلتر
        if (keyword != null && !keyword.isEmpty()) {
            model.addAttribute("questions", questionService.searchQuestionsInCourse(courseId, keyword));
        } else if (type != null) {
            model.addAttribute("questions", questionService.getQuestionsByCourseAndType(courseId, type));
        } else {
            model.addAttribute("questions", questionService.getQuestionsByCourse(courseId));
        }

        model.addAttribute("selectedType", type);
        model.addAttribute("keyword", keyword);
        model.addAttribute("questionTypes", QuestionType.values());

        // آمار
        List<Question> byCourseId = questionService.getByCourseId(courseId);
        long multipleChoiceQuestionCount = byCourseId.stream().filter(i -> i instanceof MultipleChoiceQuestion).count();
        long descriptiveQuestionCount = byCourseId.stream().filter(i -> i instanceof DescriptiveQuestion).count();

        model.addAttribute("totalQuestions", questionService.countQuestionsByCourse(courseId));
        model.addAttribute("multipleChoiceCount", multipleChoiceQuestionCount);
        model.addAttribute("descriptiveCount", descriptiveQuestionCount);

        return "instructor/question-bank";
    }

    /**
     * فرم ایجاد سوال جدید
     */
    @GetMapping("/courses/{courseId}/questions/new")
    public String newQuestionForm(@PathVariable Long courseId, Model model) {
        log.info("Loading new question form for course {}", courseId);

        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("questionTypes", QuestionType.values());

        return "instructor/question-form";
    }

    /**
     * صفحه ویرایش سوال
     */
    @GetMapping("/questions/{questionId}/edit")
    public String editQuestionForm(@PathVariable Long questionId, Model model) {
        log.info("Loading edit question form for question {}", questionId);

        var question = questionService.getQuestionById(questionId);
        model.addAttribute("question", question);
        model.addAttribute("questionTypes", QuestionType.values());

        // اضافه کردن اطلاعات دوره
        var course = courseService.getCourseById(question.getCourseId());
        model.addAttribute("course", course);

        return "instructor/question-edit";
    }

    /**
     * صفحه مدیریت سوالات آزمون
     */
    @GetMapping("/exams/{examId}/questions-manager")
    public String examQuestions(@PathVariable Long examId, Model model) {
        log.info("Loading questions management for exam {}", examId);
        var examWithQuestions = examQuestionService.getExamWithQuestions(examId);
        model.addAttribute("exam", examWithQuestions);
        model.addAttribute("questions", examWithQuestions.getQuestions());
        model.addAttribute("totalScore", examWithQuestions.getTotalScore());

        // بانک سوالات برای modal
        var courseId = examWithQuestions.getCourseId();
        var courseCode = examWithQuestions.getCourseCode();
        model.addAttribute("courseId", courseId);
        model.addAttribute("courseCode", courseCode);
        model.addAttribute("availableQuestions", questionService.getQuestionsByCourse(courseId));

        return "instructor/exam-questions";
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"))
                .getId();
    }
}