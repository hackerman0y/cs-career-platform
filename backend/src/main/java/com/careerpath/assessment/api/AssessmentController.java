package com.careerpath.assessment.api;

import com.careerpath.assessment.service.AssessmentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.security.Principal;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/assessment")
public class AssessmentController {
    private final AssessmentService assessmentService;

    public AssessmentController(AssessmentService assessmentService) {
        this.assessmentService = assessmentService;
    }

    @GetMapping("/questions")
    public List<QuestionResponse> getQuestions() {
        return assessmentService.getQuestions();
    }

    @PostMapping("/submit")
    public AssessmentResultResponse submit(Principal principal, @Valid @RequestBody SubmitAssessmentRequest request) {
        return assessmentService.submit(principal.getName(), request.answers());
    }

    @GetMapping("/result")
    public AssessmentResultResponse result(Principal principal) {
        return assessmentService.getLatestResult(principal.getName());
    }

    public record QuestionResponse(UUID id, String prompt, List<ChoiceResponse> choices) {
    }

    public record ChoiceResponse(String value, String label) {
    }

    public record AnswerInput(@NotNull UUID questionId, @NotNull String answer) {
    }

    public record SubmitAssessmentRequest(@NotEmpty List<@Valid AnswerInput> answers) {
    }

    public record CareerRecommendation(String name, String description, int score) {
    }

    public record AssessmentResultResponse(List<CareerRecommendation> recommendations) {
    }
}
