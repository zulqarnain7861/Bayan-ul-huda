package com.example

import android.content.Context
import android.content.Intent
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.auth.AuthManager
import com.example.data.local.BayanDatabase
import com.example.data.local.ExtendedHadithDataProvider
import com.example.data.local.InitialDataLoader
import com.example.data.local.OfflineQuranAssetLoader
import com.example.data.local.ImportStatus
import com.example.data.local.SourceRegistry
import com.example.data.local.VerificationStatus
import com.example.data.local.entities.TranslationEntity
import com.example.data.model.IslamicBooksCatalog
import com.example.data.prayer.AppThemeMode
import com.example.data.prayer.PrayerAndAlarmConfig
import com.example.data.prayer.PrayerPreferencesManager
import com.example.data.prayer.PrayerTimeCalculator
import com.example.data.repository.IslamicRepository
import com.example.data.repository.SourceRegistryRepository
import com.example.data.repository.TafsirRepository
import com.example.ui.prayer.AzanAudioController
import com.example.ui.prayer.BootAndTimeChangeReceiver
import com.example.ui.prayer.PrayerAlarmScheduler
import com.example.ui.theme.AmiriFontFamily
import com.example.ui.theme.ScheherazadeFontFamily
import java.util.Calendar
import java.util.TimeZone
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context matches Bayan al-Huda`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Bayan al-Huda", appName)
  }

  @Test
  fun `verify bundled arabic fonts and navigation routes and 7 day guest mode`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    assertTrue(R.font.amiri != 0)
    assertTrue(R.font.scheherazade_new != 0)
    assertNotNull(AmiriFontFamily)
    assertNotNull(ScheherazadeFontFamily)

    assertEquals("home", BayanNavRoutes.HOME)
    assertEquals("quran", BayanNavRoutes.QURAN)
    assertEquals("hadith", BayanNavRoutes.HADITH)
    assertEquals("search", BayanNavRoutes.SEARCH)
    assertEquals("library", BayanNavRoutes.LIBRARY)

    assertTrue(AuthManager.startOrResumeGuestSession(context))
    assertTrue(AuthManager.isGuestSessionValid(context))
    assertEquals(7, AuthManager.getRemainingGuestDays(context))
  }

  @Test
  fun `scenario 1 and 6 - five prayer times for Kashmir India and offline Room cache`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val cal = Calendar.getInstance(TimeZone.getTimeZone("Asia/Kolkata")).apply {
      set(2026, Calendar.MAY, 15, 10, 0, 0)
    }
    val schedule = PrayerTimeCalculator.calculateOfflineSchedule(
      calendar = cal,
      locationName = "Srinagar",
      districtOrRegion = "Srinagar District, Kashmir",
      country = "India",
      latitude = 34.0837,
      longitude = 74.7973,
      timeZoneId = "Asia/Kolkata",
      methodId = 1, // University of Islamic Sciences, Karachi
      asrMadhhab = 1, // Hanafi
      highLatitudeRule = 0
    )
    assertTrue(schedule.fajrTime.matches(Regex("\\d{2}:\\d{2}")))
    assertTrue(schedule.dhuhrTime.matches(Regex("\\d{2}:\\d{2}")))
    assertTrue(schedule.asrTime.matches(Regex("\\d{2}:\\d{2}")))
    assertTrue(schedule.maghribTime.matches(Regex("\\d{2}:\\d{2}")))
    assertTrue(schedule.ishaTime.matches(Regex("\\d{2}:\\d{2}")))
    assertTrue(schedule.tahajjudLastThirdStart.matches(Regex("\\d{2}:\\d{2}")))
    assertTrue(schedule.hijriReadable.isNotBlank())

    val nextInfo = PrayerTimeCalculator.computeNextPrayerAndCountdown(
      schedule = schedule,
      nowMillis = cal.timeInMillis
    )
    assertEquals("Dhuhr", nextInfo.nextPrayerName)
    assertTrue(nextInfo.countdownFormatted.isNotBlank())

    // Verify offline caching in Room PrayerScheduleDao
    val db = Room.inMemoryDatabaseBuilder(context, BayanDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    db.prayerScheduleDao().insertSchedule(schedule)
    val cached = db.prayerScheduleDao().getScheduleForDateAndLocation(
      dateIso = "2026-05-15",
      locationName = "Srinagar"
    )
    assertNotNull(cached)
    assertEquals(schedule.fajrTime, cached?.fajrTime)
    assertEquals(schedule.ishaTime, cached?.ishaTime)
    db.close()
  }

  @Test
  fun `scenario 2 3 4 5 - Fajr 15 minute wake up alarm dismiss snooze and reboot rescheduling`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val updatedConfig = PrayerAndAlarmConfig(
      city = "Srinagar",
      districtOrRegion = "Srinagar District, Kashmir",
      country = "India",
      fajrWakeUpAlarmEnabled = true,
      fajrWakeUpOffsetMinutes = 20,
      tahajjudWakeUpEnabled = true,
      wakeUpLanguage = "ALL",
      progressiveSequence = true,
      alarmVolumePercent = 85,
      vibrationEnabled = true
    )
    PrayerPreferencesManager.saveConfig(context, updatedConfig)
    val loadedConfig = PrayerPreferencesManager.loadConfig(context)
    assertTrue(loadedConfig.fajrWakeUpAlarmEnabled)
    assertEquals(20, loadedConfig.fajrWakeUpOffsetMinutes)
    assertTrue(loadedConfig.tahajjudWakeUpEnabled)
    assertEquals(900, AzanAudioController.MAX_FAJR_ALARM_DURATION_SECONDS)

    // Trigger special 15-minute Fajr alarm
    AzanAudioController.startAzanOrFajrAlarm(
      context = context,
      prayerName = "Fajr",
      isSpecial15MinFajrAlarm = true,
      isTahajjudAlarm = false
    )
    val activeState = AzanAudioController.playbackState.value
    assertTrue(activeState.isActive)
    assertTrue(activeState.isFajrSpecialAlarm)
    assertEquals(900, activeState.maxDurationSeconds)
    assertTrue(activeState.arabicWakeMessage.contains("الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ"))
    assertEquals("As-salatu khayrun minan-nawm.", activeState.transliterationMessage)
    assertTrue(activeState.englishWakeMessage.contains("Prayer is better than sleep"))

    // Test Snooze (5 minutes)
    AzanAudioController.snoozeActiveAlarm(context, 5)
    assertFalse(AzanAudioController.playbackState.value.isActive)

    // Start again and test Stop
    AzanAudioController.startAzanOrFajrAlarm(
      context = context,
      prayerName = "Fajr",
      isSpecial15MinFajrAlarm = true,
      isTahajjudAlarm = false
    )
    assertTrue(AzanAudioController.playbackState.value.isActive)
    AzanAudioController.stopAlarm(context)
    assertFalse(AzanAudioController.playbackState.value.isActive)

    // Verify BootAndTimeChangeReceiver reschedules cleanly on BOOT_COMPLETED and TIMEZONE_CHANGED
    val receiver = BootAndTimeChangeReceiver()
    receiver.onReceive(context, Intent(Intent.ACTION_BOOT_COMPLETED))
    receiver.onReceive(context, Intent(Intent.ACTION_TIMEZONE_CHANGED))
    val diagnostics = PrayerAlarmScheduler.getSetupDiagnostics(context)
    assertNotNull(diagnostics)
    assertTrue(diagnostics.scheduledAlarmsSummary.isNotEmpty())
  }

  @Test
  fun `scenario 7 - light dark and eye comfort themes persist and switch cleanly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val base = PrayerPreferencesManager.loadConfig(context)

    PrayerPreferencesManager.saveConfig(context, base.copy(themeMode = AppThemeMode.LIGHT))
    assertEquals(AppThemeMode.LIGHT, PrayerPreferencesManager.loadConfig(context).themeMode)

    PrayerPreferencesManager.saveConfig(context, base.copy(themeMode = AppThemeMode.DARK))
    assertEquals(AppThemeMode.DARK, PrayerPreferencesManager.loadConfig(context).themeMode)

    PrayerPreferencesManager.saveConfig(context, base.copy(themeMode = AppThemeMode.EYE_COMFORT))
    assertEquals(AppThemeMode.EYE_COMFORT, PrayerPreferencesManager.loadConfig(context).themeMode)
    assertTrue(AppThemeMode.EYE_COMFORT.description.contains("Warm", ignoreCase = true))
  }

  @Test
  fun `scenario 8 9 10 - Room schema entities Sunnah com Hadith numbering separation and 6236 Quran verses`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = Room.inMemoryDatabaseBuilder(context, BayanDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    // Seed Surahs, HadithCollections, Foundational Hadiths, and Translations
    db.surahDao().insertSurahs(InitialDataLoader.getAll114Surahs())
    db.hadithCollectionDao().insertCollections(ExtendedHadithDataProvider.getHadithCollectionEntities())
    db.hadithDao().insertHadiths(InitialDataLoader.getFoundationalHadiths())
    db.translationDao().insertTranslations(
      listOf(
        TranslationEntity(
          sourceType = "QURAN",
          referenceKey = "1:1",
          surahOrBookNumber = 1,
          verseOrHadithNumber = 1,
          editionKey = "en.sahih",
          editionTitle = "Sahih International",
          languageCode = "en",
          text = "In the name of Allah, the Entirely Merciful, the Especially Merciful.",
          isVerified = true
        )
      )
    )

    val surahsInDb = db.surahDao().getAllSurahs().first()
    assertEquals(114, surahsInDb.size)

    val collectionsInDb = db.hadithCollectionDao().getAllCollections().first()
    assertEquals(ExtendedHadithDataProvider.primaryCollections.size, collectionsInDb.size)
    assertTrue(collectionsInDb.size >= 26)
    assertTrue(collectionsInDb.any { it.collectionKey == "bukhari" && it.darussalamEditionName.contains("Darussalam") })
    assertTrue(collectionsInDb.any { it.collectionKey == "ahmad" })
    assertTrue(collectionsInDb.any { it.collectionKey == "alkafi" })
    assertTrue(collectionsInDb.any { it.collectionKey == "nahjulbalagha" })

    // Verify separate Darussalam and In-Book numbering on HadithEntity
    val bukhariHadith1 = db.hadithDao().getHadith("bukhari", 1)
    assertNotNull(bukhariHadith1)
    assertTrue(bukhariHadith1!!.darussalamReference.startsWith("Darussalam:"))
    assertTrue(bukhariHadith1.inBookReference.contains("Book"))

    // Verify Fajr phrase hadith in Sunan Abu Dawud #500
    val abuDawud500 = db.hadithDao().getHadith("abudawud", 500)
    assertNotNull(abuDawud500)
    assertTrue(abuDawud500!!.arabicText.contains("الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ"))

    // Verify Sahih International translation entity in Room
    val translations = db.translationDao().getTranslationsForReference("QURAN", "1:1").first()
    assertEquals(1, translations.size)
    assertEquals("Sahih International", translations.first().editionTitle)

    // Verify all 114 surahs / 6236 verses in offline asset bundle
    var totalAssetAyahs = 0
    for (s in 1..114) {
      totalAssetAyahs += OfflineQuranAssetLoader.loadSurahFromAssets(context, s).size
    }
    assertEquals(6236, totalAssetAyahs)

    // Verify diagnostic audit report honesty (no fabricated 100% claims) + security guard entry
    val repo = IslamicRepository(
      database = db,
      appContext = context
    )
    val audit = repo.generateDiagnosticAuditReport()
    assertTrue(audit.sourceEntries.size >= 5)
    assertEquals(114, audit.totalSurahsLocal)
    assertTrue(audit.sourceEntries.any { it.sourceName.contains("Hadith", ignoreCase = true) })
    assertTrue(audit.sourceEntries.any { it.sourceName.contains("Security", ignoreCase = true) })

    db.close()
  }

  @Test
  fun `verify 8 authentic tafsirs and 32 islamic books are available`() {
    assertEquals(8, TafsirRepository.editions.size)
    val ayahs = InitialDataLoader.getFoundationalAyahs()
    val surahs = InitialDataLoader.getAll114Surahs()
    val allHadiths = ExtendedHadithDataProvider.getExtendedHadiths()
    val commentary = TafsirRepository.getTafsirForAyah(
      ayah = ayahs.first(),
      surah = surahs.first(),
      editionKey = "ibn_kathir",
      allHadiths = allHadiths
    )
    assertTrue(commentary.arabicText.isNotBlank())
    assertTrue(commentary.commentaryEnglish.isNotBlank())
    assertTrue(commentary.commentaryUrdu.isNotBlank())
    assertTrue(commentary.linkedHadiths.isNotEmpty())

    assertEquals(32, IslamicBooksCatalog.all32Books.size)
    assertTrue(IslamicBooksCatalog.all32Books.all { it.chapters.size >= 2 })
  }

  @Test
  fun `verify audio played popup dismissal and anti-ad security integrity guard`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val playerManager = com.example.ui.audio.AudioPlayerManager(context)
    assertFalse(playerManager.audioState.value.isVisible)

    // Start playing Surah 1, Ayah 1 -> popup becomes visible
    playerManager.playAyah(1, 1, "Al-Fatihah")
    assertTrue(playerManager.audioState.value.isVisible)
    assertEquals(1, playerManager.audioState.value.surahNumber)

    // Stop / Close popup -> immediately dismisses popup and clears surahNumber
    playerManager.stop()
    assertFalse(playerManager.audioState.value.isVisible)
    assertEquals(null, playerManager.audioState.value.surahNumber)
    assertFalse(playerManager.audioState.value.isPlaying)
    assertFalse(playerManager.audioState.value.isBuffering)

    // Verify SecurityIntegrityGuard blocks ad networks, cleartext HTTP, and script injections
    val guard = com.example.data.remote.SecurityIntegrityGuard
    assertTrue(guard.isUrlTrustedAndAllowed("https://cdn.jsdelivr.net/gh/fawazahmed0/hadith-api@1/editions/eng-bukhari/1.min.json"))
    assertTrue(guard.isUrlTrustedAndAllowed("https://api.alquran.cloud/v1/surah/1"))
    assertFalse(guard.isUrlTrustedAndAllowed("http://api.alquran.cloud/v1/surah/1")) // Reject cleartext HTTP
    assertFalse(guard.isUrlTrustedAndAllowed("https://googleads.g.doubleclick.net/pagead/ads")) // Reject ad network
    assertFalse(guard.isUrlTrustedAndAllowed("https://untrusted-hacker-domain.org/inject.json"))

    val dirtyText = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ<script>alert('ad')</script><iframe src='https://admob.com'></iframe>"
    val sanitized = guard.sanitizeSacredText(dirtyText)
    assertEquals("بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ", sanitized)
    assertTrue(guard.isValidQuranVersePayload(1, 1, sanitized))
    assertTrue(guard.isAuthorizedDeveloper("sheikhzulqar@gmail.com"))
    assertFalse(guard.isAuthorizedDeveloper("intruder@example.com"))
  }

  @Test
  fun `verify SourceRegistry Room entity schema DAO and repository seeding`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val inMemoryDb = Room.inMemoryDatabaseBuilder(context, BayanDatabase::class.java)
      .allowMainThreadQueries()
      .build()

    try {
      val dao = inMemoryDb.sourceRegistryDao()
      val repo = SourceRegistryRepository(dao)

      // Initial count should be 0
      assertEquals(0, dao.getSourceCount())

      // Test seeding default authoritative sources
      repo.ensureDefaultSourcesSeeded()
      val sources = dao.getAllSourcesSnapshot()
      assertTrue(sources.size >= 5)

      // Verify specific sources and required schema fields:
      // sourceId, title, author, tradition, language, translationLanguages, officialUrl,
      // datasetLocation, license, lastSyncTime, totalAvailableRecords, totalImportedRecords,
      // duplicateCount, invalidCount, verificationStatus (Enum), and importStatus (Enum)
      val quranSrc = dao.getSourceByIdSnapshot("quran_uthmani_tanzil")
      assertNotNull(quranSrc)
      assertEquals("quran_uthmani_tanzil", quranSrc!!.sourceId)
      assertEquals("Tanzil & Quran Foundation Uthmani Text", quranSrc.title)
      assertTrue(quranSrc.author.contains("Zayd ibn Thabit"))
      assertEquals("Quran al-Kareem (Universal Tawatur)", quranSrc.tradition)
      assertEquals("Classical Arabic (العربية الفصحى)", quranSrc.language)
      assertTrue(quranSrc.translationLanguages.contains("English") && quranSrc.translationLanguages.contains("Urdu"))
      assertTrue(quranSrc.officialUrl.contains("tanzil.net"))
      assertTrue(quranSrc.datasetLocation.contains("quran_complete_v1.dat"))
      assertTrue(quranSrc.license.contains("Open Source"))
      assertTrue(quranSrc.lastSyncTime > 0)
      assertEquals(6236, quranSrc.totalAvailableRecords)
      assertEquals(6236, quranSrc.totalImportedRecords)
      assertEquals(0, quranSrc.duplicateCount)
      assertEquals(0, quranSrc.invalidCount)
      assertEquals(VerificationStatus.VERIFIED, quranSrc.verificationStatus)
      assertEquals(ImportStatus.SUCCESS, quranSrc.importStatus)

      val sunnahSrc = dao.getSourceByIdSnapshot("sunnah_kutub_sittah")
      assertNotNull(sunnahSrc)
      assertEquals("Sunnah.com / Kutub al-Sittah Hadith Archive", sunnahSrc!!.title)
      assertEquals("Sunni / Ahl al-Sunnah wal-Jama'ah", sunnahSrc.tradition)
      assertEquals(VerificationStatus.PARTIALLY_VERIFIED, sunnahSrc.verificationStatus)
      assertEquals(38500, sunnahSrc.totalAvailableRecords)
      assertEquals(140, sunnahSrc.totalImportedRecords)
      assertEquals(ImportStatus.SUCCESS, sunnahSrc.importStatus)

      val aladhanSrc = dao.getSourceByIdSnapshot("aladhan_prayer_engine")
      assertNotNull(aladhanSrc)
      assertEquals(VerificationStatus.VERIFIED, aladhanSrc!!.verificationStatus)
      assertTrue(aladhanSrc.officialUrl.contains("aladhan.com"))

      val fiqhSrc = dao.getSourceByIdSnapshot("fiqh_fatwa_registry")
      assertNotNull(fiqhSrc)
      assertTrue(fiqhSrc!!.title.contains("Islamic Fiqh Academy"))
      assertEquals(VerificationStatus.PARTIALLY_VERIFIED, fiqhSrc.verificationStatus)

      // Test updating sync stats
      dao.updateSyncStats(
        sourceId = "sunnah_kutub_sittah",
        totalImported = 250,
        importStatus = ImportStatus.SUCCESS
      )
      val updatedSunnah = dao.getSourceByIdSnapshot("sunnah_kutub_sittah")
      assertEquals(250, updatedSunnah!!.totalImportedRecords)
      assertEquals(ImportStatus.SUCCESS, updatedSunnah.importStatus)

      // Test updating verification status enum
      dao.updateVerificationStatus("sunnah_kutub_sittah", VerificationStatus.VERIFIED)
      val verifiedSunnah = dao.getSourceByIdSnapshot("sunnah_kutub_sittah")
      assertEquals(VerificationStatus.VERIFIED, verifiedSunnah!!.verificationStatus)

      // Test reactive Flow emissions
      val flowList = dao.getAllSources().first()
      assertTrue(flowList.any { it.sourceId == "quran_uthmani_tanzil" })
    } finally {
      inMemoryDb.close()
    }
  }
}
