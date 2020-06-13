package com.android.geoquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.geoquiz.model.Question
import com.android.geoquiz.utils.Constants

class MainActivity : AppCompatActivity() {
    // Using lateinit, we are informing compiler that these buttons will be assigned to non-null values later
    private lateinit var btnTrue: Button
    private lateinit var btnFalse: Button
    private lateinit var btnNext: Button
    private lateinit var btnPrev: Button
    private lateinit var tvQuestion: TextView

    // Questions to be posed to the user
    private val questionBank = listOf(
        Question(R.string.question_australia, true),
        Question(R.string.question_oceans, true),
        Question(R.string.question_mideast, false),
        Question(R.string.question_africa, false),
        Question(R.string.question_americas, true),
        Question(R.string.question_asia, true)
    )

    // Index to keep track of question displayed to the user
    private var currentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
            checkAnswer(true)
        }
        btnFalse.setOnClickListener{view:View->
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

    private fun updateQuestion(){
        // Display question text
        val questionTextResId = questionBank[currentIndex].textResId
        tvQuestion.setText(questionTextResId)
    }

    private fun checkAnswer(userAnswer: Boolean){
        val correctAnswer = questionBank[currentIndex].answer

        val messageResId = if(userAnswer == correctAnswer){
            R.string.toast_correct
        } else{
            R.string.toast_incorrect
        }

        // Display toast message based on user click
        var toast = Toast.makeText(this, messageResId, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, Constants.TOAST_XOFFSET, Constants.TOAST_YOFFSET)
        toast.show()
    }
}
