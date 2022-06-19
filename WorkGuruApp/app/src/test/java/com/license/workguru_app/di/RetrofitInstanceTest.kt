package com.license.workguru_app.di

import android.util.Log
import com.google.common.truth.Truth
import com.license.workguru_app.BuildConfig
import com.license.workguru_app.profile.data.remote.DTO.ProfileResponse
import com.license.workguru_app.profile.data.remote.ProfileApi
import com.license.workguru_app.profile.data.repository.ProfileRepository
import com.license.workguru_app.timetracking.data.remote.DTO.ProjectResponse
import com.license.workguru_app.timetracking.data.remote.TimeTrackingApi
import com.license.workguru_app.utils.Constants.BASE_URL
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
@RunWith(MockitoJUnitRunner::class)
class RetrofitInstanceTest{
    val token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhdWQiOiIxIiwianRpIjoiMWIxZDJjNzZhNzA2MTBhNzNjMTFkM2NmOWUzMGU4MTkxYjY0MjRlY2VlMDc2YTAxYzMyNzIzYTM2OTBmMTY0NTJiZmM3OTk5MTJjNjgxZGEiLCJpYXQiOjE2NTU0ODUzNjAuMTU1NzMzLCJuYmYiOjE2NTU0ODUzNjAuMTU1NzM2LCJleHAiOjE2NTU1NzE3NjAuMDY3ODAzLCJzdWIiOiI1NCIsInNjb3BlcyI6W119.dD_rUD-bv1jKRmQCauUTjvsLloTQMPJ9jne7lbzm36BkJp7p80h6ycIUFkAoQ1ur_kJ9pb0U56d8fjAoEfu5l-iQynIiDhNmqeM_xidQm87XOls0ZauJUmBVGL5NKs8-cUr9j91dooBfd3GACeLpbnNJCV1qJQY5opIc_tRGrHpOP20Zby-jfZc8aqdwWz3w70T97HyVFQi9bzkAVL1R2stmZc52bybd8GiEQNqvHGVMHs6FG8Mt9ayo3J3w5c9_ZxKYclthDX1H455G2O99NHjPSvH0349PT__G9kt9h8KfGCU5SYLhO2jDxqvjOgDcCvL3paebHmDMrCUUEUYfcuJ81onUxwy7iRPi7zOQy3YH-xGAEtGjMxSO9kIv08sek2iBubkZZtkRAILyroI6FoeUIBXfVaRBeGPuIGFpaNrvpK5d0Yb070Tfeip5VzFGS5fhkELmApvOxE2NKAYFZc7m_MUsHRPlg989n-_fO_XLqK9e4wxAnxWzKIWDbaarHzbYMCgys0r8_R9LufDBZzxtKyRXTX9AY7tS3dTLrtElqQEyESTZGc_moIEPrShADFZYB6i3L5hIza2z45IKGI2jW-Gpn709yueKNfcWJ_FoOncsFMCdAuWS2fe3a4MHuZrrhgpOQeCX1znxgvRKseR1aS76OHT8vOukxriPhpg"
    @Test
    fun testRetrofitInstance() {
        //Get an instance of Retrofit
        val instance: Retrofit = RetrofitInstance.retrofit
        //Assert that, Retrofit's base url matches to our BASE_URL
        assert(instance.baseUrl().toUrl().toString() == BASE_URL)
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testTimeTrackingApi() {
        var result: ProjectResponse? = null
        val instance: TimeTrackingApi = RetrofitInstance.apiTimeTracking
        runBlockingTest {
            val job = launch {
                result = instance.listAllProjects("Bearer " + token,true, "1")
                Truth.assertThat(result?.data.isNullOrEmpty()).isTrue()
            }

            job.cancel()
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    fun testProfileApi() {
        var result: ProfileResponse? = null
        val instance: ProfileApi = RetrofitInstance.apiProfile
        runBlockingTest {
            val job = launch {
                result = instance.getUserProfile("Bearer " + token)
                Truth.assertThat(result?.data?.email.isNullOrEmpty()).isFalse()
            }
            job.cancel()
        }
    }
    @Test
    suspend fun `get profile details returns true`(){
        val mockProfileService = Mockito.mock(ProfileRepository::class.java)
        val result = mockProfileService.getUserProfile("Bearer " + token).data.avatar.isNullOrEmpty()
        Truth.assertThat(result).isTrue()

    }



}