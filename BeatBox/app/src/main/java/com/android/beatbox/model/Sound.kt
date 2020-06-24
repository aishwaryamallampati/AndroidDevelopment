package com.android.beatbox.model

class Sound(val assetPath: String, var soundId: Int? = null) {
    companion object {
        private const val TAG = "Sound"
        private const val WAV = ".wav"
    }

    // Removes file extension from the file name
    val name = assetPath.split("/").last().removeSuffix(WAV)
}