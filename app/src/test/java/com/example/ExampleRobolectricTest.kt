package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.EntelPackageCatalog
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Megas Entel", appName)
  }

  @Test
  fun `verify star package ussd code`() {
    val star = EntelPackageCatalog.STAR_PACKAGE
    assertEquals("*10*1*1*1*1#", star.ussdCode)
    assertEquals(200, star.megasAmount)
    assertEquals(2.0, star.priceBs, 0.001)
    assertEquals(24, star.validityHours)
  }
}
