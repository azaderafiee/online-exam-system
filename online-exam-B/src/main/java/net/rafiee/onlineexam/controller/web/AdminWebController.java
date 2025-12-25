package net.rafiee.onlineexam.controller.web;

import net.rafiee.onlineexam.enumuration.UserRole;
import net.rafiee.onlineexam.enumuration.UserStatus;
import net.rafiee.onlineexam.service.CourseService;
import net.rafiee.onlineexam.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminWebController {
    
    private final UserService userService;
    private final CourseService courseService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/dashboard";
    }
    
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.getAllUsers());
        return "admin/users";
    }
    
    @GetMapping("/users/pending")
    public String pendingUsers(Model model) {
        model.addAttribute("users", userService.getUsersByStatus(UserStatus.PENDING));
        return "admin/pending-users";
    }
    
    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.getAllCourses());
        return "admin/courses";
    }
    
    @GetMapping("/courses/create")
    public String createCourse() {
        return "admin/course-form";
    }
    
    @GetMapping("/courses/{id}")
    public String courseDetail(@PathVariable Long id, Model model) {
        model.addAttribute("course", courseService.getCourseById(id));
        model.addAttribute("availableStudents", userService.getUsersByRole(UserRole.STUDENT));
        model.addAttribute("availableInstructors", userService.getUsersByRole(UserRole.INSTRUCTOR));
        return "admin/course-detail";
    }
}