package com.android.geoquiz.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.android.geoquiz.R
import com.android.geoquiz.model.Question

private const val TAG = "QuizViewModel"

class QuizViewModel : ViewModel() {

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
    var currentIndex = 0
    private var questionsAnswered = 0
    private var questionsAnsweredCorrectly = 0

    val currentQuestionText: Int
        get() = questionBank[currentIndex].textResId

    val currentQuestionAnswer: Boolean
        get() = questionBank[currentIndex].answer

    val isCurrentQuestionAnswered: Boolean
        get() = questionBank[currentIndex].userAnswered

    fun moveToNext() {
        currentIndex = (currentIndex + 1) % questionBank.size
    }

    fun moveToPrev() {
        if (currentIndex == 0) {
            currentIndex = questionBank.size
        }
        currentIndex -= 1
    }

    // Once user submits answer to a question, mark it as answered and update answer buttons
    fun markQuestionAsAnswered() {
        questionsAnswered += 1
        questionBank[currentIndex].userAnswered = true
    }

    fun incrementCorrectAnswerCount() {
        questionsAnsweredCorrectly += 1
    }

    fun areAllQuestionsAnswered(): Boolean {
        if (questionsAnswered == questionBank.size) {
            return true
        }
        return false
    }

    // Quiz score is computed as a percentage
    fun calculateQuizScore(): Int {
        return ((questionsAnsweredCorrectly.toDouble() / questionsAnswered) * 100).toInt()
    }

    init {
        Log.d(TAG, "ViewModel instance created")
    }

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "ViewModel instance about to be destroyed")
    }

}