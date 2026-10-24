package com.example.ui.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.auth.AuthManager
import com.example.data.local.ExtendedHadithDataProvider
import com.example.data.local.entities.AyahEntity
import com.example.data.local.entities.BookmarkEntity
import com.example.data.local.entities.HadithCollectionEntity
import com.example.data.local.entities.HadithEntity
import com.example.data.local.entities.NoteEntity
import com.example.data.local.entities.PrayerScheduleEntity
import com.example.data.local.SourceRegistry
import com.example.data.local.entities.SurahEntity
import com.example.data.model.ReflectionDataProvider
import com.example.data.model.ReflectionItem
import com.example.data.prayer.AppThemeMode
import com.example.data.prayer.PrayerAndAlarmConfig
import com.example.data.prayer.PrayerPreferencesManager
import com.example.data.remote.CloudBookmark
import com.example.data.remote.CloudNote
import com.example.data.repository.CloudSyncRepository
import com.example.data.repository.DiagnosticAuditReport
import com.example.data.repository.IslamicRepository
import com.example.ui.audio.AudioPlayerManager
import com.example.ui.audio.AudioState
import com.example.ui.audio.Reciter
import com.example.ui.prayer.AzanAudioController
import com.example.ui.prayer.FajrAlarmPlaybackState
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainNavigationTab {
  HOME,
  QURAN,
  HADITH,
  DUAS,
  SEARCH,
  LIBRARY
}

enum class SearchCategory {
  ALL,
  QURAN,
  HADITH
}

enum class HadithEditionFilterMode(val label: String) {
  ALL_EDITIONS("All Editions"),
  DARUSSALAM("Darussalam #"),
  IN_BOOK("In-Book #"),
  SAHIH_INTERNATIONAL_QURAN("Sahih International (Quran Refs)")
}

