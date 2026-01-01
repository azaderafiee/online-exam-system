package net.rafiee.onlineexam.service;

import net.rafiee.onlineexam.dto.AddQuestionToExamDTO;
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
    
    // Question management methods
    void addQuestionToExam(Long examId, AddQuestionToExamDTO dto, Long instructorId);
    
    void addMultipleQuestionsToExam(Long examId, List<AddQuestionToExamDTO> questions, Long instructorId);
    
    void removeQuestionFromExam(Long examId, Long questionId, Long instructorId);
    
    void updateQuestionScore(Long examId, Long questionId, Double newScore, Long instructorId);
    
    List<net.rafiee.onlineexam.dto.ExamQuestionDTO> getExamQuestions(Long examId, Long instructorId);
}