package com.android.beatbox

import android.content.res.AssetManager
import android.util.Log
import com.android.beatbox.model.Sound

class BeatBox(private val assets: AssetManager) {
    companion object {
        private const val TAG = "BeatBox"
        private const val SOUNDS_FOLDER = "sample_sounds"
    }

    val sounds: List<Sound>

    init {
        sounds = loadSounds()
    }

    // Loads sound files from assets
    fun loadSounds(): List<Sound> {
        val soundNames: Array<String>
        try {
            // AssetManager.list() lists filenames contained in the folder path that is given as input
            soundNames = assets.list(SOUNDS_FOLDER)!!
            if (soundNames != null) {
                Log.i(TAG, "Found ${soundNames.size} sounds")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Could not list assets", e)
            return emptyList()
        }
        val sounds = mutableListOf<Sound>()
        soundNames.forEach{filename->
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
            sounds.add(sound)
        }
        return sounds
    }
}