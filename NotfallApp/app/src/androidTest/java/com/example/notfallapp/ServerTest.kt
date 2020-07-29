package com.example.notfallapp

import android.app.Service
import android.content.Context
import android.os.Handler
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.notfallapp.server.ServerApi
import com.google.android.gms.tasks.Tasks.await
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch
import javax.security.auth.callback.Callback


@RunWith(AndroidJUnit4::class)
class ServerTest {

    private lateinit var testContext: Context

    @Before
    fun setup(){
        testContext = InstrumentationRegistry.getInstrumentation().targetContext
        ServerApi.setContext(testContext)
    }

    @Test
    fun loginTest(){
        ServerApi.sendLogInDataToServer("sosapp", "gTN52PoeUQ")

        // 5 sec, wait that response from server
        Thread.sleep(5000)

        assertNotNull(ServerApi.accessToken)
    }
}