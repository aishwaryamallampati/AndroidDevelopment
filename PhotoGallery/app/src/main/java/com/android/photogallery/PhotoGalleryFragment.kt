package com.android.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.model.GalleryItem
import com.android.photogallery.repository.FlickrFetchr

class PhotoGalleryFragment : Fragment() {
    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance() = PhotoGalleryFragment()
    }

    private lateinit var rvPhoto: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        val flickrLiveData: LiveData<List<GalleryItem>> = FlickrFetchr().fetchPhotos()
        flickrLiveData.observe(
            this,
            Observer { galleryItems ->
                Log.d(TAG, "Response recerived: $galleryItems")
            }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        Log.i(TAG, "onCreateView()")
        val view = inflater.inflate(R.layout.fragment_photo_gallery, container, false)
        rvPhoto = view.findViewById(R.id.rv_photo)
        rvPhoto.layoutManager = GridLayoutManager(context, 3)
        return view
    }

}