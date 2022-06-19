package com.license.workguru_app.di

import com.google.common.truth.Truth.assertThat
import io.mockk.mockk
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.Assert.*
import org.junit.rules.TestRule
import org.mockito.Mockito
import java.util.logging.Handler

class SharedViewModelTest{
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()
    private var handler = Mockito.mock(Handler::class.java)
    lateinit var viewModel:SharedViewModel

    @Before
    fun setup(){
        viewModel = SharedViewModel()
    }

    @Test
    fun `is terms and conditions accepted was saved returns true`(){
        viewModel.acceptTermsAndConditions(true)
        assertThat(viewModel.isTermsAndConditionsAccepted.value).isTrue()
    }

    @Test
    fun `is saved searching keyword returns true`(){
        viewModel.searchWithKeyword("searching")
        assertEquals(viewModel.searchingKeyword.value, "searching")
    }

    @Test
    fun `empty searching keywords returns true`(){
        viewModel.searchWithKeyword("")
        val result = viewModel.searchingKeyword.value
        assertTrue(result.isNullOrEmpty());
    }

    @Test
    fun `saved category not null returns true`(){
        viewModel.searchWithKeyword("")
        val result = viewModel.choosenCategory.value
        assertNotNull(result != null);
    }
}