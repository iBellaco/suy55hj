package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("AI Quota Widget", appName)
  }

  @Test
  fun `test quota repository initialization and usage increment`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val repo = com.example.data.QuotaRepository(context)
    val gemini = repo.getGeminiConfig()
    assertEquals("reqs", gemini.unit)
    
    val initialUsed = gemini.currentUsed
    repo.incrementUsage(com.example.model.ProviderType.GEMINI, 2.0, "Test Model")
    val updatedGemini = repo.getGeminiConfig()
    assertEquals(initialUsed + 2.0, updatedGemini.currentUsed, 0.001)
  }
}
