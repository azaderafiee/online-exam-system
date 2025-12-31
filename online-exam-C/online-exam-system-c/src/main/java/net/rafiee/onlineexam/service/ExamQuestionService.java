package net.rafiee.onlineexam.service;

import net.rafiee.onlineexam.dto.*;

import java.util.List;

public interface ExamQuestionService {
    
    // اضافه کردن سوال به آزمون
    ExamQuestionResponseDTO addQuestionToExam(Long examId, AddQuestionToExamDTO dto, String username);
    
    // اضافه کردن چند سوال به آزمون
    List<ExamQuestionResponseDTO> addMultipleQuestionsToExam(Long examId, AddMultipleQuestionsToExamDTO dto, String username);
    
    // دریافت سوالات آزمون
    List<ExamQuestionResponseDTO> getExamQuestions(Long examId);
    
    ExamWithQuestionsResponseDTO getExamWithQuestions(Long examId);
    
    // ویرایش نمره سوال در آزمون
    ExamQuestionResponseDTO updateQuestionScore(Long examId, Long questionId, UpdateQuestionScoreDTO dto, String username);
    
    // حذف سوال از آزمون
    void removeQuestionFromExam(Long examId, Long questionId, String username);
    
    // تغییر ترتیب سوالات
    void reorderQuestions(Long examId, List<Long> questionIds, String username);
    
    // محاسبه مجموع نمرات آزمون
    Double calculateExamTotalScore(Long examId);
}