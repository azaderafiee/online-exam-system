package net.rafiee.onlineexam.service;

import net.rafiee.onlineexam.dto.DescriptiveQuestionDTO;
import net.rafiee.onlineexam.dto.MultipleChoiceQuestionDTO;
import net.rafiee.onlineexam.dto.QuestionResponseDTO;
import net.rafiee.onlineexam.entity.Question;
import net.rafiee.onlineexam.enumuration.QuestionType;

import java.util.List;

public interface QuestionService {
    
    // ایجاد سوالات
    QuestionResponseDTO createMultipleChoiceQuestion(MultipleChoiceQuestionDTO questionDTO, String username);
    
    QuestionResponseDTO createDescriptiveQuestion(DescriptiveQuestionDTO questionDTO, String username);
    
    // دریافت سوالات
    QuestionResponseDTO getQuestionById(Long id);
    
    List<QuestionResponseDTO> getQuestionsByCourse(Long courseId);

    List<Question> getByCourseId(Long courseId);

    List<QuestionResponseDTO> getQuestionsByCourseAndType(Long courseId, QuestionType type);
    
    List<QuestionResponseDTO> getQuestionsByInstructor( String username, Long courseId);
    
    List<QuestionResponseDTO> searchQuestionsInCourse(Long courseId, String keyword);
    
    // ویرایش سوالات
    QuestionResponseDTO updateMultipleChoiceQuestion(Long id, MultipleChoiceQuestionDTO questionDTO, String username);
    
    QuestionResponseDTO updateDescriptiveQuestion(Long id, DescriptiveQuestionDTO questionDTO, String username);
    
    // حذف سوال
    void deleteQuestion(Long id, String username);
    
    // آمار بانک سوالات
    Long countQuestionsByCourse(Long courseId);
    
    Long countQuestionsByCourseAndType(Long courseId, QuestionType type);
}
