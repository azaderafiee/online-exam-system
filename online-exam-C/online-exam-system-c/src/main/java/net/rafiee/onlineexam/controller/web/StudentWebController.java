package net.rafiee.onlineexam.controller.web;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.rafiee.onlineexam.repository.UserRepository;
import net.rafiee.onlineexam.service.CourseService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
@Slf4j
public class StudentWebController {

    private final CourseService courseService;
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

    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("کاربر یافت نشد"))
                .getId();
    }
}