package com.wakeup.hyperion.ui.signal.changeSound.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.wakeup.hyperion.R
import com.wakeup.hyperion.ui.signal.changeSound.childTab.BasicSoundFragment
import com.wakeup.hyperion.ui.signal.changeSound.childTab.CustomSoundFragment

class ChangeSoundTabAdapter(val context: Context?, fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(
        fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
    ) {
    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> {
                BasicSoundFragment.newInstance()
            }
            else -> {
                CustomSoundFragment.newInstance()
            }
        }
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> {
                context?.resources?.getString(R.string.text_title_tab_basic_sound)
            }
            else -> {
                context?.resources?.getString(R.string.text_title_tab_custom_sound)
            }
        }
    }
}