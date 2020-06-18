package com.android.criminalintent

import android.app.Application
import com.android.criminalintent.repository.CrimeRepository

// This class can be used to perform single time initializations such as singleton initialization etc
class CriminalIntentApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        CrimeRepository.initialize(this)
    }
}