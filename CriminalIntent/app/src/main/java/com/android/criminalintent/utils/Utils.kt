package com.android.criminalintent.utils

import java.text.SimpleDateFormat
import java.util.*


class Utils {
    companion object {

        fun formatDate(date: Date): String? {
            val df = SimpleDateFormat("EEE, MMM d, YYYY")
            return df.format(date)
        }
    }
}