package com.odc.hub.quiz.controller;

import com.odc.hub.quiz.dto.requests.CreateQuizRequest;
import com.odc.hub.quiz.dto.requests.SubmitQuizRequest;
import com.odc.hub.quiz.dto.responses.QuizAdminResponse;
import com.odc.hub.quiz.dto.responses.QuizAttemptResponse;
import com.odc.hub.quiz.dto.responses.QuizResponse;
import com.odc.hub.quiz.dto.responses.QuizResultResponse;
import com.odc.hub.quiz.service.QuizService;
import com.odc.hub.user.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    // ---------------- FORMATEUR ----------------
    @PostMapping
    @PreAuthorize("hasRole('FORMATEUR')")
    public ResponseEntity<QuizAdminResponse> createQuiz(
            @RequestBody CreateQuizRequest request
    ) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        String formateurId = user.getId();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(quizService.createQuiz(request, formateurId));
    }



    @GetMapping("/mine")
    @PreAuthorize("hasRole('FORMATEUR')")
    public ResponseEntity<List<QuizAdminResponse>> getMyQuizzes() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        String formateurId = user.getId();

        return ResponseEntity.ok(
                quizService.getFormateurQuizzes(formateurId)
        );
    }

    @PreAuthorize("hasRole('FORMATEUR')")
    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(
            @PathVariable String quizId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        quizService.deleteQuiz(quizId, user.getId());
        return ResponseEntity.noContent().build();
    }

    // ---------------- BOOTCAMPER ----------------
    @PreAuthorize("hasRole('BOOTCAMPER')")
    @GetMapping("/{quizId}")
    public ResponseEntity<QuizResponse> getQuizForBootcamper(
            @PathVariable String quizId
    ) {
        return ResponseEntity.ok(
                quizService.getQuizForBootcamper(quizId)
        );
    }

    @PreAuthorize("hasRole('BOOTCAMPER')")
    @PostMapping("/{quizId}/submit")
    public ResponseEntity<QuizResultResponse> submitQuiz(
            @PathVariable String quizId,
            @RequestBody SubmitQuizRequest request
    ) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        String userId = user.getId();

        return ResponseEntity.ok(
                quizService.submitQuiz(quizId, userId, request)
        );
    }

    @PreAuthorize("hasRole('BOOTCAMPER')")
    @GetMapping
    public ResponseEntity<List<QuizResponse>> getAvailableQuizzes(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                quizService.getAvailableQuizzesForBootcamper(user.getId())
        );
    }

    @PreAuthorize("hasRole('BOOTCAMPER')")
    @GetMapping("/results/me")
    public ResponseEntity<List<QuizAttemptResponse>> getMyResults() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();
        String userId = user.getId();

        return ResponseEntity.ok(
                quizService.getMyQuizResults(userId)
        );
    }

    @PreAuthorize("hasRole('FORMATEUR')")
    @GetMapping("/formateur/results")
    public ResponseEntity<List<QuizAttemptResponse>> getAllResultsForFormateur(
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                quizService.getAllAttemptsForFormateur(user.getId())
        );
    }
    
    @PreAuthorize("hasRole('FORMATEUR')")
    @PutMapping("/{quizId}")
    public ResponseEntity<QuizAdminResponse> updateQuiz(
            @PathVariable String quizId,
            @RequestBody CreateQuizRequest request,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();

        return ResponseEntity.ok(
                quizService.updateQuiz(quizId, user.getId(), request)
        );
    }
    @PreAuthorize("hasRole('FORMATEUR')")
    @GetMapping("/{quizId}/admin")
    public ResponseEntity<QuizAdminResponse> getQuizForAdmin(
            @PathVariable String quizId,
            Authentication authentication
    ) {
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(
                quizService.getQuizForAdmin(quizId, user.getId())
        );
    }


}
