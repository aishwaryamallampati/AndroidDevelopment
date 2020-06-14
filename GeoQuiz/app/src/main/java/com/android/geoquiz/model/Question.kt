package com.android.geoquiz.model
import androidx.annotation.StringRes

// Annotations such as @StringRes are useful: 1. Prevents runtime crashes 2. Make code readable
// All model classes are declared with data keyword - as it makes it clear that this class is meant to hold data and
// compiler also provides additional functions such as equals(), hashCode() etc
data class Question(@StringRes val textResId: Int, val answer: Boolean, var userAnswered:Boolean)