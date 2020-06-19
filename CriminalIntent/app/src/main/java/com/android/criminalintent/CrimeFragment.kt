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
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.android.criminalintent.model.Crime
import com.android.criminalintent.utils.Constants
import com.android.criminalintent.viewmodel.CrimeDetailViewModel
import java.sql.Date
import java.util.*

class CrimeFragment : Fragment(), DatePickerFragment.Callbacks {
    companion object {
        private const val TAG = "CrimeFragment"

        fun newInstance(crimeId: UUID): CrimeFragment {
            val args = Bundle().apply {
                putSerializable(Constants.ARG_CRIME_ID, crimeId)
            }
            return CrimeFragment().apply {
                arguments = args
            }
        }
    }

    private lateinit var crime: Crime
    private lateinit var etCrimeTitle: EditText
    private lateinit var btnDate: Button
    private lateinit var cbCrimeSolved: CheckBox

    private val crimeDetailViewModel: CrimeDetailViewModel by lazy {
        ViewModelProviders.of(this).get(CrimeDetailViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "onCreate")
        crime = Crime()
        val crimeId: UUID = arguments?.getSerializable(Constants.ARG_CRIME_ID) as UUID
        Log.d(TAG, "args bundle crime ID: $crimeId")
        crimeDetailViewModel.loadCrime(crimeId)
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

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    updateUI()
                }
            }
        )
    }

    private fun updateUI() {
        etCrimeTitle.setText(crime.title)
        btnDate.text = crime.date.toString()
        cbCrimeSolved.apply {
            isChecked = crime.isSolved
            jumpDrawablesToCurrentState() // this eliminates the checkbox animation after opening the crime detail fragment
        }
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

        btnDate.setOnClickListener {
            DatePickerFragment.newInstance(crime.date).apply {
                setTargetFragment(this@CrimeFragment, Constants.REQUEST_DATE)
                show(this@CrimeFragment.requireFragmentManager(), Constants.DIALOG_DATE)
            }
        }
    }

    // onStop() is called when fragments moves entirely out of view
    override fun onStop() {
        super.onStop()
        // Once user leaves the crime detail fragment, the crime details edited by the user are saved back to database
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDateSelected(date: java.util.Date) {
        crime.date = date
        updateUI()
    }
}