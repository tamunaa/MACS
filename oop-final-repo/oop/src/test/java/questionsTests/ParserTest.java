package questionsTests;

import objects.questions.Parser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;

public class ParserTest {

    // main tests of StringArrayToString
    @Test
    public void testStringArrayToStringEmpty() {
        String[] arr = new String[0];
        assertEquals("", Parser.StringArrayToString(arr));
    }

    @Test
    void testStringArrayToStringSingleElement() {
        String[] arr = { "  aPPle" };
        assertEquals("  aPPle", Parser.StringArrayToString(arr));
    }

    @Test
    void testStringArrayToStringMultipleElements() {
        String[] arr = { "apple", "banana", "orange" };
        assertEquals("apple,banana,orange", Parser.StringArrayToString(arr));
    }

    // main tests of StringToStringArray
    @Test
    void testStringToStringArrayEmpty() {
        String str = "";
        assertArrayEquals(new String[0], Parser.StringToStringArray(str));
    }

    @Test
    void testStringToStringArraySingleElement() {
        String str = "apple";
        assertArrayEquals(new String[] { "apple" }, Parser.StringToStringArray(str));
    }

    @Test
    void testStringToStringArrayMultipleElements() {
        String str = "apple,banana, Orange";
        assertArrayEquals(new String[] { "apple", "banana", " Orange" }, Parser.StringToStringArray(str));
    }

    // main tests of GetQuestionsAndAnswersOfMatching
    @Test
    void testGetQuestionsAndAnswersOfMatchingEmpty() {
        String str = "";
        assertTrue(Parser.GetQuestionsAndAnswersOfMatching(str).isEmpty());
    }

    @Test
    void testGetQuestionsAndAnswersOfMatchingValidInput() {
        String str = "q1:a1,q2:a2,q3:a3";
        HashMap<String, String> expected = new HashMap<>();
        expected.put("q1", "a1");
        expected.put("q2", "a2");
        expected.put("q3", "a3");
        assertEquals(expected, Parser.GetQuestionsAndAnswersOfMatching(str));
    }

    // random more tests
    @Test
    void testStringArrayToStringRandom1() {
        String[] arr = { "apple", "banana", "orange", "grape" };
        assertEquals("apple,banana,orange,grape", Parser.StringArrayToString(arr));
    }

    @Test
    void testStringArrayToStringRandom3() {
        String[] arr = { "", "", "" };
        assertEquals(",,", Parser.StringArrayToString(arr));
    }

    @Test
    void testStringToStringArrayRandom1() {
        String str = "apple,banana,orange,grape";
        assertArrayEquals(new String[] { "apple", "banana", "orange", "grape" }, Parser.StringToStringArray(str));
    }

    @Test
    void testStringToStringArrayRandom2() {
        String str = "one";
        assertArrayEquals(new String[] { "one" }, Parser.StringToStringArray(str));
    }

    @Test
    void testStringToStringArrayRandom3() {
        String str = ",apple,banana,,orange,";
        assertArrayEquals(new String[] { "", "apple", "banana", "", "orange"}, Parser.StringToStringArray(str));
    }

    @Test
    void testGetQuestionsAndAnswersOfMatchingRandom2() {
        String str = "question1:answer1,question2:answer2,question3:answer3,question4:answer4";
        HashMap<String, String> expected = new HashMap<>();
        expected.put("question1", "answer1");
        expected.put("question2", "answer2");
        expected.put("question3", "answer3");
        expected.put("question4", "answer4");
        assertEquals(expected, Parser.GetQuestionsAndAnswersOfMatching(str));
    }
}
