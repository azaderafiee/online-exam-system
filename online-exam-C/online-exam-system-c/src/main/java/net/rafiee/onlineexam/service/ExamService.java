package net.rafiee.onlineexam.service;


import net.rafiee.onlineexam.dto.ExamDTO;
import net.rafiee.onlineexam.dto.ExamResponseDTO;
import net.rafiee.onlineexam.dto.ExamUpdateDTO;

import java.util.List;

public interface ExamService {
    
    ExamResponseDTO createExam(ExamDTO examDTO, Long instructorId);
    
    ExamResponseDTO getExamById(Long id);
    
    List<ExamResponseDTO> getAllExams();
    
    List<ExamResponseDTO> getExamsByCourse(Long courseId);
    
    List<ExamResponseDTO> getExamsByInstructor(Long instructorId);
    
    List<ExamResponseDTO> getExamsByCourseAndInstructor(Long courseId, Long instructorId);
    
    ExamResponseDTO updateExam(Long id, ExamUpdateDTO updateDTO, Long instructorId);
    
    void deleteExam(Long id, Long instructorId);
    
    Long countExamsByCourse(Long courseId);
}