package com.android.beatbox

import android.content.res.AssetFileDescriptor
import android.content.res.AssetManager
import android.media.SoundPool
import android.util.Log
import com.android.beatbox.model.Sound
import java.io.IOException

class BeatBox(private val assets: AssetManager) {
    companion object {
        private const val TAG = "BeatBox"
        private const val SOUNDS_FOLDER = "sample_sounds"
        private const val MAX_SOUNDS = 5
    }

    val sounds: List<Sound>
    private val soundPool = SoundPool.Builder() // SoundPool plays a sound immediately without delay
        .setMaxStreams(MAX_SOUNDS) // At any point of time, at max 5 sound files can be played
        .build()

    init {
        sounds = loadSounds()
    }

    // Loads sound files from assets
    private fun loadSounds(): List<Sound> {
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
        soundNames.forEach { filename ->
            val assetPath = "$SOUNDS_FOLDER/$filename"
            val sound = Sound(assetPath)
            try {
                load(sound)
                sounds.add(sound)
            } catch (ioe: IOException) {
                Log.e(TAG, "Could not load sound $filename", ioe)
            }
        }
        return sounds
    }

    private fun load(sound: Sound) {
        val afd: AssetFileDescriptor = assets.openFd(sound.assetPath)
        val soundId = soundPool.load(
            afd,
            1
        ) // loads a file into soundpool for later playback - id is returned to keep track of the file
        sound.soundId = soundId
    }

    fun play(sound: Sound) {
        sound.soundId?.let {
            soundPool.play(it, 1.0f, 1.0f, 1, 0, 1.0f)
        }
    }

    fun release() {
        soundPool.release()
    }
}