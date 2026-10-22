package com.example.data.local

import android.content.Context
import android.util.Log
import com.example.data.local.dao.AyahDao
import com.example.data.local.dao.TranslationDao
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.TranslationEntity
import java.io.BufferedInputStream
import java.util.zip.GZIPInputStream
import org.json.JSONArray

object OfflineQuranAssetLoader {

  private const val ASSET_FILE_NAME = "quran_complete_v1.dat"

  private val LEADING_BISMILLAH_PREFIXES = listOf(
    "بِسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ",
    "بِّسْمِ ٱللَّهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ",
    "بِسْمِ ٱللَّٰهِ ٱلرَّحْمَٰنِ ٱلرَّحِيمِ ",
    "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ "
  )

  private val MUQATTAAT_SMALL_ALIF_FIXES = mapOf(
    10 to ("الٓر" to "الٓرٰ"),
    11 to ("الٓر" to "الٓرٰ"),
    12 to ("الٓر" to "الٓرٰ"),
    13 to ("الٓمٓر" to "الٓـمّٓرٰ"),
    14 to ("الٓر" to "الٓرٰ"),
    15 to ("الٓر" to "الٓرٰ"),
    19 to ("كٓهيعٓصٓ" to "كٓهٰيٰعٓصٓ"),
    20 to ("طه" to "طٰهٰ"),
    26 to ("طسٓمٓ" to "طٰسّٓمٓ"),
    27 to ("طسٓ" to "طٰسٓ"),
    28 to ("طسٓمٓ" to "طٰسّٓمٓ"),
    36 to ("يسٓ" to "يٰسٓ"),
    40 to ("حمٓ" to "حٰمٓ"),
    41 to ("حمٓ" to "حٰمٓ"),
    42 to ("حمٓ" to "حٰمٓ"),
    43 to ("حمٓ" to "حٰمٓ"),
    44 to ("حمٓ" to "حٰمٓ"),
    45 to ("حمٓ" to "حٰمٓ"),
    46 to ("حمٓ" to "حٰمٓ")
  )

  /**
   * Removes only the extra prepended Bismillah at the start of Verse 1 for Surahs 2..114 (excluding Surah 9 At-Tawbah),
   * because the upper Bismillah header is already displayed above the Surah, and ensures Uthmani Muqatta'at
   * small alif (e.g., الٓـمّٓرٰ in Surah Ar-Ra'd 13:1) is accurately preserved.
   */
  fun stripDuplicateBismillahPrefix(surahNumber: Int, verseNumber: Int, rawArabic: String): String {
    var cleaned = rawArabic.removePrefix("\uFEFF").trim()
    if (surahNumber != 1 && surahNumber != 9 && verseNumber == 1) {
      for (prefix in LEADING_BISMILLAH_PREFIXES) {
        if (cleaned.startsWith(prefix)) {
          val remainder = cleaned.substring(prefix.length).trim()
          if (remainder.isNotEmpty()) {
            cleaned = remainder
            break
          }
        }
      }
    }
    if (verseNumber == 1) {
      MUQATTAAT_SMALL_ALIF_FIXES[surahNumber]?.let { (oldTok, newTok) ->
        if (cleaned == oldTok) {
          cleaned = newTok
        } else if (cleaned.startsWith("$oldTok ")) {
          cleaned = newTok + cleaned.substring(oldTok.length)
        }
      }
    }
    return cleaned
  }

