package com.android.photogallery

import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.photogallery.model.GalleryItem
import com.android.photogallery.repository.ThumbnailDownloader
import com.android.photogallery.viewmodel.PhotoGalleryViewModel

class PhotoGalleryFragment : Fragment() {
    companion object {
        private const val TAG = "PhotoGalleryFragment"
        fun newInstance() = PhotoGalleryFragment()
    }

    private lateinit var photoGalleryViewModel: PhotoGalleryViewModel
    private lateinit var rvPhoto: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<PhotoHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        Log.i(TAG, "onCreate()")
        super.onCreate(savedInstanceState)
        photoGalleryViewModel = ViewModelProviders.of(this).get(PhotoGalleryViewModel::class.java)

        retainInstance =
            true // it is not a good practice to use this - we are using it here to make code simple and concentrate on handler threads

        // Handles the UI message loop
        // by default this handler will attach itself to the current UI thread as it is created in onCreate()
        val responseHandler = Handler()
        // Register thumbnaildownlader instance so that it can observe the lifecycle events of this fragment
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { photoHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            photoHolder.bindDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
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

        viewLifecycleOwner.lifecycle.addObserver(thumbnailDownloader.viewLifecycleObserver)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        photoGalleryViewModel.galleryItemLiveData.observe(
            viewLifecycleOwner,
            Observer { galleryItems ->
                Log.i(TAG, "Have gallery items from ViewModel $galleryItems")
                rvPhoto.adapter = PhotoAdapter(galleryItems)
            }
        )
    }

    private class PhotoHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        val ivImage = view.findViewById(R.id.iv_image) as ImageView
        val bindDrawable: (Drawable) -> Unit = ivImage::setImageDrawable
    }

    private inner class PhotoAdapter(private val galleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PhotoHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.list_item_gallery, parent, false)
            return PhotoHolder(view)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            thumbnailDownloader.queueThumbnail(holder, galleryItem.url)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.fragmentLifecycleObserver)
        // no need to remove views lifecycle observer as the fragments view lifecycle registry which keeps track of all of the view lifecycle
        // observers gets nulled out when the fragments view is destroyed.
    }

}