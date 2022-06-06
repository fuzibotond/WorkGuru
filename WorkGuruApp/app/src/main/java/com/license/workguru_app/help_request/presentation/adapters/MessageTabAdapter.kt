package com.license.workguru_app.help_request.presentation.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.license.workguru_app.help_request.presentation.components.MessageListFragment
import com.license.workguru_app.help_request.presentation.components.ReceivedMessageFragment

class MessageTabAdapter (fm: FragmentManager, lifecycle: Lifecycle): FragmentStateAdapter(fm,lifecycle) {


    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        return when(position){
            0->{
                ReceivedMessageFragment()
            }
            1->{
                MessageListFragment()
            }
            else->{
                Fragment()
            }
        }
    }

}