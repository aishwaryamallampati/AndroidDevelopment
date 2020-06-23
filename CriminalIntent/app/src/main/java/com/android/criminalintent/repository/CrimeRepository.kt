package com.android.criminalintent.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.android.criminalintent.database.CrimeDatabase
import com.android.criminalintent.database.migration_1_2
import com.android.criminalintent.model.Crime
import com.android.criminalintent.utils.Constants
import java.io.File
import java.lang.IllegalStateException
import java.util.*
import java.util.concurrent.Executors

/**
 * UI asks repository to provide the data.
 * Repository decides whether to fetch data from local db or from server
 * CrimeRepository is a singleton class
 */
class CrimeRepository private constructor(context: Context) {
    companion object {
        private var INSTANCE: CrimeRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = CrimeRepository(context)
            }
        }

        fun get(): CrimeRepository {
            return INSTANCE ?: throw IllegalStateException("CrimeRepository must be initialized")
        }
    }

    private val database: CrimeDatabase = Room.databaseBuilder(
        context.applicationContext,
        CrimeDatabase::class.java,
        Constants.DATABASE_NAME
    ).addMigrations(migration_1_2)
        .build()

    private val crimeDao = database.crimeDao()

    // newSingleThreadExecutor function returns an executor instance that points to a new thread
    // Any work executed with the executor will therefore happen off the main thread
    private val executor = Executors.newSingleThreadExecutor()

    // returns a handle to the directory for private application files
    private val filesDir = context.applicationContext.filesDir

    fun getCrimes(): LiveData<List<Crime>> = crimeDao.getCrimes()

    fun getCrime(id: UUID): LiveData<Crime?> = crimeDao.getCrime(id)

    fun updateCrime(crime: Crime) {
        executor.execute {
            crimeDao.updateCrime(crime)
        }
    }

    fun addCrime(crime: Crime) {
        executor.execute {
            crimeDao.addCrime(crime)
        }
    }

    fun getPhotoFile(crime: Crime): File = File(filesDir, crime.photoFileName)
}