package net.rafiee.onlineexam.controller.web;

import net.rafiee.onlineexam.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/instructor")
@PreAuthorize("hasRole('INSTRUCTOR')")
@RequiredArgsConstructor
public class InstructorWebController {

    private final CourseService courseService;

    @GetMapping("/dashboard")
    public String  dashboard(Model model) {
        return "instructor/dashboard";
    }

    @GetMapping("/courses")
    public String courses(Model model) {
        return "instructor/courses";
    }
}