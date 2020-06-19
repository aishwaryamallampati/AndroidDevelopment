package com.android.criminalintent.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.android.criminalintent.database.CrimeTypeConverters
import java.util.*

@Entity
data class Crime(
    @PrimaryKey val id: UUID = UUID.randomUUID(),
    var title: String = "",
    @TypeConverters(CrimeTypeConverters::class)
    var date: Date = Date(),
    var isSolved: Boolean = false,
    var requiresPolice: Boolean = false
)