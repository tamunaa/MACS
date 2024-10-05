package questionsTests;

import objects.QuestionResponsePair;
import objects.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ResponseTest {

    @Test
    public void testResponseMethods() {
        // Create a Response object for testing
        Response response = new Response(1, 123, 456, 90, true, "Sample response");

        // Test the getters
        assertEquals(1, response.getId());
        assertEquals(123, response.getQuestionId());
        assertEquals(456, response.getHistoryId());
        assertEquals(90, response.getGrade());
        assertTrue(response.isGraded());
        assertEquals("Sample response", response.getResponseText());

        // Test the toString method
        String expectedToString = "Response{" +
                "id=1, questionId=123, historyId=456, grade=90, isGraded=true, responseText='Sample response'}";
        assertEquals(expectedToString, response.toString());

        response.setId(2);
        assertEquals(2, response.getId());

        response.setQuestionId(234);
        assertEquals(234, response.getQuestionId());

        response.setHistoryId(567);
        assertEquals(567, response.getHistoryId());

        response.setGrade(80);
        assertEquals(80, response.getGrade());

        response.setGraded(false);
        assertFalse(response.isGraded());

        response.setResponseText("Updated response");
        assertEquals("Updated response", response.getResponseText());
    }
}
