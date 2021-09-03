package com.wakeup.hyperion.ui.signal.changeSound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wakeup.hyperion.common.base.BaseRecyclerViewAdapter
import com.wakeup.hyperion.databinding.ItemSoundBinding
import com.wakeup.hyperion.model.entity.SoundModel

class BasicSoundAdapter : BaseRecyclerViewAdapter<SoundModel, BasicSoundAdapter.BasicSoundVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicSoundVH {
        return BasicSoundVH(
            ItemSoundBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: BasicSoundVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    class BasicSoundVH(val item: ItemSoundBinding) : RecyclerView.ViewHolder(item.root) {
        fun bindData(soundModel: SoundModel) {
            with(item) {
                tvNameSound.text = soundModel.name
            }
        }
    }
}