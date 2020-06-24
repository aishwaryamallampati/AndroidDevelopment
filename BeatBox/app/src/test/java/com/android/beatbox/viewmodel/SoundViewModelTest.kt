package com.android.beatbox.viewmodel

import com.android.beatbox.BeatBox
import com.android.beatbox.model.Sound
import org.hamcrest.core.Is.`is`
import org.junit.Before

import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify

class SoundViewModelTest {
    private lateinit var sound: Sound
    private lateinit var subject: SoundViewModel // name the object under test as subject
    private lateinit var beatBox: BeatBox

    // this block will be run once before each test executes
    @Before
    fun setUp() {
        beatBox =
            mock(BeatBox::class.java) // creates a mocked BeatBox class which has all the functions of beatbox but does nothing
        sound = Sound("assestPath")
        subject = SoundViewModel(beatBox)
        subject.sound = sound
    }

    @Test
    fun exposesSoundNameAsTitle() {
        assertThat(subject.title, `is`(sound.name))
    }

    @Test
    fun callsBeatBoxPlayOnButtonClicked() {
        subject.onButtonClicked()
        // All mockito mock objects keep track of which of their functions have been called as well as what parameters were passed in for each call
        // So, using mockitos verify method we can check whether onButtonClicked function called BeatBox.play(Sound) method
        verify(beatBox).play(sound)
    }
}