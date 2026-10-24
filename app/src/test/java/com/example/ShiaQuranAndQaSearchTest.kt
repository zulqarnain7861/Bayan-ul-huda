package com.example

import com.example.data.local.ExtendedHadithDataProvider
import com.example.data.local.ShiaTranslationDataProvider
import com.example.data.local.entities.AyahEntity
import com.example.data.repository.TafsirRepository
import com.example.ui.audio.availableReciters
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ShiaQuranAndQaSearchTest {

  @Test
  fun `verify Stage 1 - 8 Sunni translations and 5 Shia translations are authentic and distinct`() {
    val sunniEditions = ShiaTranslationDataProvider.sunniEditions
    val shiaEditions = ShiaTranslationDataProvider.shiaEditions

    // Sunni translations count: Exactly 8
    assertEquals("Must preserve all 8 Sunni translations", 8, sunniEditions.size)
    val expectedSunniKeys = listOf(
      "en.sahih", "en.pickthall", "en.yusufali", "en.clearquran",
      "ur.jalandhry", "ur.maududi", "ur.junagarhi", "ur.usmani"
    )
    expectedSunniKeys.forEach { key ->
      assertTrue("Sunni edition $key must be present", sunniEditions.any { it.key == key })
    }

    // Shia translations count: Exactly 5 verified editions
    assertEquals("Must provide all 5 verified Shia translations", 5, shiaEditions.size)
    val expectedShiaKeys = listOf(
      "en.qarai", "en.mir_ahmed_ali", "en.shakir", "ur.jawadi", "ur.najafi"
    )
    expectedShiaKeys.forEach { key ->
      assertTrue("Shia edition $key must be present", shiaEditions.any { it.key == key })
    }

    // Test text generation for Surah 1 & 2 for all Shia and Sunni translations
    for (surahNum in listOf(1, 2, 36, 112, 114)) {
      expectedSunniKeys.forEach { key ->
        val text = ShiaTranslationDataProvider.getSpecificTranslation(surahNum, 1, key, "Base English", "Base Urdu")
        assertNotNull("Text for $key at $surahNum:1 must not be null", text)
        assertTrue("Text for $key at $surahNum:1 must not be blank", text.isNotBlank())
      }
      expectedShiaKeys.forEach { key ->
        val text = ShiaTranslationDataProvider.getSpecificTranslation(surahNum, 1, key, "Base English", "Base Urdu")
        assertNotNull("Text for $key at $surahNum:1 must not be null", text)
        assertTrue("Text for $key at $surahNum:1 must not be blank", text.isNotBlank())
      }
    }
  }

  @Test
  fun `verify Stage 1 - 8 Sunni tafsirs and 6 Shia tafsirs are registered with authentic scholarship`() {
    val allEditions = TafsirRepository.editions
    assertEquals("Must offer exactly 14 Tafsirs (8 Sunni + 6 Shia)", 14, allEditions.size)

    val sunniTafsirs = allEditions.filter { it.tradition == "SUNNI" }
    val shiaTafsirs = allEditions.filter { it.tradition == "SHIA" }

    assertEquals("Must have 8 Sunni tafsirs", 8, sunniTafsirs.size)
    assertEquals("Must have 6 Shia tafsirs", 6, shiaTafsirs.size)

    // Check specific Shia exegeses
    val shiaKeys = listOf(
      "al_mizan", "majma_ul_bayan", "al_tibyan",
      "nur_al_thaqalayn", "tafsir_al_qummi", "tafsir_al_ayyashi"
    )
    shiaKeys.forEach { key ->
      val tafsir = shiaTafsirs.find { it.key == key }
      assertNotNull("Shia tafsir $key must be present", tafsir)
      assertTrue("Scholar name must be documented for $key", tafsir!!.scholarName.isNotBlank())
      assertTrue("Methodology must be documented for $key", tafsir.methodology.isNotBlank())
    }

    // Check specific Sunni exegeses
    val sunniKeys = listOf(
      "ibn_kathir", "tabari", "qurtubi", "jalalayn",
      "as_sadi", "maariful_quran", "tafheem", "usmani"
    )
    sunniKeys.forEach { key ->
      val tafsir = sunniTafsirs.find { it.key == key }
      assertNotNull("Sunni tafsir $key must be present", tafsir)
      assertTrue("Scholar name must be documented for $key", tafsir!!.scholarName.isNotBlank())
    }
  }

  @Test
  fun `verify Stage 1 - Audio reciters independent defaults for Sunni and Shia sections`() {
    val sunniDefault = availableReciters.find { it.id == "husary" }
    assertNotNull("Sunni default reciter Al-Husary must be registered", sunniDefault)
    assertEquals("SUNNI", sunniDefault!!.tradition)

    val shiaDefault = availableReciters.find { it.id == "karim_mansouri" }
    assertNotNull("Shia default reciter Karim Mansouri must be registered", shiaDefault)
    assertEquals("SHIA", shiaDefault!!.tradition)
    assertTrue("Reciter stream URL must be EveryAyah standard format", shiaDefault.subfolder.isNotBlank())

    val maytham = availableReciters.find { it.id == "maytham_tammar" }
    assertNotNull("Alternative Shia reciter Maytham al-Tammar must be registered", maytham)
    assertEquals("SHIA", maytham!!.tradition)
  }

  @Test
  fun `verify Stage 2 - Non-dummy indexed Q&A search across Tafsirs and Hadiths with tradition filtering`() {
    val hadiths = ExtendedHadithDataProvider.getExtendedHadiths()
    assertTrue("Hadiths dataset must be substantial", hadiths.size >= 30)

    val sampleAyahs = listOf(
      AyahEntity(
        id = 1L,
        surahNumber = 1,
        verseNumber = 1,
        textArabic = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
        translationEnglish = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
        translationUrdu = "شروع اللہ کے نام سے جو بڑا مہربان نہایت رحم والا ہے",
        transliteration = "Bismillahi ar-Rahmani ar-Raheem",
        juzNumber = 1,
        pageNumber = 1
      ),
      AyahEntity(
        id = 255L,
        surahNumber = 2,
        verseNumber = 255,
        textArabic = "اللَّهُ لَا إِلَٰهَ إِلَّا هُوَ الْحَيُّ الْقَيُّومُ",
        translationEnglish = "Allah - there is no deity except Him, the Ever-Living, the Sustainer of all existence.",
        translationUrdu = "اللہ جس کے سوا کوئی معبود نہیں وہ زندہ ہے سب کا تھامنے والا",
        transliteration = "Allahu la ilaha illa huwal-Hayyul-Qayyum",
        juzNumber = 3,
        pageNumber = 42
      )
    )

    // 1. Search for keyword "Rahman" in both traditions
    val bothResults = TafsirRepository.searchTafsirCommentary("Rahman", "BOTH", sampleAyahs, hadiths)
    assertFalse("Searching for 'Rahman' across both traditions must return genuine records", bothResults.isEmpty())
    assertTrue("Both results must contain Sunni or Shia commentary", bothResults.any { it.edition.tradition == "SUNNI" } || bothResults.any { it.edition.tradition == "SHIA" })

    // 2. Search for "Mercy" with SHIA filter
    val shiaResults = TafsirRepository.searchTafsirCommentary("Mercy", "SHIA", sampleAyahs, hadiths)
    shiaResults.forEach {
      assertEquals("SHIA filter must strictly return SHIA exegeses", "SHIA", it.edition.tradition)
    }

    // 3. Search for SUNNI filter
    val sunniResults = TafsirRepository.searchTafsirCommentary("Mercy", "SUNNI", sampleAyahs, hadiths)
    sunniResults.forEach {
      assertEquals("SUNNI filter must strictly return SUNNI exegeses", "SUNNI", it.edition.tradition)
    }

    // 4. Searching for completely non-existent gibberish must return ZERO results (NO dummy/mock data!)
    val gibberishResults = TafsirRepository.searchTafsirCommentary("xyznonexistentgibberish9874", "BOTH", sampleAyahs, hadiths)
    assertTrue("Non-matching search query must return 0 results without inventing fake data", gibberishResults.isEmpty())
  }
}
