package com.android.beatbox.viewmodel

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.android.beatbox.model.Sound

class SoundViewModel : BaseObservable() {
    var sound: Sound? = null
        set(sound) {
            field = sound
            // it notifies respective binding class that all of the bindable properties  have been updated.
            // Then the binding class runs the code inside the binding mustaches again to repopulate the view
            // Here, ListItemSoundBinding will be notified and then its calls Button.setText
            notifyChange()
        }

    @get:Bindable
    val title: String?
        get() = sound?.name
}