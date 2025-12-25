package net.rafiee.onlineexam.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.CourseService;
import net.rafiee.onlineexam.service.ExamService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
@RequiredArgsConstructor
@Slf4j
public class InstructorWebController {

    private final CourseService courseService;
    private final ExamService examService;
    private final UserRepository userRepository;

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

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"))
                .getId();
    }
}