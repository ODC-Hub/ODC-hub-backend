package com.odc.hub.quiz.service;

import com.odc.hub.quiz.dto.requests.CreateQuizRequest;
import com.odc.hub.quiz.dto.requests.SubmitQuizRequest;
import com.odc.hub.quiz.dto.responses.QuizAdminResponse;
import com.odc.hub.quiz.dto.responses.QuizAttemptResponse;
import com.odc.hub.quiz.dto.responses.QuizResponse;
import com.odc.hub.quiz.dto.responses.QuizResultResponse;

import java.util.List;

public interface QuizService {

    QuizAdminResponse createQuiz(CreateQuizRequest request, String formateurId);

    List<QuizAdminResponse> getFormateurQuizzes(String formateurId);

    QuizResponse getQuizForBootcamper(String quizId);

    QuizResultResponse submitQuiz(
            String quizId,
            String userId,
            SubmitQuizRequest request
    );

    List<QuizResponse> getAvailableQuizzesForBootcamper(String userId);

    List<QuizAttemptResponse> getMyQuizResults(String userId);

    List<QuizAttemptResponse> getAllAttemptsForFormateur(String formateurId);

    void deleteQuiz(String quizId, String formateurId);

    QuizAdminResponse getQuizForEdit(String quizId, String formateurId);

    QuizAdminResponse updateQuiz(
            String quizId,
            String formateurId,
            CreateQuizRequest request
    );

    QuizAdminResponse getQuizForAdmin(String quizId, String formateurId);


}
