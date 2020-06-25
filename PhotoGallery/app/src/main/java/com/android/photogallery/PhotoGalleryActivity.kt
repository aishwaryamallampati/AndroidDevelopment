package com.android.photogallery

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle


class PhotoGalleryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_gallery)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fl_fragmentContainer, PhotoGalleryFragment.newInstance())
                .commit()
        }
    }
}