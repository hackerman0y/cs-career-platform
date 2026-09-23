package com.careerpath.assessment.service;

import com.careerpath.assessment.api.AssessmentController.AnswerInput;
import com.careerpath.assessment.api.AssessmentController.AssessmentResultResponse;
import com.careerpath.assessment.api.AssessmentController.CareerRecommendation;
import com.careerpath.assessment.api.AssessmentController.ChoiceResponse;
import com.careerpath.assessment.api.AssessmentController.QuestionResponse;
import com.careerpath.assessment.domain.AssessmentAnswer;
import com.careerpath.assessment.domain.AssessmentAnswerRepository;
import com.careerpath.assessment.domain.AssessmentQuestion;
import com.careerpath.assessment.domain.AssessmentQuestionRepository;
import com.careerpath.auth.domain.AppUser;
import com.careerpath.auth.domain.AppUserRepository;
import com.careerpath.career.domain.CareerPath;
import com.careerpath.career.domain.CareerPathRepository;
import jakarta.transaction.Transactional;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AssessmentService {
    private static final Map<String, List<ChoiceResponse>> CHOICES = Map.of(
            "build", List.of(choice("frontend", "Create polished web interfaces"), choice("backend", "Design APIs and systems"), choice("mobile", "Build apps for phones"), choice("data", "Find insights in data"), choice("ai", "Build intelligent products with data"), choice("security", "Protect systems and investigate risks"), choice("cloud", "Automate delivery and cloud infrastructure"), choice("fullstack", "Build a complete product end to end")),
            "project", List.of(choice("frontend", "A portfolio website or product interface"), choice("backend", "A service with users and a database"), choice("mobile", "A mobile app for everyday tasks"), choice("data", "A dashboard that explains a real problem"), choice("ai", "A model that solves a real prediction problem"), choice("security", "A documented legal security lab or defensive tool"), choice("cloud", "A deployed application with a CI/CD pipeline"), choice("fullstack", "A complete app with users, APIs, and deployment")),
            "strength", List.of(choice("frontend", "Making experiences clear and easy to use"), choice("backend", "Organizing complex logic and solving technical problems"), choice("mobile", "Turning ideas into useful experiences on the go"), choice("data", "Spotting patterns and explaining what they mean"), choice("ai", "Experimenting with data and improving models"), choice("security", "Finding weaknesses and thinking about risk"), choice("cloud", "Making systems reliable and repeatable"), choice("fullstack", "Connecting many parts into one useful product")));

    private static final Map<String, String> CAREER_FOR_ANSWER = Map.of(
            "frontend", "Frontend Developer", "backend", "Backend Developer", "mobile", "Mobile Developer", "data", "Data Analyst",
            "ai", "AI / Machine Learning", "security", "Cybersecurity", "cloud", "DevOps / Cloud", "fullstack", "Full Stack Developer");

    private final AssessmentQuestionRepository questionRepository;
    private final AssessmentAnswerRepository answerRepository;
    private final AppUserRepository userRepository;
    private final CareerPathRepository careerPathRepository;

    public AssessmentService(AssessmentQuestionRepository questionRepository, AssessmentAnswerRepository answerRepository,
            AppUserRepository userRepository, CareerPathRepository careerPathRepository) {
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
        this.userRepository = userRepository;
        this.careerPathRepository = careerPathRepository;
    }

    public List<QuestionResponse> getQuestions() {
        return questionRepository.findAllByOrderByQuestionKeyAsc().stream()
                .map(question -> new QuestionResponse(question.getId(), question.getPrompt(), CHOICES.get(question.getQuestionKey())))
                .toList();
    }

    @Transactional
    public AssessmentResultResponse submit(String email, List<AnswerInput> inputs) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Map<UUID, AssessmentQuestion> questions = questionRepository.findAllById(inputs.stream().map(AnswerInput::questionId).toList())
                .stream().collect(Collectors.toMap(AssessmentQuestion::getId, question -> question));
        if (questions.size() != inputs.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more assessment questions are invalid.");
        }

        Map<UUID, AssessmentAnswer> answersByQuestionId = answerRepository.findByUser(user).stream()
                .collect(Collectors.toMap(answer -> answer.getQuestion().getId(), answer -> answer));
        Map<String, Integer> scores = new LinkedHashMap<>();
        for (AnswerInput input : inputs) {
            AssessmentQuestion question = questions.get(input.questionId());
            boolean allowed = CHOICES.get(question.getQuestionKey()).stream().anyMatch(choice -> choice.value().equals(input.answer()));
            if (!allowed) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "One or more assessment answers are invalid.");
            }
            AssessmentAnswer savedAnswer = answersByQuestionId.get(question.getId());
            if (savedAnswer == null) {
                answerRepository.save(new AssessmentAnswer(user, question, input.answer()));
            } else {
                savedAnswer.updateAnswer(input.answer());
            }
            String career = CAREER_FOR_ANSWER.get(input.answer());
            scores.merge(career, 1, Integer::sum);
        }

        return resultForScores(scores);
    }

    public AssessmentResultResponse getLatestResult(String email) {
        AppUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        Map<String, Integer> scores = new LinkedHashMap<>();
        answerRepository.findByUser(user).forEach(answer ->
                scores.merge(CAREER_FOR_ANSWER.get(answer.getAnswerValue()), 1, Integer::sum));
        return resultForScores(scores);
    }

    private AssessmentResultResponse resultForScores(Map<String, Integer> scores) {
        Map<String, CareerPath> paths = careerPathRepository.findAll().stream()
                .collect(Collectors.toMap(CareerPath::getName, path -> path));
        List<CareerRecommendation> recommendations = scores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue(Comparator.reverseOrder()).thenComparing(Map.Entry::getKey))
                .map(entry -> {
                    CareerPath path = paths.get(entry.getKey());
                    return new CareerRecommendation(path.getName(), path.getDescription(), entry.getValue());
                })
                .toList();
        return new AssessmentResultResponse(recommendations);
    }

    private static ChoiceResponse choice(String value, String label) {
        return new ChoiceResponse(value, label);
    }
}
