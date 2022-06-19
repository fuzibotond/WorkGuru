package com.license.workguru_app.timetracking.domain.use_case.list_projects

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ApplicationProvider
import com.google.ar.core.Config
import com.google.common.truth.Truth.assertThat
import com.license.workguru_app.AuthorizedActivity
import com.license.workguru_app.di.SharedViewModel
import com.license.workguru_app.timetracking.data.remote.TimeTrackingApi
import com.license.workguru_app.timetracking.data.repository.TimeTrackingRepository
import com.nhaarman.mockito_kotlin.mock
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import org.robolectric.Robolectric


class ListProjectsViewModelTest{

    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMWIxZDJjNzZhNzA2MTBhNzNjMTFkM2NmOWUzMGU4MTkxYjY0MjRlY2VlMDc2YTAxYzMyNzIzYTM2OTBmMTY0NTJiZmM3OTk5MTJjNjgxZGEiLCJpYXQiOjE2NTU0ODUzNjAuMTU1NzMzLCJuYmYiOjE2NTU0ODUzNjAuMTU1NzM2LCJleHAiOjE2NTU1NzE3NjAuMDY3ODAzLCJzdWIiOiI1NCIsInNjb3BlcyI6W119.dD_rUD-bv1jKRmQCauUTjvsLloTQMPJ9jne7lbzm36BkJp7p80h6ycIUFkAoQ1ur_kJ9pb0U56d8fjAoEfu5l-iQynIiDhNmqeM_xidQm87XOls0ZauJUmBVGL5NKs8-cUr9j91dooBfd3GACeLpbnNJCV1qJQY5opIc_tRGrHpOP20Zby-jfZc8aqdwWz3w70T97HyVFQi9bzkAVL1R2stmZc52bybd8GiEQNqvHGVMHs6FG8Mt9ayo3J3w5c9_ZxKYclthDX1H455G2O99NHjPSvH0349PT__G9kt9h8KfGCU5SYLhO2jDxqvjOgDcCvL3paebHmDMrCUUEUYfcuJ81onUxwy7iRPi7zOQy3YH-xGAEtGjMxSO9kIv08sek2iBubkZZtkRAILyroI6FoeUIBXfVaRBeGPuIGFpaNrvpK5d0Yb070Tfeip5VzFGS5fhkELmApvOxE2NKAYFZc7m_MUsHRPlg989n-_fO_XLqK9e4wxAnxWzKIWDbaarHzbYMCgys0r8_R9LufDBZzxtKyRXTX9AY7tS3dTLrtElqQEyESTZGc_moIEPrShADFZYB6i3L5hIza2z45IKGI2jW-Gpn709yueKNfcWJ_FoOncsFMCdAuWS2fe3a4MHuZrrhgpOQeCX1znxgvRKseR1aS76OHT8vOukxriPhpg"
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    lateinit var instance:TimeTrackingRepository


    @Before
    fun setup(){
         instance = TimeTrackingRepository()
    }

    @ExperimentalCoroutinesApi
    @Test
    fun `project list is empty returns false`() {


        runBlockingTest  {
            val job = launch {
                val result = instance.listProjects("Bearer " + token,"1")
                assertThat(result.data.isEmpty()).isFalse()
            }
            job.cancel()
        }

    }


}