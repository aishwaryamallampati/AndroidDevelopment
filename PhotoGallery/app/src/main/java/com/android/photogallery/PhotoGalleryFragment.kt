package com.android.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.model.GalleryItem
import com.android.photogallery.repository.FlickrFetchr
import com.android.photogallery.viewmodel.PhotoGalleryViewModel

class PhotoGalleryFragment : Fragment() {
    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance() = PhotoGalleryFragment()
    }

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var rvPhoto: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer { galleryItems ->
                Log.i(TAG, "Have gallery items from ViewModel $galleryItems")

            }
        )
    }

}