package com.project69.project.service;
import com.project69.project.dao.QuestionDao;
import com.project69.project.dao.QuizDao;
import com.project69.project.model.C_BASIC;
import com.project69.project.model.QuestionFeedback;
import com.project69.project.model.QuestionWrapper;
import com.project69.project.model.Quiz;
import com.project69.project.model.QuizResult;
import com.project69.project.model.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class QuizServiceImpl implements QuizService {
    private static final Logger log = LoggerFactory.getLogger(QuizServiceImpl.class);
    private final QuestionDao questionDao;
    private final QuizDao quizDao;
    public QuizServiceImpl(QuestionDao questionDao, QuizDao quizDao) {
        this.questionDao = questionDao;
        this.quizDao = quizDao;
    }
    @Override
    @Transactional
    public ResponseEntity<String> createQuiz(int numQ, String title) {
        log.info("Attempting to create a quiz: title={}, numQ={}", title, numQ);
        try {
            List<C_BASIC> questions = questionDao.findRandomQuestions(Math.max(1, numQ));
            if (questions == null || questions.isEmpty()) {
                log.warn("Could not find any questions for quiz creation.");
                return new ResponseEntity<>("No questions available to create quiz.", HttpStatus.NOT_FOUND);
            }
            Quiz quiz = new Quiz();
            quiz.setTitle(title);
            quiz.setQuestions(questions);
            quizDao.save(quiz);
            log.info("Successfully created quiz with ID: {}", quiz.getId());
            return new ResponseEntity<>("Quiz created successfully. ID: " + quiz.getId(), HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("Error creating quiz: title={}", title, e);
            return new ResponseEntity<>("Failed to create quiz due to an internal error.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<List<QuestionWrapper>> getQuizQuestions(Integer id) {
        log.info("Fetching quiz questions for ID: {}", id);
        try {
            return quizDao.findById(id)
                .map(quiz -> {
                    List<C_BASIC> questionsFromDB = Optional.ofNullable(quiz.getQuestions()).orElse(Collections.emptyList());
                    List<QuestionWrapper> wrappers = questionsFromDB.stream()
                        .filter(Objects::nonNull)
                        .map(q -> new QuestionWrapper(
                                q.getId(),
                                q.getQuestion(),
                                q.getOption1(),
                                q.getOption2(),
                                q.getOption3(),
                                q.getOption4()
                        ))
                        .collect(Collectors.toList());
                    log.debug("Found {} questions for quiz ID {}", wrappers.size(), id);
                    return new ResponseEntity<>(wrappers, HttpStatus.OK);
                })
                .orElseGet(() -> {
                    log.warn("Quiz not found with ID: {}", id);
                    return new ResponseEntity<>(Collections.emptyList(), HttpStatus.NOT_FOUND);
                });
        } catch (Exception e) {
            log.error("Error fetching quiz questions for ID: {}", id, e);
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<QuizResult> calculateResult(Integer id, List<Response> responses) {
        log.debug("calculateResult called with quizId={}, responses count={}", id, responses == null ? 0 : responses.size());
        if (responses == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            return (ResponseEntity<QuizResult>) quizDao.findById(id)
                    .map(quizObj -> {
                        Quiz quiz = quizObj;
                        List<C_BASIC> questions = Optional.ofNullable(quiz.getQuestions()).orElse(Collections.emptyList());

                        Map<Integer, C_BASIC> questionMap = questions.stream()
                                .filter(Objects::nonNull)
                                .collect(Collectors.toMap(C_BASIC::getId, q -> q));

                        for (Response userResponse : responses) {
                            if (userResponse == null || userResponse.getQuestionId() == null) {
                                log.warn("Invalid response entry in submission for quizId={}: {}", id, userResponse);
                                return ResponseEntity.badRequest().build();
                            }
                            if (!questionMap.containsKey(userResponse.getQuestionId())) {
                                log.warn("Submission contains invalid questionId={} for quizId={}", userResponse.getQuestionId(), id);
                                return ResponseEntity.badRequest().build();
                            }
                        }
                        if (responses.size() > questions.size()) {
                            log.warn("Submission for quizId={} contains more answers ({}) than quiz questions ({})",
                                    id, responses.size(), questions.size());
                            return ResponseEntity.badRequest().build();
                        }
                        int correctCount = 0;
                        int skippedCount = 0;
                        List<QuestionFeedback> feedbackList = new ArrayList<>();
                        for (Response userResponse : responses) {
                            C_BASIC question = questionMap.get(userResponse.getQuestionId());
                            if (question == null) {
                                log.debug("Skipping unknown questionId={} in submission for quizId={}", userResponse.getQuestionId(), id);
                                continue;
                            }
                            Integer userIndex = userResponse.getResponseIndex();
                            Integer correctIndex = question.getCorrectAnswerIndex();
                            boolean isSkipped = (userIndex == null);
                            boolean isCorrect = (!isSkipped) && Objects.equals(userIndex, correctIndex);
                            if (isCorrect) correctCount++;
                            if (isSkipped) skippedCount++;
                            String userAnswerText = getOptionText(question, userIndex);
                            String correctAnswerText = getOptionText(question, correctIndex);
                            feedbackList.add(QuestionFeedback.builder()
                                    .questionText(question.getQuestion())
                                    .userResponse(userAnswerText)
                                    .isCorrect(isCorrect)
                                    .correctAnswer(correctAnswerText)
                                    .build());
                        }
                        int totalQuestions = questions.size();
                        int incorrectCount = Math.max(0, totalQuestions - correctCount - skippedCount);
                        double scorePercentage = totalQuestions > 0 ? ((double) correctCount / totalQuestions) * 100.0 : 0.0;
                        boolean passed = scorePercentage >= 70.0;
                        QuizResult result = QuizResult.builder()
                                .quizTitle(quiz.getTitle())
                                .totalQuestions(totalQuestions)
                                .correctCount(correctCount)
                                .incorrectCount(incorrectCount)
                                .skippedCount(skippedCount)
                                .scorePercentage(scorePercentage)
                                .passed(passed)
                                .feedbackList(feedbackList)
                                .build();
                        log.debug("Calculated quiz result for id={}: correct={}, skipped={}, score={}",
                                id, correctCount, skippedCount, scorePercentage);
                        return new ResponseEntity<>(result, HttpStatus.OK);
                    })
                    .orElseGet(() -> {
                        log.debug("Quiz not found with id={}", id);
                        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
                    });
        } catch (Exception e) {
            log.error("Failed to calculate result for quizId={}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    private String getOptionText(C_BASIC q, Integer index) {
        if (index == null) return "No answer provided";
        return switch (index) {
            case 0 -> q.getOption1();
            case 1 -> q.getOption2();
            case 2 -> q.getOption3();
            case 3 -> q.getOption4();
            default -> "Invalid option index";
        };
    }
}
