package com.wakeup.hyperion.ui.signal.changeSound.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuanpx.ktext.view.hide
import com.thuanpx.ktext.view.show
import com.wakeup.hyperion.common.Constant.TYPE_DELETE
import com.wakeup.hyperion.common.Constant.TYPE_SELECTED
import com.wakeup.hyperion.common.base.BaseRecyclerViewAdapter
import com.wakeup.hyperion.databinding.ItemSoundCustomBinding
import com.wakeup.hyperion.model.entity.SignalLocalModel
import com.wakeup.hyperion.utils.extension.clicks

class CustomSoundAdapter(private val itemCustomSoundCLickListener: (String, SignalLocalModel, Int) -> Unit) :
    BaseRecyclerViewAdapter<SignalLocalModel, CustomSoundAdapter.CustomSoundVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomSoundVH {
        return CustomSoundVH(
            ItemSoundCustomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ), itemCustomSoundCLickListener
        )
    }

    override fun onBindViewHolder(holder: CustomSoundVH, position: Int) {
        getItem(position)?.let {
            holder.bindData(it)
        }
    }

    class CustomSoundVH(
        val view: ItemSoundCustomBinding,
        private val itemSoundCLickListener: (String, SignalLocalModel, Int) -> Unit
    ) : RecyclerView.ViewHolder(view.root) {
        fun bindData(soundModel: SignalLocalModel) {
            with(view) {
                tvNameSound.text = soundModel.disPlayName
                if (soundModel.isSelected) {
                    ivSelectSound.show()
                } else {
                    ivSelectSound.hide()
                }
                ivDeleteSound.clicks {
                    this@CustomSoundVH.itemSoundCLickListener.invoke(
                        TYPE_DELETE,
                        soundModel,
                        adapterPosition
                    )
                }
                llContainerItemSound.clicks {
                    this@CustomSoundVH.itemSoundCLickListener.invoke(
                        TYPE_SELECTED,
                        soundModel,
                        adapterPosition
                    )
                }
            }
        }
    }
}