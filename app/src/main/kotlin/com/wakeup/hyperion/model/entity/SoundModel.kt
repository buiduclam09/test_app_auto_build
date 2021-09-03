package com.wakeup.hyperion.model.entity

import androidx.recyclerview.widget.DiffUtil

data class SoundModel(
    val id: String? = null,
    val name: String? = null,
    var isSelected : Boolean = false,
    var isPlaying: Boolean = false,
    val path: String
)

val SOUND_DIFF = SoundModelItemDiff()

class SoundModelItemDiff : DiffUtil.ItemCallback<SoundModel>() {
    override fun areItemsTheSame(oldItem: SoundModel, newItem: SoundModel): Boolean =
        oldItem.id === newItem.id

    override fun areContentsTheSame(oldItem: SoundModel, newItem: SoundModel): Boolean =
        oldItem == newItem
}
