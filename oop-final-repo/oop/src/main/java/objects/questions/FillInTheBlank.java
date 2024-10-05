package objects.questions;

import java.io.Serializable;

public class FillInTheBlank extends QuestionResponse implements Serializable {
    public FillInTheBlank(String questionText, String correctAnswer) {
        super(questionText, correctAnswer);
    }

    @Override
    public String getQuestionType() {
        return "FillInTheBlank";
    }
}
