package quizDAOTests;

import dataBase.ConnectionPool;
import dataBase.DbQuizDAO;
import dataBase.questionsDAOs.QuestionsDAO;
import junit.framework.TestCase;
import objects.Quiz;
import objects.Review;
import objects.questions.Matching;
import objects.questions.MultiAnswer;
import objects.questions.Question;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.BeforeEach;


import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class QuizDAOTest extends TestCase {
    private Connection connection;
    private ConnectionPool pool = new ConnectionPool(5,"test_quiz","123456789");
    private Statement statement;
    private DbQuizDAO quizDAO;

    public void beforeEach() throws SQLException {
        quizDAO = new DbQuizDAO(pool);
        connection = pool.getConnection();
        statement = connection.createStatement();
    }

    public void clearQuizzes() throws SQLException {
        statement.execute("DELETE FROM quizzes");
    }

    public void addQuizzes() throws SQLException {
        beforeEach();
//        Quiz quiz = new Quiz(1, "qvizi2", "satesto quiz" ,15, "physics");
//        quizDAO.addQuiz(quiz);
//
//        quiz = new Quiz(2,"qvizi2","martivi quiz",30, "english");
//        quizDAO.addQuiz(quiz);

//        quiz = new Quiz(2, "qvizi", "sashualo quiz" ,40, "chemistry");
//        quizDAO.addQuiz(quiz);
//
//        quiz = new Quiz(2, "qvizi", "sashualo quiz" ,35, "physics");
//        quiz.setRandom(true);
//        quiz.setPractice(true);
//        quizDAO.addQuiz(quiz);
//
//        quiz = new Quiz(1, "qvizi", "satesto quiz" ,20, "math");
//        Quiz quiz = new Quiz(2,"qvizi","sashualo quiz",40, "geography");
//        quizDAO.addQuiz(quiz);

    }

    public void testAddQuizzes() throws SQLException {
        beforeEach();
//        Quiz quiz = new Quiz(1, "qvizi", "satesto quiz" ,20, "math");
//        quiz.setDateCreated(Timestamp.valueOf("2023-08-11 19:51:50"));
//        assertEquals(quizDAO.addQuiz(quiz), quiz.getID());
    }

    public void testRemoveQuiz() throws SQLException {
        beforeEach();
        assertTrue(quizDAO.removeQuiz(27));
    }

    public void testGetQuizzesByAuthor() throws SQLException {
        beforeEach();
        List<Quiz> quizzes = quizDAO.getQuizzesByAuthor(1);
        assertEquals(8, quizzes.size());
        Quiz q1 = quizzes.get(1);
        assertEquals(29, q1.getID());
        assertEquals(q1.getAuthor(), 1);
        assertEquals(15, q1.getTimer());
        assertEquals(q1.getQuizName(), "qvizi2");
        assertEquals(q1.getDescription(), "satesto quiz");
        assertEquals("physics", q1.getCategory());

        quizzes = quizDAO.getQuizzesByAuthor(2);
        assertEquals(8, quizzes.size());
        Quiz q2 = quizzes.get(2);
        assertEquals(33, q2.getID());
        assertEquals(q2.getAuthor(), 2);
        assertEquals(q2.getTimer(), 35);
        assertEquals(q2.getQuizName(), "qvizi");
        assertEquals(q2.getDescription(), "sashualo quiz");
        assertEquals("physics", q2.getCategory());
    }

    public void testGetQuizByQuizName() throws SQLException {
        beforeEach();

        List<Quiz> quizzes = quizDAO.getQuizByQuizName("qvizi");
        assertEquals(10, quizzes.size());
        Quiz q1 = quizzes.get(4);
        assertEquals(2, q1.getAuthor());
        assertEquals(40, q1.getTimer());
        assertEquals("qvizi",q1.getQuizName());
        assertEquals("sashualo quiz", q1.getDescription());
        assertEquals("chemistry", q1.getCategory());

        q1 = quizzes.get(6);
        assertEquals(1, q1.getAuthor());
        assertEquals(20, q1.getTimer());
        assertEquals("qvizi",q1.getQuizName());
        assertEquals("satesto quiz", q1.getDescription());
        assertEquals("math", q1.getCategory());
    }

    public void testGetQuizByID() throws SQLException {
        beforeEach();
        Quiz quiz;
//        quiz = new Quiz(2, "qvizi", "sashualo quiz" ,35, "physics");
//        quiz.setRandom(true);
//        quiz.setPractice(true);
//        quizDAO.addQuiz(quiz);

        quiz = quizDAO.getQuizByID(37);
        assertEquals(2, quiz.getAuthor());
        assertEquals(35, quiz.getTimer());
        assertEquals("qvizi",quiz.getQuizName());
        assertEquals("sashualo quiz", quiz.getDescription());
        assertEquals("physics", quiz.getCategory());
        assertTrue(quiz.isPractice());
        assertTrue(quiz.isRandom());
        assertFalse(quiz.isOnOnePage());
        assertFalse(quiz.correctImmediately());
    }

    public void testGetPopularQuizzes() throws SQLException {
        beforeEach();
        ArrayList<Quiz> quizzes = (ArrayList<Quiz>) quizDAO.getPopularQuizzes(2);
        assertEquals(30, quizzes.get(0).getID());
        assertEquals(35, quizzes.get(1).getID());
    }

    private void addQuestions() throws SQLException {
        statement.execute("delete from QUESTIONS");
        String questionText = "Match the capitals with their respective countries.";
        String[] questions = {"France", "Germany", "Italy"};
        String[] answers = {"Paris", "Berlin", "Rome"};
        Question question = new Matching(questionText, questions, answers);

        question.setTimer(17);

        QuestionsDAO questionsDAO = new QuestionsDAO(pool);
        int questionId = questionsDAO.addQuestion(question, 41);

        questionText = "Whats the capitals of France, Germany and Italy?";
        answers = new String[]{"Paris", "Berlin", "Rome"};
        int numFields = answers.length;

        question = new MultiAnswer(questionText, answers, true);
        question.setTimer(17);
        questionId = questionsDAO.addQuestion(question, 41);

        questionText = "Whats the capitals of France, Germany and Italy?";
        answers = new String[]{"Paris", "Berlin", "Rome"};
        HashSet<String> answersSet = new HashSet<>();
        numFields = answers.length;

        question = new MultiAnswer(questionText, answers, false);
        question.setTimer(15);

        questionId = questionsDAO.addQuestion(question, 35);
    }

    public void testGetQuestions() throws SQLException {
        beforeEach();
//        addQuestions();
//        quiz with id 41 -> 41,2,qvizi,sashualo quiz,40,geography,2023-08-18 19:17:42,0,0,0,0
        List<Question> questions = quizDAO.getQuestions(41);
        assertEquals(2, questions.size());
        assertEquals("Match the capitals with their respective countries.", questions.get(0).getQuestion());
        assertEquals("Whats the capitals of France, Germany and Italy?", questions.get(1).getQuestion());
        assertEquals(17, questions.get(0).getTimer());
        assertEquals(17, questions.get(1).getTimer());
    }

    public void testRecentlyCreatedQuizzes() throws SQLException {
        beforeEach();
        List<Quiz> quizzes = quizDAO.recentlyCreatedQuizzes(5);
        assertEquals(44, quizzes.get(0).getID());
        assertEquals(43, quizzes.get(1).getID());
        assertEquals(41, quizzes.get(2).getID());
        assertEquals(40, quizzes.get(3).getID());
        assertEquals(39, quizzes.get(4).getID());
    }

    private void addTags() throws SQLException {
        statement.execute("INSERT INTO TAG(Quiz_ID, Hashtag) VALUES(35, 'Simple');");
        statement.execute("INSERT INTO TAG(Quiz_ID, Hashtag) VALUES(43, 'Medium');");
        statement.execute("INSERT INTO TAG(Quiz_ID, Hashtag) VALUES(35, 'Martivi');");
//        statement.execute("INSERT INTO TAG(Quiz_ID, Hashtag) VALUES(43, 'Medium');");
    }

    public void testGetTags() throws SQLException {
        beforeEach();
//        addTags();
        ArrayList<String> tags = (ArrayList<String>) quizDAO.getTags(43);
        assertEquals(1, tags.size());
        assertEquals("Medium", tags.get(0));
    }


    private void addReviews() throws SQLException {
        statement.execute("INSERT INTO REVIEW(User_ID,Quiz_ID, Content) VALUES(1, 35, 'Good');");
        statement.execute("INSERT INTO REVIEW(User_ID,Quiz_ID, Content) VALUES(2, 38, 'Liked it!');");
        statement.execute("INSERT INTO REVIEW(User_ID,Quiz_ID, Content) VALUES(2, 35, 'Not easy!');");
    }
    public void testGetReviews() throws SQLException {
        beforeEach();
//        addReviews();
        List<Review> reviews = quizDAO.getReviews(35);
        assertEquals(2, reviews.size());
        assertEquals(1, reviews.get(0).getUser_id());
        assertEquals(35, reviews.get(0).getQuiz_id());
        assertEquals("Good", reviews.get(0).getContent());
        assertEquals(2, reviews.get(1).getUser_id());
        assertEquals(35, reviews.get(1).getQuiz_id());
        assertEquals("Not easy!", reviews.get(1).getContent());
    }

    private void addRating() throws SQLException {
        statement.execute("INSERT INTO RATING(User_ID, Quiz_ID, Rating) VALUES(1, 44, 9);");
        statement.execute("INSERT INTO RATING(User_ID, Quiz_ID, Rating) VALUES(2, 40, 9.5);");
        statement.execute("INSERT INTO RATING(User_ID, Quiz_ID, Rating) VALUES(2, 44, 9.3);");

    }

    public void testGetRating() throws SQLException {
        beforeEach();
//        addRating();
        assertEquals(9.15, quizDAO.getRating(44));
    }

    public void testGetTopRatedQuizzes() throws SQLException {
        beforeEach();
        List<Quiz> quizes = quizDAO.getTopRatedQuizzes(1);
        assertEquals(1, quizes.size());
        assertEquals(40, quizes.get(0).getID());

    }

    public void testGetCategory() throws SQLException {
        beforeEach();
        assertEquals("history", quizDAO.getCategory(44));
        assertEquals("english", quizDAO.getCategory(35));
    }

    public void testGetQuizzesByTag() throws SQLException {
        beforeEach();
        List<Quiz> quizzes = quizDAO.getQuizzesByTag("Simple");
        assertEquals(2, quizzes.size());
        assertEquals(35, quizzes.get(0).getID());
        assertEquals(40, quizzes.get(1).getID());
    }

    public void testAddTag() throws SQLException {
        beforeEach();
        quizDAO.removeTag(40, "English");
        quizDAO.addTag(40, "English");
        assertTrue(quizDAO.getTags(40).contains("English"));
    }

    public void testRemoveTag() throws SQLException {
        beforeEach();
        assertTrue(quizDAO.getTags(40).contains("English"));
        quizDAO.removeTag(40, "English");
        assertFalse(quizDAO.getTags(40).contains("English"));
    }

    public void testGetAllQuizzes() throws SQLException {
        beforeEach();
        List<Quiz> quizzes = quizDAO.getAllQuizzes();
        assertEquals(18, quizzes.size());
    }

}
