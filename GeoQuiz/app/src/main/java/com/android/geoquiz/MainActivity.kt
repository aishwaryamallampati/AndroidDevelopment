package com.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import com.android.geoquiz.model.Question
import com.android.geoquiz.utils.Constants

class MainActivity : AppCompatActivity() {
    // Tag to log messages
    companion object{
        private const val TAG = "MainActivity"
    }

    // Using lateinit, we are informing compiler that these buttons will be assigned to non-null values later
    private lateinit var btnTrue: Button
    private lateinit var btnFalse: Button
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var tvQuestion: TextView

    // Questions to be posed to the user
    private val questionBank = listOf(
        Question(R.string.question_australia, true, false),
        Question(R.string.question_oceans, true, false),
        Question(R.string.question_mideast, false, false),
        Question(R.string.question_africa, false, false),
        Question(R.string.question_americas, true, false),
        Question(R.string.question_asia, true, false)
    )

    // Index to keep track of question displayed to the user
    private var currentIndex = 0
    private var questionsAnswered = 0
    private var questionsAnsweredCorrectly = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        // Assign all view to respective variables
        btnTrue = findViewById(R.id.btn_true)
        btnFalse = findViewById(R.id.btn_false)
        btnNext = findViewById(R.id.btn_next)
        btnPrev = findViewById(R.id.btn_prev)
        tvQuestion = findViewById(R.id.tv_question)

        // Display question to the user
        updateQuestion()

        // Logic for each button
        btnTrue.setOnClickListener{view:View->
            markQuestionAsAnswered()
            checkAnswer(true)
        }
        btnFalse.setOnClickListener{view:View->
            markQuestionAsAnswered()
            checkAnswer(false)
        }
        btnNext.setOnClickListener{View->
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }
        btnPrev.setOnClickListener{View->
            if(currentIndex == 0){
                currentIndex = questionBank.size
            }
            currentIndex = (currentIndex - 1) % questionBank.size
            updateQuestion()
        }
        tvQuestion.setOnClickListener{View->
            currentIndex = (currentIndex + 1) % questionBank.size
            updateQuestion()
        }
    }

    // Overriding activity life cycle methods
    override fun onStart() {
        super.onStart()
        Log.d(TAG, "onStart() called")
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() called")
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "onPause() called")
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "onStop() called")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy() called")
    }

    private fun updateQuestion(){
        // Display question text
        val questionTextResId = questionBank[currentIndex].textResId
        tvQuestion.setText(questionTextResId)
        updateAnswerButtons()
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = questionBank[currentIndex].answer

        val messageResId = if(userAnswer == correctAnswer){
            questionsAnsweredCorrectly += 1
            R.string.toast_correct
        } else{
            R.string.toast_incorrect
        }

        // Display toast message based on user click
        var toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, Constants.TOAST_XOFFSET, Constants.TOAST_YOFFSET)
        toast.show()

        areAllQuestionsAnswered()
    }

    // User is allowed to enter answer to each question exactly once
    private fun updateAnswerButtons(){
        if(questionBank[currentIndex].userAnswered){
            btnTrue.isEnabled = false
            btnFalse.isEnabled = false
        } else{
            btnTrue.isEnabled = true
            btnFalse.isEnabled = true
        }
    }

    // Once user submits answer to a question, mark it as answered and update answer buttons
    private fun markQuestionAsAnswered(){
        questionsAnswered += 1
        questionBank[currentIndex].userAnswered = true
        updateAnswerButtons()
    }

    // If user answered all the questions, then display a score
    private fun areAllQuestionsAnswered(){
        if(questionsAnswered == questionBank.size){
            val score = ((questionsAnsweredCorrectly.toDouble()/questionsAnswered)*100).toInt()
            val message = getString(R.string.score, score)
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }
    }

}
