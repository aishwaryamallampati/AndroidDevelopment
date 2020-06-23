package com.android.criminalintent

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.Observer
import com.android.criminalintent.model.Crime
import com.android.criminalintent.utils.Constants
import com.android.criminalintent.utils.getScaledBitmap
import com.android.criminalintent.viewmodel.CrimeDetailViewModel
import java.io.File
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
    private lateinit var ivCrimePhoto: ImageView
    private lateinit var ibCrimeCamera: ImageButton
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri

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
        ivCrimePhoto = view.findViewById(R.id.iv_crime_photo) as ImageView
        ibCrimeCamera = view.findViewById(R.id.ib_crime_camera) as ImageButton
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        crimeDetailViewModel.crimeLiveData.observe(
            viewLifecycleOwner,
            Observer { crime ->
                crime?.let {
                    this.crime = crime
                    photoFile = crimeDetailViewModel.getPhotoFile(crime)
                    // FileProvidre.getUriForFile() converts local file path into a Uri so that the camera app can see.
                    photoUri = FileProvider.getUriForFile(
                        requireActivity(),
                        "com.android.criminalintent.fileprovider",
                        photoFile
                    )
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
        updatePhotoView()
    }

    private fun updatePhotoView() {
        if (photoFile.exists()) {
            val bitmap = getScaledBitmap(photoFile.path, requireActivity())
            ivCrimePhoto.setImageBitmap(bitmap)
        } else {
            ivCrimePhoto.setImageDrawable(null)
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

        ibCrimeCamera.apply {
            // enable this button only if the device has camera and a location to save the photo
            val packageManager: PackageManager = requireActivity().packageManager
            val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val resolvedActivity: ResolveInfo? =
                packageManager.resolveActivity(captureImage, PackageManager.MATCH_DEFAULT_ONLY)
            if (resolvedActivity == null) {
                isEnabled = false
            }

            setOnClickListener {
                captureImage.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                val cameraActivities: List<ResolveInfo> = packageManager.queryIntentActivities(
                    captureImage,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                for (cameraActivity in cameraActivities) {
                    // Grant permission to camera app to store the photo taken in apps private storage
                    requireActivity().grantUriPermission(
                        cameraActivity.activityInfo.packageName,
                        photoUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                    )
                }
                startActivityForResult(captureImage, Constants.REQUEST_PHOTO)
            }
        }
    }

    // onStop() is called when fragments moves entirely out of view
    override fun onStop() {
        super.onStop()
        // Once user leaves the crime detail fragment, the crime details edited by the user are saved back to database
        crimeDetailViewModel.saveCrime(crime)
    }

    override fun onDetach() {
        super.onDetach()
        // Revoke permission given to camera app
        requireActivity().revokeUriPermission(photoUri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
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
            requestCode == Constants.REQUEST_PHOTO -> {
                // once the photo is saved by the camera app in the specified private location => then revoke the permission and updat ui
                requireActivity().revokeUriPermission(
                    photoUri,
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
                updatePhotoView()
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