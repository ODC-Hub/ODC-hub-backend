package com.odc.hub.quiz.service;

import com.odc.hub.quiz.dto.requests.CreateQuizRequest;
import com.odc.hub.quiz.dto.requests.QuestionRequest;
import com.odc.hub.quiz.dto.requests.SubmitQuizRequest;
import com.odc.hub.quiz.dto.responses.*;
import com.odc.hub.quiz.model.*;
import com.odc.hub.quiz.repository.QuizAttemptRepository;
import com.odc.hub.quiz.repository.QuizRepository;
import com.odc.hub.user.model.User;
import com.odc.hub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.odc.hub.quiz.model.Option;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final QuizAttemptRepository attemptRepository;
    private final UserRepository userRepository;

    @Override
    public QuizAdminResponse createQuiz(CreateQuizRequest request, String formateurId) {

        validateCreateQuizRequest(request);

        QuizDocument quiz = QuizDocument.builder()
                .title(request.getTitle())
                .module(request.getModule())
                .durationSeconds(request.getDurationSeconds())
                .passingScore(request.getPassingScore())
                .createdBy(formateurId)
                .createdAt(Instant.now())
                .questions(mapQuestions(request.getQuestions()))
                .build();

        quizRepository.save(quiz);
        return mapToAdminResponse(quiz);
    }

    @Override
    public List<QuizAdminResponse> getFormateurQuizzes(String formateurId) {
        return quizRepository.findByCreatedBy(formateurId)
                .stream()
                .map(this::mapToAdminResponse)
                .toList();
    }


    @Override
    public QuizResponse getQuizForBootcamper(String quizId) {
        QuizDocument quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        return mapToSafeResponse(quiz);
    }


    @Override
    public QuizResultResponse submitQuiz(
            String quizId,
            String userId,
            SubmitQuizRequest request
    ) {
        attemptRepository.findByQuizIdAndUserId(quizId, userId)
                .ifPresent(a -> {
                    throw new ResponseStatusException(
                            HttpStatus.CONFLICT,
                            "Quiz already submitted"
                    );
                });

        QuizDocument quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new IllegalArgumentException("Quiz not found"));

        int score = calculateScore(quiz, request.getAnswers());
        int total = quiz.getQuestions().size();
        int percentage = (int) Math.round((score * 100.0) / total);
        boolean passed = percentage >= quiz.getPassingScore();

        QuizAttemptDocument attempt = QuizAttemptDocument.builder()
                .quizId(quizId)
                .userId(userId)
                .score(score)
                .totalQuestions(total)
                .percentage(percentage)
                .passed(passed)
                .timeTakenSeconds(request.getTimeTakenSeconds())
                .submittedAt(Instant.now())
                .build();

        attemptRepository.save(attempt);

        return QuizResultResponse.builder()
                .score(score)
                .totalQuestions(total)
                .percentage(percentage)
                .passed(passed)
                .build();
    }


    private int calculateScore(
            QuizDocument quiz,
            Map<String, List<String>> answers
    ) {
        int score = 0;

        for (Question question : quiz.getQuestions()) {
            List<String> userAnswer =
                    answers.getOrDefault(question.getId(), List.of());

            if (isCorrectAnswer(question, userAnswer)) {
                score++;
            }
        }
        return score;
    }

    private boolean isCorrectAnswer(
            Question question,
            List<String> userAnswer
    ) {
        if (userAnswer.size() != question.getCorrectOptionIds().size()) {
            return false;
        }
        return new HashSet<>(userAnswer)
                .equals(new HashSet<>(question.getCorrectOptionIds()));
    }


    private void validateCreateQuizRequest(CreateQuizRequest request) {
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new IllegalArgumentException("Quiz must contain at least one question");
        }

        if (request.getPassingScore() < 0 || request.getPassingScore() > 100) {
            throw new IllegalArgumentException("Passing score must be between 0 and 100");
        }

        for (QuestionRequest q : request.getQuestions()) {
            if (q.getCorrectOptionIds() == null || q.getCorrectOptionIds().isEmpty()) {
                throw new IllegalArgumentException("Each question must have a correct answer");
            }
            if (q.getType() == QuestionType.SINGLE && q.getCorrectOptionIds().size() != 1) {
                throw new IllegalArgumentException("Single choice question must have exactly one correct answer");
            }
        }
    }


    private List<Question> mapQuestions(List<QuestionRequest> requests) {
        return requests.stream()
                .map(q -> Question.builder()
                        .id(UUID.randomUUID().toString())
                        .type(q.getType())
                        .text(q.getText())
                        .options(
                                q.getOptions().stream()
                                        .map(o -> Option.builder()
                                                .id(o.getId())
                                                .text(o.getText())
                                                .build()
                                        )
                                        .collect(Collectors.toList())
                        )
                        .correctOptionIds(q.getCorrectOptionIds())
                        .build()
                )
                .collect(Collectors.toList());
    }


    private QuizAdminResponse mapToAdminResponse(QuizDocument quiz) {
        boolean hasAttempts = attemptRepository.existsByQuizId(quiz.getId());
        return QuizAdminResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .module(quiz.getModule())
                .durationSeconds(quiz.getDurationSeconds())
                .passingScore(quiz.getPassingScore())
                .createdAt(quiz.getCreatedAt())
                .editable(!hasAttempts)
                .questions(
                        quiz.getQuestions().stream()
                                .map(q -> QuestionAdminResponse.builder()
                                        .id(q.getId())
                                        .type(q.getType())
                                        .text(q.getText())
                                        .options(
                                                q.getOptions().stream()
                                                        .map(o -> OptionResponse.builder()
                                                                .id(o.getId())
                                                                .text(o.getText())
                                                                .build())
                                                        .collect(Collectors.toList())
                                        )
                                        .correctOptionIds(q.getCorrectOptionIds())
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }


    private QuizResponse mapToSafeResponse(QuizDocument quiz) {
        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .module(quiz.getModule())
                .durationSeconds(quiz.getDurationSeconds())
                .passingScore(quiz.getPassingScore())
                .questions(
                        quiz.getQuestions().stream()
                                .map(q -> QuestionResponse.builder()
                                        .id(q.getId())
                                        .type(q.getType())
                                        .text(q.getText())
                                        .options(
                                                q.getOptions().stream()
                                                        .map(o -> OptionResponse.builder()
                                                                .id(o.getId())
                                                                .text(o.getText())
                                                                .build())
                                                        .collect(Collectors.toList())
                                        )
                                        .build())
                                .collect(Collectors.toList())
                )
                .build();
    }
    @Override
    public List<QuizResponse> getAvailableQuizzesForBootcamper(String userId) {

        // 1. Get quizzes already attempted by this user
        Set<String> attemptedQuizIds = attemptRepository
                .findByUserId(userId)
                .stream()
                .map(QuizAttemptDocument::getQuizId)
                .collect(Collectors.toSet());

        // 2. Fetch all quizzes and exclude attempted ones
        return quizRepository.findAll()
                .stream()
                .filter(q -> !attemptedQuizIds.contains(q.getId()))
                .map(this::mapToSafeResponse)
                .toList();
    }

    @Override
    public List<QuizAttemptResponse> getMyQuizResults(String userId) {

        return attemptRepository.findByUserId(userId)
                .stream()
                .map(attempt -> {
                    QuizDocument quiz = quizRepository.findById(attempt.getQuizId())
                            .orElse(null);

                    return QuizAttemptResponse.builder()
                            .quizId(attempt.getQuizId())
                            .quizTitle(quiz != null ? quiz.getTitle() : "Deleted quiz")
                            .module(quiz != null ? quiz.getModule() : "-")
                            .score(attempt.getScore())
                            .totalQuestions(attempt.getTotalQuestions())
                            .percentage(attempt.getPercentage())
                            .passed(attempt.isPassed())
                            .timeTakenSeconds(attempt.getTimeTakenSeconds())
                            .submittedAt(attempt.getSubmittedAt())
                            .build();
                })
                .toList();
    }

    @Override
    public List<QuizAttemptResponse> getAllAttemptsForFormateur(String formateurId) {

        // 1. Get quizzes created by formateur
        List<QuizDocument> quizzes =
                quizRepository.findByCreatedBy(formateurId);

        if (quizzes.isEmpty()) return List.of();

        Map<String, QuizDocument> quizMap = quizzes.stream()
                .collect(Collectors.toMap(QuizDocument::getId, q -> q));

        // 2. Get attempts for these quizzes
        List<QuizAttemptDocument> attempts =
                attemptRepository.findByQuizIdIn(quizMap.keySet().stream().toList());

        // 3. Map attempts
        return attempts.stream().map(attempt -> {
            QuizDocument quiz = quizMap.get(attempt.getQuizId());
            User bootcamper = userRepository
                    .findById(attempt.getUserId())
                    .orElse(null);

            return QuizAttemptResponse.builder()
                    .quizId(quiz.getId())
                    .quizTitle(quiz.getTitle())
                    .module(quiz.getModule())
                    .bootcamperId(attempt.getUserId())
                    .bootcamperName(
                            bootcamper != null
                                    ? bootcamper.getFullName()
                                    : "Unknown user"
                    )                    .score(attempt.getScore())
                    .totalQuestions(attempt.getTotalQuestions())
                    .percentage(attempt.getPercentage())
                    .passed(attempt.isPassed())
                    .timeTakenSeconds(attempt.getTimeTakenSeconds())
                    .submittedAt(attempt.getSubmittedAt())
                    .build();
        }).toList();
    }

    @Override
    public void deleteQuiz(String quizId, String formateurId) {

        QuizDocument quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        // 🔒 Ownership check
        if (!quiz.getCreatedBy().equals(formateurId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to delete this quiz"
            );
        }

        attemptRepository.deleteAll(
                attemptRepository.findByQuizId(quizId)
        );

        quizRepository.deleteById(quizId);
    }

    @Override
    public QuizAdminResponse getQuizForEdit(String quizId, String formateurId) {

        QuizDocument quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        // 🔒 Ownership check
        if (!quiz.getCreatedBy().equals(formateurId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to access this quiz"
            );
        }

        return mapToAdminResponse(quiz);
    }

    @Override
    public QuizAdminResponse updateQuiz(
            String quizId,
            String formateurId,
            CreateQuizRequest request
    ) {

        QuizDocument quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Quiz not found"
                ));

        // Ownership check
        if (!quiz.getCreatedBy().equals(formateurId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "You are not allowed to edit this quiz"
            );
        }

        // BLOCK edit if already attempted
        if (!attemptRepository.findByQuizId(quizId).isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot edit quiz after submissions"
            );
        }

        // Validate again
        validateCreateQuizRequest(request);

        // Update fields
        quiz.setTitle(request.getTitle());
        quiz.setModule(request.getModule());
        quiz.setDurationSeconds(request.getDurationSeconds());
        quiz.setPassingScore(request.getPassingScore());
        quiz.setQuestions(mapQuestions(request.getQuestions()));

        quizRepository.save(quiz);

        return mapToAdminResponse(quiz);
    }

    @Override
    public QuizAdminResponse getQuizForAdmin(String quizId, String formateurId) {

        QuizDocument quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Quiz not found"
                ));

        if (!quiz.getCreatedBy().equals(formateurId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN, "Not your quiz"
            );
        }

        return mapToAdminResponse(quiz);
    }


}
