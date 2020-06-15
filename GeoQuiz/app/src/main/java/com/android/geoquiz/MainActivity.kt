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
import androidx.lifecycle.ViewModelProviders
import com.android.geoquiz.utils.Constants
import com.android.geoquiz.viewmodel.QuizViewModel

class MainActivity : AppCompatActivity() {
    // Tag to log messages
    companion object {
        private const val TAG = "MainActivity"
        private const val KEY_INDEX = "index"
    }

    // Using lateinit, we are informing compiler that these buttons will be assigned to non-null values later
    private lateinit var btnTrue: Button
    private lateinit var btnFalse: Button
    private lateinit var btnNext: ImageButton
    private lateinit var btnPrev: ImageButton
    private lateinit var tvQuestion: TextView

    // Using by lazy makes the quizViewModel a val instead of a var
    // In this way, quizViewModel is assigned a value only one time
    private val quizViewModel: QuizViewModel by lazy {
        ViewModelProviders.of(this).get(QuizViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_main)

        // check if current index is saved in the bundle
        // If user used the app before, then app starts displaying the question from where he left the app
        val currentIndex = savedInstanceState?.getInt(KEY_INDEX, 0) ?: 0
        Log.i("bug", "currentIndex:" + currentIndex)
        quizViewModel.currentIndex = currentIndex

        // Assign all view to respective variables
        btnTrue = findViewById(R.id.btn_true)
        btnFalse = findViewById(R.id.btn_false)
        btnNext = findViewById(R.id.btn_next)
        btnPrev = findViewById(R.id.btn_prev)
        tvQuestion = findViewById(R.id.tv_question)

        // Display question to the user
        updateQuestion()

        // Logic for each button
        btnTrue.setOnClickListener { view: View ->
            quizViewModel.markQuestionAsAnswered()
            updateAnswerButtons()
            checkAnswer(true)
        }
        btnFalse.setOnClickListener { view: View ->
            quizViewModel.markQuestionAsAnswered()
            updateAnswerButtons()
            checkAnswer(false)
        }
        btnNext.setOnClickListener { View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }
        btnPrev.setOnClickListener { View ->
            quizViewModel.moveToPrev()
            updateQuestion()
        }
        tvQuestion.setOnClickListener { View ->
            quizViewModel.moveToNext()
            updateQuestion()
        }
    }

    private fun updateQuestion() {
        // Display question text
        val questionTextResId = quizViewModel.currentQuestionText
        tvQuestion.setText(questionTextResId)
        updateAnswerButtons()
    }

    // User is allowed to enter answer to each question exactly once
    private fun updateAnswerButtons() {
        if (quizViewModel.isCurrentQuestionAnswered) {
            btnTrue.isEnabled = false
            btnFalse.isEnabled = false
        } else {
            btnTrue.isEnabled = true
            btnFalse.isEnabled = true
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = if (userAnswer == correctAnswer) {
            quizViewModel.incrementCorrectAnswerCount()
            R.string.toast_correct
        } else {
            R.string.toast_incorrect
        }

        // Display toast message based on user click
        var toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, Constants.TOAST_XOFFSET, Constants.TOAST_YOFFSET)
        toast.show()

        if (quizViewModel.areAllQuestionsAnswered()) {
            displayScore()
        }
    }

    // Displays quiz score as percentage
    private fun displayScore() {
        val score = quizViewModel.calculateQuizScore()
        val message = getString(R.string.score, score)
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        Log.i(TAG, "onSaveInstanceState")
        savedInstanceState.putInt(KEY_INDEX, quizViewModel.currentIndex)
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
}
