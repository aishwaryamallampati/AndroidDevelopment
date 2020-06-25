package com.android.photogallery

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
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
                rvPhoto.adapter = PhotoAdapter(galleryItems)
            }
        )
    }

    private class PhotoHolder(tvItem: TextView) : RecyclerView.ViewHolder(tvItem) {
        val bindTitle: (CharSequence) -> Unit = tvItem::setText
    }

    private class PhotoAdapter(private val galleryItems: List<GalleryItem>) :
        RecyclerView.Adapter<PhotoHolder>() {
        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): PhotoHolder {
            val textView = TextView(parent.context)
            return PhotoHolder(textView)
        }

        override fun getItemCount(): Int = galleryItems.size

        override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
            val galleryItem = galleryItems[position]
            holder.bindTitle(galleryItem.title)
        }
    }

}