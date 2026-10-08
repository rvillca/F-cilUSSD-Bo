package com.example

import android.content.Context
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.EntelPackageCatalog
import com.example.util.TelephonyHelper
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
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
    assertEquals("FácilUSSD Bo", appName)
  }

  @Test
  fun `verify star package ussd code`() {
    val star = EntelPackageCatalog.STAR_PACKAGE
    assertEquals("*10*1*1*1*1#", star.ussdCode)
    assertEquals(200, star.megasAmount)
    assertEquals(2.0, star.priceBs, 0.001)
    assertEquals(24, star.validityHours)
  }

  @Test
  fun `verify new packages and doble carga codes`() {
    val waPackage = EntelPackageCatalog.PACKAGE_350MB_WHATSAPP
    assertEquals("*10*1*1*2*1#", waPackage.ussdCode)
    assertEquals(350, waPackage.megasAmount)
    assertEquals(3.0, waPackage.priceBs, 0.001)

    val unlimited = EntelPackageCatalog.PACKAGE_UNLIMITED_24H
    assertEquals("*10*1*3*1*1#", unlimited.ussdCode)
    assertEquals(6.0, unlimited.priceBs, 0.001)

    val dobleCarga = EntelPackageCatalog.PROMO_DOBLE_CARGA
    assertEquals("*10*4*1#", dobleCarga.ussdCode)
  }

  @Test
  fun `verify entel bolivia mcc mnc detection`() {
    // MCC 736 (Bolivia) and MNC 02 or 002 (Entel)
    assertTrue(TelephonyHelper.isEntel("736", "02", "Entel"))
    assertTrue(TelephonyHelper.isEntel("736", "002", "Entel Bolivia"))
    assertTrue(TelephonyHelper.isEntel("736", "2", "ENTEL"))

    // Non-Entel operators in Bolivia (Viva is 01, Tigo is 03/04)
    assertFalse(TelephonyHelper.isEntel("736", "01", "VIVA"))
    assertFalse(TelephonyHelper.isEntel("736", "03", "Tigo"))
    assertFalse(TelephonyHelper.isEntel("724", "02", "Claro"))
  }

  @Test
  fun `verify safe ussd uri encoding of pound symbol`() {
    val rawCode1 = "*105#"
    val encoded1 = Uri.encode(rawCode1)
    assertEquals("*105%23", encoded1)

    val rawCode2 = "*10*1*1*1*1#"
    val encoded2 = Uri.encode(rawCode2)
    assertEquals("*10*1*1*1*1%23", encoded2)

    val rawCode3 = "*10*1*1*2*1#"
    val encoded3 = Uri.encode(rawCode3)
    assertEquals("*10*1*1*2*1%23", encoded3)

    val rawCode4 = "*10*1*3*1*1#"
    val encoded4 = Uri.encode(rawCode4)
    assertEquals("*10*1*3*1*1%23", encoded4)

    val rawCode5 = "*10*4*1#"
    val encoded5 = Uri.encode(rawCode5)
    assertEquals("*10*4*1%23", encoded5)
  }

  @Test
  fun `verify mandatory google play legal disclaimer`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val disclaimer = context.getString(R.string.disclaimer_text)
    val expected = "Descargo de responsabilidad: Esta aplicación es una utilidad independiente. No está afiliada, asociada, ni respaldada por Entel S.A. Los comandos USSD son funciones públicas provistas por el operador."
    assertEquals(expected, disclaimer)
  }
}
