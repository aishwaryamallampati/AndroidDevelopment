package com.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.android.geoquiz.utils.Constants

class CheatActivity : AppCompatActivity() {

    // A companion object allows you to access functions without having an instance of a class similar to static functions in Java
    companion object {
        // Key for cheat sheet intent
        private const val EXTRA_ANSWER_IS_TRUE = "com.android.geoquiz.answer_is_true"
        private const val TAG = "CheatActivity"

        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }

    private lateinit var tvAnswer: TextView
    private lateinit var btnShowAnswer: Button
    private lateinit var tvApiLevel: TextView

    private var answerIsTrue = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate(Bundle?) called")
        setContentView(R.layout.activity_cheat)

        answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)
        tvAnswer = findViewById(R.id.tv_answer)
        btnShowAnswer = findViewById(R.id.btn_show_answer)
        tvApiLevel = findViewById(R.id.tv_apiLevel)
        tvApiLevel.setText(String.format(getString(R.string.api_level), android.os.Build.VERSION.SDK_INT))

        btnShowAnswer.setOnClickListener {
            val textAnswer = when {
                answerIsTrue -> R.string.btn_true
                else -> R.string.btn_false
            }
            tvAnswer.setText(textAnswer)
            setAnswerShownResult(true)
        }
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        Log.i(TAG, "setAnswerShownResult()")
        val data = Intent().apply {
            putExtra(Constants.EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

}