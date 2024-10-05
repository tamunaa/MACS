package com.example.myapplication
data class Question(
    val questionText: String,
    val answer: Boolean // true or false
)
object QuizManager {
    var currentQuestionIndex = 0
    var quizEnded = false
    private val answers = mutableListOf<Boolean?>()

    private val questions = listOf(
        Question("In Kotlin, 'val' stands for 'value' and it's absolutely changeable.", false),
        Question("To declare a variable in Kotlin that can change, you would use the keyword 'var', which stands for 'variable'.", true),
        Question("Kotlin was designed to be completely interoperable with Java. Is this a programming fairy tale?", false),
        Question("In Kotlin, a 'when' statement is how you decide when to have lunch.", false),
        Question("The '!!' operator in Kotlin is used to assure the compiler that the developer is 100% confident, no doubt about it.", true)
    )

    init {
        questions.forEach {
            answers.add(null)
        }
    }

    fun getCurrentQuestion(): Question {
        return questions[currentQuestionIndex]
    }

    fun answerCurrentQuestion(answer: Boolean) {
        answers[currentQuestionIndex] = answer
    }

    fun getAnswerForCurrentQuestion(): Boolean? {
        return answers[currentQuestionIndex]
    }

    fun moveToNextQuestion() {
        if (currentQuestionIndex < questions.size - 1) {
            currentQuestionIndex++
        }
        // else do nothing, moving from last element to first seems wrong
    }
    fun moveToPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--
        }
        // else do nothing
    }

    fun isLastQuestion(): Boolean {
        return currentQuestionIndex == questions.size - 1
    }

    fun getTotalQuestions(): Int {
        return questions.size
    }

    fun calculateScore(): Int{
        return answers.filterIndexed() { index, answer ->
            answer == questions[index].answer
        } .count()
    }
}