package com.android.photogallery.repository

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import java.util.concurrent.ConcurrentHashMap

private const val TAG = "ThumbnailDownloader"
private const val MESSAGE_DOWNLOAD = 0 // Used to identify download requests

// Thumbnail Downloader downloads images in the background thread but it cannot update the UI as background threads cannot update UI
// So we use message queues for this - UI has a message loop and background thread has a message loop => UI can put messages in background threads message loop and vice versa
// A handler is attached to exactly one looper, and a message is attached to exactly one target handler

// Instead of creating ThumbnailDownloader thread in PhotoGalleryFragment, ThumbnailDownloader is made lifecycle-aware component by extending lifecycle observer
// LifecycleObserver observes the lifecycle of a lifecycle owner so that the methods can be overriden and code can be added here itself
class ThumbnailDownloader<in T>(
    private var responseHandler: Handler,
    private val onThumbnailDownloaded: (T, Bitmap) -> Unit
) : HandlerThread(TAG) {

    val fragmentLifecycleObserver: LifecycleObserver = object : LifecycleObserver {
        @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
        fun setup() {
            Log.i(TAG, "Starting background thread")
            start()
            looper
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun tearDown() {
            Log.i(TAG, "Destroying background thread")
            quit() // quit() is used to terminate the thread. If quit is not called, then the handler thread will never die
        }
    }

    // If user rotates the device, then the view is destroyed this listener handles that scenario
    val viewLifecycleObserver: LifecycleObserver = object: LifecycleObserver{
        @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        fun clearQueue(){
            Log.i(TAG, "Clearing all requests from queue")
            requestHandler.removeMessages(MESSAGE_DOWNLOAD)
            requestMap.clear()
        }
    }


    private var hasQuit = false

    // requestHandler will store a reference to the Handler responsible for queueing download requests as messages on to the Thumbnail Downloader background thread
    private lateinit var requestHandler: Handler

    // The requestMap is a concurrenthashmap which is a thread-safe version of HashMap
    private val requestMap = ConcurrentHashMap<T, String>()
    private val flickrFetchr = FlickrFetchr()

    override fun quit(): Boolean {
        hasQuit = true
        return super.quit()
    }


    fun queueThumbnail(target: T, url: String) {
        Log.i(TAG, "Got a URL: $url")
        requestMap[target] = url
        // placing a message in the looper
        requestHandler.obtainMessage(MESSAGE_DOWNLOAD, target).sendToTarget()
    }

    // Once the looper is ready with messages, one by one message is processed
    @Suppress("UNCHECKED_CAST")
    @SuppressLint("HandlerLeak")
    override fun onLooperPrepared() {
        requestHandler = object : Handler() {
            override fun handleMessage(msg: Message) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    val target = msg.obj as T
                    Log.i(TAG, "Got a request for URL:${requestMap[target]}")
                    handleRequest(target)
                }
            }
        }
    }

    // 1.Performs downlaod 2.After download is complete, puts a message on the response handler to display the image in the UI
    private fun handleRequest(target: T) {
        val url = requestMap[target] ?: return
        val bitmap = flickrFetchr.fetchPhoto(url) ?: return

        responseHandler.post(Runnable {
            // if the app is closed or if the view requested for a different url by the time downlaod is complete
            if (requestMap[target] != url || hasQuit) {
                return@Runnable
            }

            requestMap.remove(target)
            onThumbnailDownloaded(target, bitmap)
        })
    }
}