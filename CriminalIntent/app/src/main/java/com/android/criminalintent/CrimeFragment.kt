package com.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
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
    private lateinit var btnChooseSuspect: Button
    private lateinit var btnSendReport: Button

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
        btnChooseSuspect = view.findViewById(R.id.btn_choose_suspect)
        btnSendReport = view.findViewById(R.id.btn_send_crime_report)
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
        if (crime.suspect.isNotEmpty()) {
            btnChooseSuspect.text = crime.suspect
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

        btnSendReport.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getCrimeReport())
                putExtra(
                    Intent.EXTRA_SUBJECT,
                    getString(R.string.crime_report_subject)
                )
            }.also { intent ->
                // this displays the different choices available on user device to display the provided intent
                val chooserIntent = Intent.createChooser(intent, getString(R.string.send_report))
                startActivity(chooserIntent)
            }
        }

        btnChooseSuspect.apply {
            val pickContactIntent =
                Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI)

            setOnClickListener {
                startActivityForResult(pickContactIntent, Constants.REQUEST_CONTACT)
            }
            val packageManager: PackageManager = requireActivity().packageManager
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(pickContactIntent, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when {
            resultCode != Activity.RESULT_OK -> return

            requestCode == Constants.REQUEST_CONTACT && data != null -> {
                val contactUri: Uri? = data.data
                val queryFields = arrayOf(ContactsContract.Contacts.DISPLAY_NAME)
                val cursor = contactUri?.let {
                    requireActivity().contentResolver
                        .query(it, queryFields, null, null, null)
                }
                cursor?.use {
                    if (it.count == 0) {
                        return
                    }

                    it.moveToFirst()
                    val suspect = it.getString(0)
                    crime.suspect = suspect
                    crimeDetailViewModel.saveCrime(crime)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun getCrimeReport(): String {
        val solvedString = if (crime.isSolved) {
            getString(R.string.crime_report_solved)
        } else {
            getString(R.string.crime_report_unsolved)
        }
        val dateString =
            android.text.format.DateFormat.format(Constants.DATE_FORMAT, crime.date.time).toString()

        var suspect = if (crime.suspect.isBlank()) {
            getString(R.string.crime_report_no_suspect)
        } else {
            getString(R.string.crime_report_suspect, crime.suspect)
        }

        return getString(R.string.crime_report, crime.title, dateString, solvedString, suspect)
    }
}