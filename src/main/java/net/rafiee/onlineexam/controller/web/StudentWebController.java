package net.rafiee.onlineexam.controller.web;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
@PreAuthorize("hasRole('STUDENT')")
@RequiredArgsConstructor
public class StudentWebController {
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        return "student/dashboard";
    }
    
    @GetMapping("/courses")
    public String courses(Model model) {
        return "student/courses";
    }
}