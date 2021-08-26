package com.example.notfallapp

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.notfallapp.server.ServerApi
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class ServerTest {

    private lateinit var testContext: Context

    @Before
    fun setup(){
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
        ServerApi.setContext(testContext)
        login()
    }

    @Test
    fun loginTest(){
        assertNotNull(ServerApi.accessToken)
    }
    
    @Test
    fun recreateTokenTest(){
        val oldAccessToken = ServerApi.accessToken

        ServerApi.refreshToken()
        Thread.sleep(5000)

        assertNotEquals(oldAccessToken, ServerApi.accessToken)
    }
    
    private fun login(){
        ServerApi.setSharedPreferences(testContext.getSharedPreferences("Response", Context.MODE_PRIVATE))
        ServerApi.sendLogInDataToServer("sosapp", "gTN52PoeUQ", testContext)

        // 5 sec, wait for the response from server
        Thread.sleep(5000)
        assertNotNull(ServerApi.userId)
    }
}