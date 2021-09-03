package com.wakeup.hyperion.ui.introduce

import android.view.LayoutInflater
import androidx.viewpager2.widget.ViewPager2
import com.thuanpx.ktext.context.startActivityAtRoot
import com.thuanpx.ktext.view.gone
import com.wakeup.hyperion.R
import com.wakeup.hyperion.common.base.BaseActivity
import com.wakeup.hyperion.databinding.ActivityIntroduceBinding
import com.wakeup.hyperion.model.entity.IntroduceModel
import com.wakeup.hyperion.ui.introduce.adapter.IntroduceAdapter
import com.wakeup.hyperion.ui.signal.SignalActivity
import com.wakeup.hyperion.utils.extension.clicks

class IntroduceActivity :
    BaseActivity<IntroduceViewModel, ActivityIntroduceBinding>(IntroduceViewModel::class) {
    private var introduceList: ArrayList<IntroduceModel> = ArrayList()
    private lateinit var adapter: IntroduceAdapter
    override fun inflateViewBinding(inflater: LayoutInflater): ActivityIntroduceBinding {
        return ActivityIntroduceBinding.inflate(inflater)
    }

    override fun initialize() {
        initAdapter()
        initViewPagerClick()
        viewBinding.btnSkip.clicks {
            startActivityAtRoot(SignalActivity::class)
        }
    }

    private fun initViewPagerClick() {
        viewBinding.viewPager.isUserInputEnabled = false
        viewBinding.viewPager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                when (position) {
                    0 -> {
                        with(viewBinding) {
                            btnStart.gone(true)
                            updateStep(0)
                            btnNext.setOnClickListener {
                                viewBinding.viewPager.setCurrentItem(1, true)
                            }
                        }
                    }

                    1 -> {
                        with(viewBinding) {
                            btnStart.gone(true)
                            updateStep(1)
                            btnNext.setOnClickListener {
                                viewBinding.viewPager.setCurrentItem(2, true)
                            }
                        }
                    }

                    2 -> {
                        with(viewBinding) {
                            btnSkip.gone(true)
                            btnStart.gone(false)
                            btnNext.gone(true)
                            updateStep(2)
                            btnStart.setOnClickListener {
                                startActivityAtRoot(SignalActivity::class)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun initAdapter() {
        introduceList.add(
            IntroduceModel(
                R.drawable.bg_1,
                getString(R.string.text_introduce_description_1)
            )
        )
        introduceList.add(
            IntroduceModel(
                R.drawable.bg_2,
                getString(R.string.text_introduce_description_2)
            )
        )
        introduceList.add(
            IntroduceModel(
                R.drawable.bg_3,
                getString(R.string.text_introduce_description_3)
            )
        )
        adapter = IntroduceAdapter(introduceList)
        viewBinding.viewPager.adapter = adapter
    }

    private fun updateStep(step: Int) {
        with(viewBinding) {
            when (step) {
                0 -> {
                    tvStep1.isSelected = true
                }
                1 -> {
                    tvStep1.isSelected = true
                    tvStep2.isSelected = true
                    viewLine1.isSelected = true
                }
                2 -> {
                    viewLine1.isSelected = true
                    viewLine2.isSelected = true
                    tvStep1.isSelected = true
                    tvStep2.isSelected = true
                    tvStep3.isSelected = true
                }
            }
        }
    }
}