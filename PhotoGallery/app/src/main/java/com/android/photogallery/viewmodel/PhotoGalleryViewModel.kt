package com.android.photogallery.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.android.photogallery.model.GalleryItem
import com.android.photogallery.repository.FlickrFetchr


class PhotoGalleryViewModel : ViewModel() {
    val galleryItemLiveData: LiveData<List<GalleryItem>>

    init {
        // request for photos is made when viewmodel is created for the first time
        // In this way, we can avoid fetching photos each time the activity is destroyed on configuration changes
        galleryItemLiveData = FlickrFetchr().fetchPhotos()
    }
}