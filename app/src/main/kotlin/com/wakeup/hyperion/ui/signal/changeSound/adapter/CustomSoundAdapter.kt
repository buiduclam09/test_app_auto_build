package com.wakeup.hyperion.ui.signal.changeSound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wakeup.hyperion.common.base.BaseRecyclerViewAdapter
import com.wakeup.hyperion.databinding.ItemSoundCustomBinding
import com.wakeup.hyperion.model.entity.SoundModel

class CustomSoundAdapter() :
    BaseRecyclerViewAdapter<SoundModel, CustomSoundAdapter.CustomSoundVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSoundVH {
        return CustomSoundVH(
            ItemSoundCustomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CustomSoundVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    class CustomSoundVH(val view: ItemSoundCustomBinding) : RecyclerView.ViewHolder(view.root) {
        fun bindData(soundModel: SoundModel) {
            with(view) {
                tvNameSound.text = soundModel.name
            }
        }
    }
}