data class BayanUiState(
  val currentTab: MainNavigationTab = MainNavigationTab.HOME,
  val isOpeningScreenVisible: Boolean = true,
  val isGuestModeActive: Boolean = false,
  val remainingGuestDays: Int = 7,
  // Appearance & Prayer Config
  val prayerConfig: PrayerAndAlarmConfig = PrayerAndAlarmConfig(),
  // Quran reader state
  val selectedSurah: SurahEntity? = null,
  val isReaderOpen: Boolean = false,
  val isReadingMode: Boolean = false,
  val isContinuousMushafFlow: Boolean = false,
  val highlightedVerseNumber: Int? = null,
  val showEnglish: Boolean = true,
  val showUrdu: Boolean = true,
  val showTransliteration: Boolean = true,
  val arabicFontSizeSp: Float = 24f,
  val translationFontSizeSp: Float = 15f,
  // Hadith state
  val selectedCollectionKey: String = "bukhari",
  val selectedEditionFilterMode: HadithEditionFilterMode = HadithEditionFilterMode.ALL_EDITIONS,
  val selectedHadithDetail: HadithEntity? = null,
  val isFetchingHadithByNumber: Boolean = false,
  val previewedQuranVerse: AyahEntity? = null,
  // Search state
  val searchQuery: String = "",
  val searchCategory: SearchCategory = SearchCategory.ALL,
  val isSearchingRemote: Boolean = false,
  // Daily insights
  val dailyAyah: AyahEntity? = null,
  val dailyHadith: HadithEntity? = null,
  val dailyReflection: ReflectionItem = ReflectionDataProvider.items.first(),
  // Reporting dialog
  val showErrorReportDialog: Boolean = false,
  val errorReportContext: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
class BayanViewModel(application: Application) : AndroidViewModel(application) {

  private val repository = IslamicRepository.getInstance(application)
  private val cloudRepository: CloudSyncRepository? = try {
    CloudSyncRepository.getInstance(application)
  } catch (e: Exception) {
    Log.w("BayanViewModel", "CloudSyncRepository unavailable in this environment", e)
    null
  }
  val audioPlayerManager = AudioPlayerManager(application)
  val duaAudioController = com.example.ui.audio.DuaAudioController(application, audioPlayerManager)
  val downloadManager = com.example.data.download.DownloadManager.getInstance(application)
  val evidenceEngine = com.example.data.evidence.EvidenceVerificationEngine(application)

  private val _sunnahRemindersState = MutableStateFlow(
    com.example.data.prayer.SunnahReminderManager.loadState(application)
  )
  val sunnahRemindersState: StateFlow<com.example.data.prayer.SunnahRemindersUiState> =
    _sunnahRemindersState.asStateFlow()

  val duaAudioState: StateFlow<com.example.ui.audio.DuaAudioPlaybackState> =
    duaAudioController.playbackState

  private val _uiState = MutableStateFlow(
    BayanUiState(
      isGuestModeActive = AuthManager.isGuestSessionValid(application),
      remainingGuestDays = AuthManager.getRemainingGuestDays(application),
      prayerConfig = PrayerPreferencesManager.loadConfig(application)
    )
  )
  val uiState: StateFlow<BayanUiState> = _uiState.asStateFlow()

  val audioState: StateFlow<AudioState> = audioPlayerManager.audioState
  val alarmPlaybackState: StateFlow<FajrAlarmPlaybackState> = AzanAudioController.playbackState

  // Today's Prayer Schedule
  private val _todayPrayerSchedule = MutableStateFlow<PrayerScheduleEntity?>(null)
  val todayPrayerSchedule: StateFlow<PrayerScheduleEntity?> = _todayPrayerSchedule.asStateFlow()

  // Developer Source & API Audit Report
  private val _auditReport = MutableStateFlow<DiagnosticAuditReport?>(null)
  val auditReport: StateFlow<DiagnosticAuditReport?> = _auditReport.asStateFlow()

  // Verified Source Registry Flow
  val sourceRegistryList: StateFlow<List<SourceRegistry>> = repository.getAllSourceRegistry()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Reactive Firebase Auth User
  val currentUser: StateFlow<FirebaseUser?> = AuthManager.authStateFlow()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

  // All 114 Surahs Flow
  val allSurahs: StateFlow<List<SurahEntity>> = repository.getAllSurahs()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Hadith Collections Metadata Flow
  val hadithCollections: StateFlow<List<HadithCollectionEntity>> = repository.getAllHadithCollections()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Ayahs for current Surah
  private val _currentAyahs = MutableStateFlow<List<AyahEntity>>(emptyList())
  val currentAyahs: StateFlow<List<AyahEntity>> = _currentAyahs.asStateFlow()

  // Hadiths for current collection
  private val _collectionHadiths = MutableStateFlow<List<HadithEntity>>(emptyList())
  val collectionHadiths: StateFlow<List<HadithEntity>> = _collectionHadiths.asStateFlow()

  // All cached Hadiths for Tafsir verse-to-hadith cross-linking
  val allHadithsForLinks: List<HadithEntity> = ExtendedHadithDataProvider.getExtendedHadiths()

  private var surahAyahsJob: Job? = null
  private var collectionHadithsJob: Job? = null
  private var searchJob: Job? = null

  private val cloudBookmarksFlow = currentUser.flatMapLatest { user ->
    if (user == null || cloudRepository == null) {
      flowOf(emptyList())
    } else {
      cloudRepository.getBookmarksFlow(user.uid).catch { emit(emptyList()) }
    }
  }

  private val cloudNotesFlow = currentUser.flatMapLatest { user ->
    if (user == null || cloudRepository == null) {
      flowOf(emptyList())
    } else {
      cloudRepository.getNotesFlow(user.uid).catch { emit(emptyList()) }
    }
  }

  // Bookmarks merged with Cloud Firestore
  val bookmarks: StateFlow<List<BookmarkEntity>> = combine(
    repository.getAllBookmarks(),
    cloudBookmarksFlow
  ) { localList, cloudList ->
    val byKey = LinkedHashMap<String, BookmarkEntity>()
    cloudList.forEach { cloud ->
      val key = cloud.id.ifEmpty { cloud.referenceKey }
      byKey[key] = BookmarkEntity(
        itemType = if (cloud.itemType == "AYAH") "QURAN" else "HADITH",
        itemKey = key,
        title = cloud.title,
        subtitle = cloud.translationSnippet,
        arabicSnippet = cloud.arabicSnippet,
        timestamp = cloud.createdAt?.time ?: System.currentTimeMillis()
      )
    }
    localList.forEach { local ->
      if (!byKey.containsKey(local.itemKey)) {
        byKey[local.itemKey] = local
      }
    }
    byKey.values.toList()
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Notes merged with Cloud Firestore
  val notes: StateFlow<List<NoteEntity>> = combine(
    repository.getAllNotes(),
    cloudNotesFlow
  ) { localList, cloudList ->
    val result = mutableListOf<NoteEntity>()
    val seenTitlesAndRefs = mutableSetOf<String>()
    cloudList.forEachIndexed { index, cloud ->
      val signature = "${cloud.referenceKey}|${cloud.title}"
      seenTitlesAndRefs.add(signature)
      val syntheticId = cloud.id.removePrefix("note_").toLongOrNull() ?: (100000L + index)
      result.add(
        NoteEntity(
          id = syntheticId,
          referenceKey = cloud.referenceKey,
          title = cloud.title,
          content = cloud.content,
          updatedAt = cloud.updatedAt?.time ?: System.currentTimeMillis()
        )
      )
    }
    localList.forEach { local ->
      val signature = "${local.referenceKey}|${local.title}"
      if (!seenTitlesAndRefs.contains(signature)) {
        result.add(local)
      }
    }
    result
  }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  // Search results
  private val _searchAyahResults = MutableStateFlow<List<AyahEntity>>(emptyList())
  val searchAyahResults: StateFlow<List<AyahEntity>> = _searchAyahResults.asStateFlow()

  private val _searchHadithResults = MutableStateFlow<List<HadithEntity>>(emptyList())
  val searchHadithResults: StateFlow<List<HadithEntity>> = _searchHadithResults.asStateFlow()

  init {
    viewModelScope.launch {
      repository.initializeDatabaseIfNeeded()
      loadDailyContent()
      loadCollectionHadiths("bukhari")
      refreshPrayerSchedule(forceRemote = false)
      com.example.data.prayer.SunnahReminderManager.scheduleAllSunnahReminders(getApplication())
      _sunnahRemindersState.value =
        com.example.data.prayer.SunnahReminderManager.ensureTodayCalculatedNotificationSentIfEnabled(getApplication())
      refreshAuditReport(checkNetworkLive = false)
    }
  }

  fun dismissOpeningScreen() {
    _uiState.value = _uiState.value.copy(isOpeningScreenVisible = false)
  }

  fun continueAsGuest() {
    val app = getApplication<Application>()
    val valid = AuthManager.startOrResumeGuestSession(app)
    val remaining = AuthManager.getRemainingGuestDays(app)
    _uiState.value = _uiState.value.copy(
      isGuestModeActive = valid,
      remainingGuestDays = remaining
    )
  }

  fun selectTab(tab: MainNavigationTab) {
    _uiState.value = _uiState.value.copy(currentTab = tab)
  }

  // Theme & Prayer Configuration
  fun updatePrayerAndAlarmConfig(newConfig: PrayerAndAlarmConfig) {
    val app = getApplication<Application>()
    PrayerPreferencesManager.saveConfig(app, newConfig)
    _uiState.value = _uiState.value.copy(prayerConfig = newConfig)
    refreshPrayerSchedule(forceRemote = false)
  }

  fun setThemeMode(mode: AppThemeMode) {
    val updated = _uiState.value.prayerConfig.copy(themeMode = mode)
    updatePrayerAndAlarmConfig(updated)
  }

  fun cycleThemeMode() {
    val entries = AppThemeMode.entries
    val currentIdx = entries.indexOf(_uiState.value.prayerConfig.themeMode).coerceAtLeast(0)
    val next = entries[(currentIdx + 1) % entries.size]
    setThemeMode(next)
  }

  fun refreshPrayerSchedule(forceRemote: Boolean = true) {
    viewModelScope.launch {
      val schedule = repository.getOrRefreshTodayPrayerSchedule(forceRemoteRefresh = forceRemote)
      _todayPrayerSchedule.value = schedule
    }
  }

  fun testAzanOrFajrAlarm(prayerName: String, isSpecialFajrAlarm: Boolean, muezzinOverrideId: String? = null) {
    AzanAudioController.startAzanOrFajrAlarm(
      context = getApplication(),
      prayerName = prayerName,
      isSpecial15MinFajrAlarm = isSpecialFajrAlarm,
      muezzinOverrideId = muezzinOverrideId
    )
  }

  fun testMuezzinAzan(muezzin: com.example.data.prayer.MuezzinAudioOption) {
    AzanAudioController.testMuezzinAzan(getApplication(), muezzin)
  }

  fun startContinuousVibrationTest() {
    AzanAudioController.startContinuousVibrationTest(getApplication())
  }

  fun stopContinuousVibration() {
    AzanAudioController.stopContinuousVibration(getApplication())
  }

  fun stopActiveAzanOrAlarm() {
    AzanAudioController.stopAlarm(getApplication())
  }

  fun snoozeActiveAzanOrAlarm(minutes: Int) {
    AzanAudioController.snoozeActiveAlarm(getApplication(), minutes)
  }

  fun refreshAuditReport(checkNetworkLive: Boolean = true) {
    viewModelScope.launch {
      _auditReport.value = repository.generateDiagnosticAuditReport(checkNetworkLive = checkNetworkLive)
    }
  }

  fun signOut() {
    viewModelScope.launch {
      val app = getApplication<Application>()
      AuthManager.signOut(app)
      _uiState.value = _uiState.value.copy(
        isGuestModeActive = false,
        remainingGuestDays = AuthManager.getRemainingGuestDays(app)
      )
    }
  }

  private suspend fun loadDailyContent() {
    val ayah = repository.getRandomAyah()
    val hadith = repository.getRandomHadith()
    _uiState.value = _uiState.value.copy(
      dailyAyah = ayah,
      dailyHadith = hadith
    )
  }

  // Quran Actions
  fun openSurahReader(surah: SurahEntity, highlightVerse: Int? = null) {
    _uiState.value = _uiState.value.copy(
      selectedSurah = surah,
      isReaderOpen = true,
      highlightedVerseNumber = highlightVerse,
      currentTab = MainNavigationTab.QURAN
    )
    surahAyahsJob?.cancel()
    surahAyahsJob = viewModelScope.launch {
      repository.ensureSurahAyahsLoaded(surah.number)
      repository.getAyahsBySurah(surah.number).collect { ayahs ->
        _currentAyahs.value = ayahs
      }
    }
  }

  /**
   * Opens or previews an exact Quran verse from a Sahih International cross-reference badge (e.g. "2:255").
   */
  fun openOrPreviewQuranReference(verseRef: String, openFullSurahReader: Boolean = false) {
    val clean = verseRef.trim().removePrefix("Quran").trim()
    val parts = clean.split(":")
    val sNum = parts.getOrNull(0)?.trim()?.toIntOrNull() ?: return
    val vNum = parts.getOrNull(1)?.trim()?.toIntOrNull() ?: 1

    viewModelScope.launch {
      val ayah = repository.getAyah(sNum, vNum)
      val surah = repository.getSurahByNumber(sNum)
      if (openFullSurahReader && surah != null) {
        _uiState.value = _uiState.value.copy(previewedQuranVerse = null)
        openSurahReader(surah, highlightVerse = vNum)
      } else {
        _uiState.value = _uiState.value.copy(previewedQuranVerse = ayah)
      }
    }
  }

  fun dismissPreviewedQuranVerse() {
    _uiState.value = _uiState.value.copy(previewedQuranVerse = null)
  }

  fun navigateToAdjacentSurah(offset: Int) {
    val current = _uiState.value.selectedSurah ?: return
    val targetNumber = (current.number + offset).coerceIn(1, 114)
    if (targetNumber == current.number) return
    val targetSurah = allSurahs.value.find { it.number == targetNumber }
    if (targetSurah != null) {
      openSurahReader(targetSurah)
    } else {
      viewModelScope.launch {
        repository.getSurahByNumber(targetNumber)?.let { openSurahReader(it) }
      }
    }
  }

  fun closeSurahReader() {
    _uiState.value = _uiState.value.copy(
      isReaderOpen = false,
      isReadingMode = false,
      highlightedVerseNumber = null
    )
    audioPlayerManager.stop()
  }

  fun toggleReadingMode() {
    _uiState.value = _uiState.value.copy(isReadingMode = !_uiState.value.isReadingMode)
  }

  fun setReadingMode(enabled: Boolean) {
    _uiState.value = _uiState.value.copy(isReadingMode = enabled)
  }

  fun toggleContinuousMushafFlow() {
    _uiState.value = _uiState.value.copy(isContinuousMushafFlow = !_uiState.value.isContinuousMushafFlow)
  }

  fun toggleEnglishTranslation() {
    _uiState.value = _uiState.value.copy(showEnglish = !_uiState.value.showEnglish)
  }

  fun toggleUrduTranslation() {
    _uiState.value = _uiState.value.copy(showUrdu = !_uiState.value.showUrdu)
  }

  fun toggleTransliteration() {
    _uiState.value = _uiState.value.copy(showTransliteration = !_uiState.value.showTransliteration)
  }

  fun setArabicFontSize(sp: Float) {
    _uiState.value = _uiState.value.copy(arabicFontSizeSp = sp.coerceIn(18f, 38f))
  }

  fun setTranslationFontSize(sp: Float) {
    _uiState.value = _uiState.value.copy(translationFontSizeSp = sp.coerceIn(12f, 22f))
  }

  // Audio Playback
  fun playAyahRecitation(ayah: AyahEntity, surahName: String) {
    audioPlayerManager.playAyah(ayah.surahNumber, ayah.verseNumber, surahName)
  }

  fun setReciter(reciter: Reciter) {
    audioPlayerManager.setReciter(reciter)
  }

  // Hadith Actions
  fun selectHadithCollection(collectionKey: String) {
    _uiState.value = _uiState.value.copy(
      selectedCollectionKey = collectionKey,
      selectedHadithDetail = null
    )
    loadCollectionHadiths(collectionKey)
  }

  fun selectHadithEditionFilterMode(mode: HadithEditionFilterMode) {
    _uiState.value = _uiState.value.copy(selectedEditionFilterMode = mode)
  }

  private fun loadCollectionHadiths(collectionKey: String) {
    collectionHadithsJob?.cancel()
    collectionHadithsJob = viewModelScope.launch {
      repository.getHadithsByCollection(collectionKey).collect { list ->
        _collectionHadiths.value = list
      }
    }
  }

  fun lookupHadithByNumber(hadithNumber: Int, collectionKeyOverride: String? = null) {
    val collectionKey = collectionKeyOverride ?: _uiState.value.selectedCollectionKey
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isFetchingHadithByNumber = true)
      val hadith = repository.findOrFetchHadithByNumber(collectionKey, hadithNumber)
      _uiState.value = _uiState.value.copy(
        isFetchingHadithByNumber = false,
        selectedHadithDetail = hadith
      )
      refreshAuditReport(checkNetworkLive = false)
    }
  }

  fun loadHadithBookSection(bookNumber: Int) {
    val collectionKey = _uiState.value.selectedCollectionKey
    viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isFetchingHadithByNumber = true)
      repository.loadHadithBookSectionFromApi(collectionKey, bookNumber)
      _uiState.value = _uiState.value.copy(isFetchingHadithByNumber = false)
      refreshAuditReport(checkNetworkLive = false)
    }
  }

  fun showHadithDetail(hadith: HadithEntity) {
    _uiState.value = _uiState.value.copy(selectedHadithDetail = hadith)
  }

  fun closeHadithDetail() {
    _uiState.value = _uiState.value.copy(selectedHadithDetail = null)
  }

  // Unified Smart Search (Offline 6,236 Quran Verses + Local & Live Sunnah.com Hadith Lookup)
  fun onSearchQueryChanged(query: String) {
    _uiState.value = _uiState.value.copy(searchQuery = query)
    searchJob?.cancel()
    if (query.isBlank()) {
      _searchAyahResults.value = emptyList()
      _searchHadithResults.value = emptyList()
      _uiState.value = _uiState.value.copy(isSearchingRemote = false)
      return
    }

    searchJob = viewModelScope.launch {
      _uiState.value = _uiState.value.copy(isSearchingRemote = true)
      val ayahs = repository.searchAyahsSmart(query)
      _searchAyahResults.value = ayahs

      val hadiths = repository.searchHadithsSmart(
        rawQuery = query,
        activeCollectionKey = _uiState.value.selectedCollectionKey
      )
      _searchHadithResults.value = hadiths
      _uiState.value = _uiState.value.copy(isSearchingRemote = false)
    }
  }

  fun setSearchCategory(category: SearchCategory) {
    _uiState.value = _uiState.value.copy(searchCategory = category)
  }

  // Bookmarks (Local + Cloud Firestore Sync)
  fun toggleBookmarkAyah(ayah: AyahEntity, surahName: String) {
    viewModelScope.launch {
      val key = "ayah_${ayah.surahNumber}_${ayah.verseNumber}"
      val exists = bookmarks.value.any { it.itemKey == key }
      val uid = currentUser.value?.uid
      if (exists) {
        repository.removeBookmark(key)
        if (uid != null && cloudRepository != null) {
          runCatching { cloudRepository.deleteBookmark(uid, key) }
        }
      } else {
        repository.toggleBookmarkAyah(ayah, surahName)
        if (uid != null && cloudRepository != null) {
          val cloudBookmark = CloudBookmark(
            itemType = "AYAH",
            referenceKey = "${ayah.surahNumber}:${ayah.verseNumber}",
            title = "$surahName ${ayah.surahNumber}:${ayah.verseNumber}",
            arabicSnippet = ayah.textArabic.take(1500),
            translationSnippet = ayah.translationEnglish.take(1500)
          )
          runCatching { cloudRepository.saveBookmark(uid, key, cloudBookmark) }
        }
      }
    }
  }

  fun toggleBookmarkHadith(hadith: HadithEntity) {
    viewModelScope.launch {
      val key = "hadith_${hadith.collectionKey}_${hadith.hadithNumber}"
      val exists = bookmarks.value.any { it.itemKey == key }
      val uid = currentUser.value?.uid
      if (exists) {
        repository.removeBookmark(key)
        if (uid != null && cloudRepository != null) {
          runCatching { cloudRepository.deleteBookmark(uid, key) }
        }
      } else {
        repository.toggleBookmarkHadith(hadith)
        if (uid != null && cloudRepository != null) {
          val cloudBookmark = CloudBookmark(
            itemType = "HADITH",
            referenceKey = "${hadith.collectionKey}_${hadith.hadithNumber}",
            title = "${hadith.collectionName} #${hadith.hadithNumber}",
            arabicSnippet = hadith.arabicText.take(1500),
            translationSnippet = hadith.englishText.take(1500)
          )
          runCatching { cloudRepository.saveBookmark(uid, key, cloudBookmark) }
        }
      }
    }
  }

  fun removeBookmarkByKey(key: String) {
    viewModelScope.launch {
      repository.removeBookmark(key)
      val uid = currentUser.value?.uid
      if (uid != null && cloudRepository != null) {
        runCatching { cloudRepository.deleteBookmark(uid, key) }
      }
    }
  }

  // Notes (Local + Cloud Firestore Sync)
  fun saveNote(refKey: String, title: String, content: String) {
    viewModelScope.launch {
      val cleanRef = refKey.ifBlank { "General" }.take(100)
      val cleanTitle = title.trim().take(200)
      val cleanContent = content.trim().take(5000)
      repository.saveNote(cleanRef, cleanTitle, cleanContent)

      val uid = currentUser.value?.uid
      if (uid != null && cloudRepository != null) {
        val noteId = "note_${System.currentTimeMillis()}"
        val itemType = if (cleanRef.contains("hadith", ignoreCase = true) ||
          cleanRef.contains("bukhari", ignoreCase = true) ||
          cleanRef.contains("muslim", ignoreCase = true) ||
          cleanRef.contains("nawawi", ignoreCase = true) ||
          cleanRef.contains("tirmidhi", ignoreCase = true) ||
          cleanRef.contains("dawud", ignoreCase = true)
        ) {
          "HADITH"
        } else {
          "AYAH"
        }
        val cloudNote = CloudNote(
          itemType = itemType,
          referenceKey = cleanRef,
          title = cleanTitle,
          content = cleanContent
        )
        runCatching { cloudRepository.saveNote(uid, noteId, cloudNote) }
      }
    }
  }

  fun deleteNote(id: Long) {
    viewModelScope.launch {
      repository.deleteNote(id)
      val uid = currentUser.value?.uid
      if (uid != null && cloudRepository != null) {
        runCatching { cloudRepository.deleteNote(uid, "note_$id") }
      }
    }
  }

  // Error reporting
  fun openErrorReportDialog(context: String = "General Content") {
    _uiState.value = _uiState.value.copy(
      showErrorReportDialog = true,
      errorReportContext = context
    )
  }

  fun closeErrorReportDialog() {
    _uiState.value = _uiState.value.copy(showErrorReportDialog = false)
  }

  // Daily Masnoon Duas & Friday Bath / Necessary Sunnah Reminders Actions
  fun playDuaVocal(dua: com.example.data.model.DailyDuaItem, preferQuranReciter: Boolean = false) {
    duaAudioController.playDuaVocal(dua, preferQuranReciter)
  }

  fun stopDuaAudio() {
    duaAudioController.stop()
  }

  fun setDuaAudioLanguageMode(mode: com.example.ui.audio.DuaAudioLanguageMode) {
    duaAudioController.setLanguageMode(mode)
  }

  fun toggleDuaAudioRepeatMode() {
    duaAudioController.toggleRepeatTarget()
  }

  fun toggleFavoriteDua(duaId: String) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.toggleFavoriteDua(getApplication(), duaId)
  }

  fun incrementDuaTasbih(duaId: String, reset: Boolean = false) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.incrementDuaTasbih(getApplication(), duaId, reset)
  }

  fun updateFridayBathReminder(enabled: Boolean, time24h: String) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.setFridayBathReminder(getApplication(), enabled, time24h)
  }

  fun toggleFridayChecklistItem(itemId: String) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.toggleFridayChecklistItem(getApplication(), itemId)
  }

  fun incrementFridaySalawat(delta: Int) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.incrementFridaySalawat(getApplication(), delta)
  }

  fun updateNecessaryReminder(reminderId: String, enabled: Boolean, time24h: String) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.updateReminderConfig(
        getApplication(),
        reminderId,
        enabled,
        time24h
      )
  }

  fun triggerTestSunnahReminder(spec: com.example.data.model.NecessaryReminderSpec) {
    com.example.data.prayer.SunnahReminderManager.triggerTestReminder(getApplication(), spec)
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.loadState(getApplication())
  }

  fun selectPreviewDayOfWeek(calendarDayOfWeek: Int) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.selectPreviewDayOfWeek(
        getApplication(),
        calendarDayOfWeek
      )
  }

  fun sendCalculatedDayNotificationNow(dayOfWeekOverride: Int? = null) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.postCalculatedDayReminderNotification(
        context = getApplication(),
        dayOfWeekOverride = dayOfWeekOverride
      )
  }

  fun updateDailyCalculatedReminder(enabled: Boolean, time24h: String) {
    _sunnahRemindersState.value =
      com.example.data.prayer.SunnahReminderManager.setDailyCalculatedReminder(
        getApplication(),
        enabled,
        time24h
      )
  }

  override fun onCleared() {
    super.onCleared()
    audioPlayerManager.stop()
    duaAudioController.stop()
  }
}
