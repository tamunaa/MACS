package questionsDAOTests;

import dataBase.ConnectionPool;
import dataBase.questionsDAOs.QuestionsDAO;
import objects.questions.Matching;
import objects.questions.Question;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class MatchingDAOTest {
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
    public void testAddAndGetMatchingType() throws SQLException {
        String questionText = "Match the capitals with their respective countries.";
        String[] questions = {"France", "Germany", "Italy"};
        String[] answers = {"Paris", "Berlin", "Rome"};
        Question question = new Matching(questionText, questions, answers);

        question.setTimer(17);

        int questionId = questionsDAO.addQuestion(question, 1);

        Question retrievedQuestion = questionsDAO.getQuestionByQuestionId(questionId);

        assertNotNull(retrievedQuestion);
        assertEquals(questionText, retrievedQuestion.getQuestion());
        assertArrayEquals(questions, retrievedQuestion.getOptions());
        assertArrayEquals(answers, retrievedQuestion.getCorrectAnswers());
        assertEquals("Matching", retrievedQuestion.getQuestionType());
        assertEquals(17, retrievedQuestion.getTimer());
        assertEquals(3, retrievedQuestion.getNumFields());
        assertEquals(questionId, retrievedQuestion.getQuestionId());
    }

    @Test
    public void testRemoveOrderedMultiQuestion() throws SQLException {
        String questionText = "Match the capitals with their respective countries.";
        String[] questions = {"France", "Germany", "Italy"};
        String[] answers = {"Paris", "Berlin", "Rome"};
        Question question = new Matching(questionText, questions, answers);

        int questionId = questionsDAO.addQuestion(question, 2);

        questionsDAO.removeQuestion(questionId);
        Question retrievedQuestion = questionsDAO.getQuestionByQuestionId(questionId);
        assertEquals(null, retrievedQuestion);
    }



}