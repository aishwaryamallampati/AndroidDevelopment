package com.android.criminalintent

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.android.criminalintent.model.Crime
import com.android.criminalintent.utils.Utils

class CrimeFragment : Fragment() {
    companion object {
        private const val TAG = "CrimeFragment"
    }

    private lateinit var crime: Crime
    private lateinit var etCrimeTitle: EditText
    private lateinit var btnDate: Button
    private lateinit var cbCrimeSolved: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        crime = Crime()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.i(TAG, "onCreateView")
        val view = inflater.inflate(R.layout.fragment_crime, container, false)
        etCrimeTitle = view.findViewById(R.id.et_crime_title)
        btnDate = view.findViewById(R.id.crime_date)
        cbCrimeSolved = view.findViewById(R.id.cb_crime_solved)

        btnDate.apply {
            text = Utils.formatDate(crime.date)
            isEnabled = false
        }
        return view
    }

    override fun onStart() {
        super.onStart()

        val crimeTitleWatcher = object : TextWatcher {
            override fun beforeTextChanged(
                sequence: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
                // This space intentionally left blank
            }

            override fun onTextChanged(
                sequence: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                crime.title = sequence.toString()
            }

            override fun afterTextChanged(sequence: Editable?) {
                // This one too
            }
        }
        etCrimeTitle.addTextChangedListener(crimeTitleWatcher)

        cbCrimeSolved.apply {
            setOnCheckedChangeListener { _, isChecked ->
                crime.isSolved = isChecked
            }
        }
    }
}