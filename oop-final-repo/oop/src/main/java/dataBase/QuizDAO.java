package dataBase;

import objects.Quiz;
import objects.Review;
import objects.questions.Question;

import java.util.List;

public interface QuizDAO {
    public int addQuiz(Quiz quiz);
    public boolean removeQuiz(int id);
    public List<Quiz> getQuizzesByAuthor(int author);
    public List<Quiz> getQuizByQuizName(String name);
    public Quiz getQuizByID(int id);
    public List<Quiz> getPopularQuizzes(int top);
    public List<Question> getQuestions(int id);
    public List<Quiz> recentlyCreatedQuizzes(int num);
    public List<String> getTags(int id);
    public List<Review> getReviews(int id);
    public Double getRating(int id);
    public List<Quiz> getTopRatedQuizzes(int top);
    public String getCategory(int id);
    public List<Quiz> getQuizzesByTag(String tag);
    public void addTag(int id, String tag);
    public void removeTag(int id, String tag);
    List<Quiz> getAllQuizzes();
}
