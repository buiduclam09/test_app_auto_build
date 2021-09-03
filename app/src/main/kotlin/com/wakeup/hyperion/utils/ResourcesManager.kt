package com.wakeup.hyperion.utils

import android.content.Context
import android.provider.MediaStore
import com.wakeup.hyperion.model.entity.SignalLocalModel

object ResourcesManager {
    private var mListAudio = arrayListOf<SignalLocalModel>()
    fun getSongFromDevice(): ArrayList<SignalLocalModel> {
        return mListAudio
    }

    private fun createAudioFromUri(path: String, context: Context?): SignalLocalModel? {
        val project = arrayOf(
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
        )
        //todo check path -> uri null
        val where = "${MediaStore.Audio.Media.DATA} = ?"
        val cursor = context?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, project,
            where, arrayOf(path), null
        )
        cursor?.use {
            it.moveToFirst()
            return SignalLocalModel(
                false, it.getString(1),
                path = where, isPlaying = false, disPlayName = it.getString(0),isSelected = false,
            )
        }
        return null
    }

    fun createListAudioFromListUri(
        listUri: MutableList<String>,
        context: Context?
    ): MutableList<SignalLocalModel>? {
        val listAudio = mutableListOf<SignalLocalModel>()
        if (listUri.size > 0) {
            listUri.forEach {
                val audio = createAudioFromUri(it, context)
                audio?.let { item ->
                    listAudio.add(item)
                }
            }
        }
        return listAudio
    }
}