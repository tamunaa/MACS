package com.example.myapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var yesButton: Button
    private lateinit var noButton: Button
    private lateinit var quizManager: QuizManager

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        textView = findViewById(R.id.textView)
        yesButton = findViewById(R.id.yesButton)
        noButton = findViewById(R.id.noButton)
        quizManager = QuizManager

        yesButton.setOnClickListener{
            answerQuestion(true)
        }
        noButton.setOnClickListener{
            answerQuestion(false)
        }

        displayQuestion()
    }

    private fun displayQuestion(){
        val question = quizManager.getCurrentQuestion()
        textView.text = question.questionText

        val userAnswers = quizManager.getAnswerForCurrentQuestion()
        if (userAnswers == null){
            yesButton.isSelected = false
            noButton.isSelected = false
        }else{
            if(userAnswers == true){
                yesButton.isSelected = true
                noButton.isSelected = false
            }else{
                noButton.isSelected = true
                yesButton.isSelected = false
            }
        }
        updateQuestionUI()
    }

    private fun answerQuestion(answer: Boolean){
        quizManager.answerCurrentQuestion(answer)
        if(quizManager.isLastQuestion()){
            quizManager.quizEnded = true
            showResults()
        } else {
            quizManager.moveToNextQuestion()
            displayQuestion()
        }
    }

    private fun updateQuestionUI() {
        val totalQuestions = QuizManager.getTotalQuestions()
        val currentQuestionNumber = QuizManager.currentQuestionIndex

        val progressBar: ProgressBar = findViewById(R.id.quizProgressBar)
        progressBar.max = totalQuestions * 100
        progressBar.progress = currentQuestionNumber * 100
    }


    private fun showResults(){
        val score = quizManager.calculateScore()
        val total = quizManager.getTotalQuestions()
        val progressBar: ProgressBar = findViewById(R.id.quizProgressBar)
        val message = "You scored $score out of $total"
        textView.text = message
        yesButton.visibility = View.GONE
        noButton.visibility = View.GONE
        progressBar.visibility = View.GONE
    }

    override fun onBackPressed() {
        if (quizManager.currentQuestionIndex > 0 && !quizManager.quizEnded) {
            quizManager.moveToPreviousQuestion()
            displayQuestion()
        } else {
            super.onBackPressed()
        }
    }

}