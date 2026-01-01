package net.rafiee.onlineexam.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.dto.ExamForStudentDTO;
import net.rafiee.onlineexam.dto.ExamResultDTO;
import net.rafiee.onlineexam.dto.StartExamResponseDTO;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.CourseService;
import net.rafiee.onlineexam.service.StudentExamService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
@Slf4j
public class StudentWebController {

    private final CourseService courseService;
    private final StudentExamService studentExamService;
    private final UserRepository userRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        log.info("Loading student dashboard");
        Long studentId = getCurrentUserId();

        model.addAttribute("courses", courseService.getCoursesByStudent(studentId));

        return "student/dashboard";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        log.info("Loading student courses page");
        Long studentId = getCurrentUserId();

        model.addAttribute("courses", courseService.getCoursesByStudent(studentId));

        return "student/courses";
    }

    /**
     * صفحه لیست آزمون‌های یک دوره
     */
    @GetMapping("/courses/{courseId}/exams")
    public String courseExams(@PathVariable Long courseId, Model model) {
        log.info("Loading exams for course {}", courseId);
        Long studentId = getCurrentUserId();

        try {
            List<ExamForStudentDTO> exams = studentExamService.getCourseExamsForStudent(studentId, courseId);
            model.addAttribute("exams", exams);
            model.addAttribute("courseId", courseId);
            
            return "student/exams";
        } catch (Exception e) {
            log.error("Error loading course exams: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "redirect:/student/courses";
        }
    }

    /**
     * صفحه شرکت در آزمون
     */
    @GetMapping("/take-exam/{studentExamId}")
    public String takeExam(@PathVariable Long studentExamId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Loading exam page for student exam {}", studentExamId);
        Long studentId = getCurrentUserId();

        try {
            StartExamResponseDTO examData = studentExamService.getExamProgress(studentId, studentExamId);
            model.addAttribute("examData", examData);
            
            return "student/take-exam";
        } catch (Exception e) {
            log.error("Error loading exam: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/student/courses";
        }
    }

    /**
     * صفحه نتیجه آزمون
     */
    @GetMapping("/exam-result/{studentExamId}")
    public String examResult(@PathVariable Long studentExamId, Model model, RedirectAttributes redirectAttributes) {
        log.info("Loading exam result for student exam {}", studentExamId);
        Long studentId = getCurrentUserId();

        try {
            ExamResultDTO result = studentExamService.getExamResult(studentId, studentExamId);
            model.addAttribute("result", result);
            
            return "student/exam-result";
        } catch (Exception e) {
            log.error("Error loading exam result: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/student/courses";
        }
    }

    /**
     * صفحه لیست آزمون‌های تکمیل شده
     */
    @GetMapping("/completed-exams")
    public String completedExams(Model model) {
        log.info("Loading completed exams");
        Long studentId = getCurrentUserId();

        List<ExamResultDTO> results = studentExamService.getCompletedExams(studentId);
        model.addAttribute("results", results);

        return "student/completed-exams";
    }

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"))
                .getId();
    }
}