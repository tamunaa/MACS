package questionsDAOTests;

import dataBase.ConnectionPool;
import dataBase.questionsDAOs.QuestionsDAO;
import objects.questions.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

public class MultiAnswerDAOTest {
    private static final ConnectionPool pool = new ConnectionPool(5,"test_db","rootroot");
    private QuestionsDAO questionsDAO;
    private Connection conn;


    @BeforeEach
    public void setup() {
        conn = pool.getConnection();
        questionsDAO = new QuestionsDAO(pool);
    }

    @AfterEach
    public void tearDown() {
        pool.releaseConnection(conn);
    }
    @AfterAll
    static void closeConnection() throws SQLException {
        if (pool != null) {
            pool.close();
        }
    }

    @Test
    public void testAddAndGetOrderedMultiAnswerType() throws SQLException {
        String questionText = "Whats the capitals of France, Germany and Italy?";
        String[] answers = {"Paris", "Berlin", "Rome"};

        Question question = new MultiAnswer(questionText, answers, true);
        question.setTimer(17);

        int questionId = questionsDAO.addQuestion(question, 1);

        Question retrievedQuestion = questionsDAO.getQuestionByQuestionId(questionId);

        assertNotNull(retrievedQuestion);
        assertEquals(questionText, retrievedQuestion.getQuestion());
        assertArrayEquals(answers, retrievedQuestion.getCorrectAnswers());
        assertEquals("MultiAnswer", retrievedQuestion.getQuestionType());
        assertEquals(17, retrievedQuestion.getTimer());
        assertEquals(3, retrievedQuestion.getNumFields());
        assertEquals(questionId, retrievedQuestion.getQuestionId());
    }

    @Test
    public void testRemoveOrderedMultiQuestion() throws SQLException {
        String questionText = "Whats the capitals of France, Germany and Italy?";
        String[] answers = {"Paris", "Berlin", "Rome"};

        Question question = new MultiAnswer(questionText, answers, true);
        int questionId = questionsDAO.addQuestion(question, 2);

        questionsDAO.removeQuestion(questionId);
        Question retrievedQuestion = questionsDAO.getQuestionByQuestionId(questionId);
        assertEquals(null, retrievedQuestion);
    }

    @Test
    public void testAddAndGetUnorderedMultiAnswerType() throws SQLException {
        String questionText = "Whats the capitals of France, Germany and Italy?";
        String[] answers = {"Paris", "Berlin", "Rome"};
        HashSet<String> answersSet = new HashSet<>();

        Question question = new MultiAnswer(questionText, answers, false);
        question.setTimer(17);

        int questionId = questionsDAO.addQuestion(question, 1);
        Question retrievedQuestion = questionsDAO.getQuestionByQuestionId(questionId);

        assertNotNull(retrievedQuestion);
        assertEquals(questionText, retrievedQuestion.getQuestion());
        for (String answer : retrievedQuestion.getCorrectAnswers()) {
            assertFalse(answersSet.contains(answer));
        }
        assertEquals("MultiAnswer", retrievedQuestion.getQuestionType());
        assertEquals(17, retrievedQuestion.getTimer());
        assertEquals(3, retrievedQuestion.getNumFields());
        assertEquals(questionId, retrievedQuestion.getQuestionId());
    }


    @Test
    public void testRemoveUnorderedMultiQuestion() throws SQLException {
        String questionText = "Whats the capitals of France, Germany and Italy?";
        String[] answers = {"Paris", "Berlin", "Rome"};

        Question question = new MultiAnswer(questionText, answers, false);
        int questionId = questionsDAO.addQuestion(question, 2);

        questionsDAO.removeQuestion(questionId);
        Question retrievedQuestion = questionsDAO.getQuestionByQuestionId(questionId);
        assertEquals(null, retrievedQuestion);
    }



}