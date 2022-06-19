package com.license.workguru_app.timetracking.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.license.workguru_app.profile.presentation.components.ChartFragment
import com.license.workguru_app.profile.presentation.components.StatementsFragment

class TabAdapter (fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm,lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                ChartFragment()
            }
            1->{
                StatementsFragment()
            }
            else->{
                Fragment()
            }
        }
    }

}