  suspend fun loadAll6236AyahsIntoRoom(
    context: Context,
    ayahDao: AyahDao,
    translationDao: TranslationDao? = null
  ): Boolean {
    return try {
      val rawBytes = context.assets.open(ASSET_FILE_NAME).use { it.readBytes() }
      val jsonText = if (rawBytes.size >= 2 &&
        rawBytes[0] == 0x1f.toByte() &&
        rawBytes[1] == 0x8b.toByte()
      ) {
        GZIPInputStream(BufferedInputStream(rawBytes.inputStream())).bufferedReader(Charsets.UTF_8).use {
          it.readText()
        }
      } else {
        String(rawBytes, Charsets.UTF_8)
      }

      val jsonArray = JSONArray(jsonText)
      val ayahBatch = ArrayList<AyahEntity>(600)
      val translationBatch = ArrayList<TranslationEntity>(1200)
      val shouldSeedTranslations = translationDao != null && translationDao.getTranslationCount() < 6000

      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        val surahNum = obj.getInt("s")
        val verseNum = obj.getInt("v")
        val arabic = stripDuplicateBismillahPrefix(surahNum, verseNum, obj.getString("ar"))
        val english = obj.getString("en")
        val urdu = obj.getString("ur")
        val translit = obj.optString("tr", "")

        ayahBatch.add(
          AyahEntity(
            surahNumber = surahNum,
            verseNumber = verseNum,
            juzNumber = obj.optInt("j", 1),
            pageNumber = obj.optInt("p", 1),
            textArabic = arabic,
            translationEnglish = english,
            translationUrdu = urdu,
            transliteration = translit
          )
        )

        if (shouldSeedTranslations) {
          val refKey = "$surahNum:$verseNum"
          translationBatch.add(
            TranslationEntity(
              sourceType = "QURAN",
              referenceKey = refKey,
              surahOrBookNumber = surahNum,
              verseOrHadithNumber = verseNum,
              editionKey = "en.sahih",
              editionTitle = "Sahih International",
              languageCode = "en",
              text = english
            )
          )
          translationBatch.add(
            TranslationEntity(
              sourceType = "QURAN",
              referenceKey = refKey,
              surahOrBookNumber = surahNum,
              verseOrHadithNumber = verseNum,
              editionKey = "ur.jalandhry",
              editionTitle = "Fateh Muhammad Jalandhry",
              languageCode = "ur",
              text = urdu
            )
          )
        }

        if (ayahBatch.size >= 500) {
          ayahDao.insertAyahs(ayahBatch.toList())
          ayahBatch.clear()
        }
        if (shouldSeedTranslations && translationBatch.size >= 1000) {
          translationDao?.insertTranslations(translationBatch.toList())
          translationBatch.clear()
        }
      }
      if (ayahBatch.isNotEmpty()) {
        ayahDao.insertAyahs(ayahBatch.toList())
        ayahBatch.clear()
      }
      if (shouldSeedTranslations && translationBatch.isNotEmpty()) {
        translationDao?.insertTranslations(translationBatch.toList())
        translationBatch.clear()
      }
      Log.i("OfflineQuranLoader", "Successfully loaded ${jsonArray.length()} offline Quran verses and translations into Room.")
      true
    } catch (e: Exception) {
      Log.w("OfflineQuranLoader", "Falling back to foundational ayahs: ${e.message}")
      false
    }
  }

  fun loadSurahFromAssets(context: Context, targetSurahNumber: Int): List<AyahEntity> {
    return try {
      val rawBytes = context.assets.open(ASSET_FILE_NAME).use { it.readBytes() }
      val jsonText = if (rawBytes.size >= 2 &&
        rawBytes[0] == 0x1f.toByte() &&
        rawBytes[1] == 0x8b.toByte()
      ) {
        GZIPInputStream(BufferedInputStream(rawBytes.inputStream())).bufferedReader(Charsets.UTF_8).use {
          it.readText()
        }
      } else {
        String(rawBytes, Charsets.UTF_8)
      }
      val jsonArray = JSONArray(jsonText)
      val list = mutableListOf<AyahEntity>()
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        if (obj.getInt("s") == targetSurahNumber) {
          val vNum = obj.getInt("v")
          list.add(
            AyahEntity(
              surahNumber = targetSurahNumber,
              verseNumber = vNum,
              juzNumber = obj.optInt("j", 1),
              pageNumber = obj.optInt("p", 1),
              textArabic = stripDuplicateBismillahPrefix(targetSurahNumber, vNum, obj.getString("ar")),
              translationEnglish = obj.getString("en"),
              translationUrdu = obj.getString("ur"),
              transliteration = obj.optString("tr", "")
            )
          )
        }
      }
      list
    } catch (e: Exception) {
      emptyList()
    }
  }
}
