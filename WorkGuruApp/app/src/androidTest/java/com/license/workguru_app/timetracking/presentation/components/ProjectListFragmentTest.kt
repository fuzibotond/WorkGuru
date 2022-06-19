package com.license.workguru_app.timetracking.presentation.components


import com.google.common.truth.Truth.assertThat
import org.junit.Test


class ProjectListFragmentTest{
    val projectListFragment = ProjectListFragment()

    @Test
    fun isItemListEmpty_returnFalse(){
        val result = projectListFragment.itemList.isEmpty()
        assertThat(result).isFalse()
    }

}