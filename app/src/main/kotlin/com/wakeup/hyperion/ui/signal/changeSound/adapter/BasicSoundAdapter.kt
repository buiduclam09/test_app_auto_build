package com.wakeup.hyperion.ui.signal.changeSound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.thuanpx.ktext.boolean.isTrue
import com.thuanpx.ktext.view.hide
import com.thuanpx.ktext.view.show
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.Constant
import com.wakeup.hyperion.common.base.BaseRecyclerViewAdapter
import com.wakeup.hyperion.databinding.ItemSoundBinding
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.model.entity.SoundModel
import com.wakeup.hyperion.utils.extension.clicks

class BasicSoundAdapter(private val itemSoundCLickListener: (String, SignalLocalModel) -> Unit) :
    BaseRecyclerViewAdapter<SignalLocalModel, BasicSoundAdapter.BasicSoundVH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BasicSoundVH {
        return BasicSoundVH(
            ItemSoundBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            itemSoundCLickListener
        )
    }

    override fun onBindViewHolder(holder: BasicSoundVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    class BasicSoundVH(
        val item: ItemSoundBinding,
        private val itemSoundCLickListener: (String, SignalLocalModel) -> Unit
    ) : RecyclerView.ViewHolder(item.root) {
        fun bindData(signalLocalModel: SignalLocalModel) {
            with(item) {
                tvNameSound.text = signalLocalModel.name
                if (signalLocalModel.isSelected) {
                    ivSelectSound.show()
                } else {
                    ivSelectSound.hide()
                }
                if (signalLocalModel.isPlaying.isTrue()) {
                    ivPlaySound.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_pause))
                } else {
                    ivPlaySound.setImageDrawable(ContextCompat.getDrawable(itemView.context, R.drawable.ic_play_sound))
                }
                ivPlaySound.clicks {
                    this@BasicSoundVH.itemSoundCLickListener.invoke(Constant.TYPE_PLAY, signalLocalModel)
                }
                llContainerItemSound.clicks {
                    this@BasicSoundVH.itemSoundCLickListener.invoke(Constant.TYPE_SELECTED, signalLocalModel)
                }
            }
        }
    }
}