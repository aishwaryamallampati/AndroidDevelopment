package com.android.criminalintent.viewmodel

import androidx.lifecycle.ViewModel
import com.android.criminalintent.model.Crime
import com.android.criminalintent.repository.CrimeRepository

class CrimeListViewModel : ViewModel() {
    private val crimeRepository = CrimeRepository.get()
    val crimesListLiveData = crimeRepository.getCrimes()

    fun addCrime(crime: Crime) {
        crimeRepository.addCrime(crime)
    }
}

