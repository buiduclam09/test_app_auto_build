package com.wakeup.hyperion.ui.introduce.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.thuanpx.ktext.view.gone
import com.thuanpx.ktext.view.show
import com.wakeup.hyperion.databinding.ItemIntroduceBinding
import com.wakeup.hyperion.model.entity.IntroduceModel

class IntroduceAdapter(private val introduceList: ArrayList<IntroduceModel>) :
    RecyclerView.Adapter<IntroduceVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IntroduceVH = IntroduceVH(
        ItemIntroduceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
    )

    override fun getItemCount(): Int = introduceList.size

    override fun onBindViewHolder(holder: IntroduceVH, position: Int) {
        holder.bindData(introduceList[position])
    }
}

class IntroduceVH(private val item: ItemIntroduceBinding) :
    RecyclerView.ViewHolder(item.root) {
    fun bindData(introduceModel: IntroduceModel) {
        with(item) {
            imgTutorial.setImageResource(introduceModel.imageId)
            tvDescription.text = introduceModel.description
        }
    }

}