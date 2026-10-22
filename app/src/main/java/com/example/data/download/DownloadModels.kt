package com.example.data.download

import com.example.data.local.entities.DownloadManifestStatus

/**
 * Distinguishes chapter-level audio reciters (QuranicAudio / MP3Quran endpoints)
 * from ayah-by-ayah reciters (EveryAyah endpoints), per Priority 11 Section B.
 */
data class OfflineAudioReciterSpec(
  val id: String,
  val displayName: String,
  val arabicName: String = "قارئ معتمد",
  val style: String,
  val bitrateKbps: Int = 128,
  val chapterReciterId: String,
  val chapterAudioBaseUrl: String,
  val ayahReciterId: String,
  val ayahAudioSubfolder: String,
  val averageChapterSizeBytes: Long = 14_500_000L,
  val averageAyahSizeBytes: Long = 48_000L,
  val licenceId: String = "everyayah_audio"
)

enum class AudioDownloadScopeMode(val label: String) {
  FULL_CHAPTER_AUDIO("Chapter Audio (Full Surah MP3)"),
  AYAH_BY_AYAH_AUDIO("Ayah-by-Ayah Audio (Verse MP3s)")
}

data class AyahRangeSelection(
  val surahNumber: Int,
  val startAyah: Int,
  val endAyah: Int
) {
  init {
    require(surahNumber in 1..114) { "Surah number must be between 1 and 114" }
    require(startAyah >= 1 && endAyah >= startAyah) { "Invalid ayah range $startAyah..$endAyah" }
  }
}

/**
 * Available licensed translations with separate edition keys, translators, languages, and licence metadata
 * so downloading one translation never replaces another translation (Priority 11 Section A).
 */
data class LicensedTranslationSpec(
  val editionKey: String,
  val title: String,
  val translator: String,
  val languageCode: String,
  val languageName: String,
  val sourceProvider: String,
  val licenceId: String,
  val licenceText: String,
  val version: String,
  val estimatedSizeBytes: Long = 1_850_000L
)

data class LicensedTafsirSpec(
  val editionKey: String,
  val title: String,
  val author: String,
  val languageCode: String,
  val sourceProvider: String,
  val licenceId: String,
  val licenceText: String,
  val version: String,
  val estimatedSizeBytes: Long = 4_200_000L
)

/**
 * Priority 11 Section A — Quran Offline Download Request.
 */
data class QuranDownloadSelectionRequest(
  val includeCompleteArabicText: Boolean = false,
  val selectAll114Surahs: Boolean = false,
  val selectedSurahNumbers: Set<Int> = emptySet(),
  val specificAyahRange: AyahRangeSelection? = null,
  val selectedTranslationEditions: Set<String> = emptySet(),
  val selectedTafsirEditions: Set<String> = emptySet(),
  val includeNavigationMetadata: Boolean = true,
  val includeSearchIndex: Boolean = true,
  val includeSearchIndexes: Boolean = includeSearchIndex
)

/**
 * Priority 11 Section B — Separate Quran Audio Download Request.
 */
data class QuranAudioDownloadRequest(
  val reciter: OfflineAudioReciterSpec,
  val mode: AudioDownloadScopeMode,
  val selectAll114Surahs: Boolean = false,
  val selectedSurahNumbers: Set<Int> = emptySet(),
  val specificAyahRange: AyahRangeSelection? = null
)

enum class HadithDownloadScopeType(val label: String) {
  SINGLE_NARRATION("Individual Narration"),
  COMPLETE_CHAPTER("Complete Chapter / Book"),
  SELECTED_CHAPTERS("Selected Chapters"),
  COMPLETE_COLLECTION("Eligible Complete Collection")
}

/**
 * Priority 11 Section C — Hadith Offline Download Request (Sunni & Shia).
 */
data class HadithDownloadSelectionRequest(
  val collectionKey: String,
  val collectionTitle: String,
  val tradition: String, // "SUNNI" or "SHIA"
  val scopeType: HadithDownloadScopeType,
  val singleHadithNumber: Int? = null,
  val selectedChapters: Set<Int> = setOf(1),
  val includeArabicText: Boolean = true,
  val includeEnglishTranslation: Boolean = true,
  val includeUrduTranslation: Boolean = true,
  val includeBookAndChapterMetadata: Boolean = true,
  val includeLocalSearchIndex: Boolean = true,
  val includeAvailableAudio: Boolean = false
)

/**
 * Global Offline Mode & Sync status exposed to the top navigation bar (Priority 11 Section F).
 */
data class OfflineSyncBadgeState(
  val isOfflineMode: Boolean = false,
  val isNetworkAvailable: Boolean = true,
  val activeDownloadsCount: Int = 0,
  val queuedDownloadsCount: Int = 0,
  val completedPackagesCount: Int = 0,
  val failedPackagesCount: Int = 0,
  val usedOfflineBytes: Long = 0L,
  val storageLimitBytes: Long = 2L * 1024L * 1024L * 1024L,
  val availableDeviceBytes: Long = 8L * 1024L * 1024L * 1024L,
  val wifiOnlyEnabled: Boolean = true,
  val allowMobileData: Boolean = false,
  val statusMessage: String = "Online • Synced"
) {
  val usedOfflineStorageBytes: Long
    get() = usedOfflineBytes

  val configuredStorageLimitBytes: Long
    get() = storageLimitBytes

  val availableDeviceStorageBytes: Long
    get() = availableDeviceBytes

  val wifiOnlyDownloads: Boolean
    get() = wifiOnlyEnabled

  val allowMobileDataDownloads: Boolean
    get() = allowMobileData
}

/**
 * Result wrapper when opening content in Offline Mode (Priority 11 Section F).
 */
sealed class OfflineContentResult<out T> {
  data class Available<T>(
    val data: T,
    val sourcePackageId: String,
    val verifiedOffline: Boolean = true
  ) : OfflineContentResult<T>()

  data class NotAvailableOffline(
    val resourceIdentifier: String,
    val message: String = "This content is not available offline",
    val canDownload: Boolean = true,
    val suggestedPackageId: String = ""
  ) : OfflineContentResult<Nothing>()
}

/**
 * Pluggable transport response used by [DownloadManager] so real HTTP/Byte-Range transfers
 * and deterministic offline/test transfers share the exact 7-step integrity pipeline.
 */
data class TransferResponse(
  val httpStatusCode: Int,
  val payloadBytes: ByteArray,
  val supportsByteRange: Boolean = true,
  val resumedFromByteOffset: Long = 0L,
  val contentType: String = "application/json"
)

fun interface OfflineTransferTransport {
  suspend fun executeTransfer(
    url: String,
    resourceIdentifier: String,
    expectedFormat: String,
    existingPartialBytes: ByteArray?
  ): TransferResponse
}

object OfflineCatalogRegistry {

  val verifiedReciters: List<OfflineAudioReciterSpec>
    get() = audioReciters

  val audioReciters: List<OfflineAudioReciterSpec> = listOf(
    OfflineAudioReciterSpec(
      id = "husary",
      displayName = "Mahmoud Khalil Al-Husary",
      arabicName = "محمود خليل الحصري",
      style = "Murattal (Verified Tajweed Standard)",
      bitrateKbps = 128,
      chapterReciterId = "chapter_mahmoud_khalil_al-husaree",
      chapterAudioBaseUrl = "https://download.quranicaudio.com/quran/mahmoud_khalil_al-husaree",
      ayahReciterId = "ayah_Husary_128kbps",
      ayahAudioSubfolder = "Husary_128kbps",
      averageChapterSizeBytes = 15_200_000L,
      averageAyahSizeBytes = 52_000L
    ),
    OfflineAudioReciterSpec(
      id = "alafasy",
      displayName = "Mishary Rashid Alafasy",
      arabicName = "مشاري راشد العفاسي",
      style = "Murattal (128 kbps Verified)",
      bitrateKbps = 128,
      chapterReciterId = "chapter_mishaari_raashid_al_3afaasee",
      chapterAudioBaseUrl = "https://download.quranicaudio.com/quran/mishaari_raashid_al_3afaasee",
      ayahReciterId = "ayah_Alafasy_128kbps",
      ayahAudioSubfolder = "Alafasy_128kbps",
      averageChapterSizeBytes = 13_800_000L,
      averageAyahSizeBytes = 46_000L
    ),
    OfflineAudioReciterSpec(
      id = "abdulbasit",
      displayName = "Abdul Basit Abdus Samad",
      arabicName = "عبد الباسط عبد الصمد",
      style = "Murattal (192 kbps Classical)",
      bitrateKbps = 192,
      chapterReciterId = "chapter_abdul_basit_murattal",
      chapterAudioBaseUrl = "https://download.quranicaudio.com/quran/abdul_basit_murattal",
      ayahReciterId = "ayah_Abdul_Basit_Murattal_192kbps",
      ayahAudioSubfolder = "Abdul_Basit_Murattal_192kbps",
      averageChapterSizeBytes = 18_400_000L,
      averageAyahSizeBytes = 64_000L
    ),
    OfflineAudioReciterSpec(
      id = "shatri",
      displayName = "Abu Bakr Al-Shatri",
      arabicName = "أبو بكر الشاطري",
      style = "Murattal (128 kbps Haramain Style)",
      bitrateKbps = 128,
      chapterReciterId = "chapter_abu_bakr_ash-shaatree",
      chapterAudioBaseUrl = "https://download.quranicaudio.com/quran/abu_bakr_ash-shaatree",
      ayahReciterId = "ayah_Abu_Bakr_Ash-Shaatree_128kbps",
      ayahAudioSubfolder = "Abu_Bakr_Ash-Shaatree_128kbps",
      averageChapterSizeBytes = 13_100_000L,
      averageAyahSizeBytes = 44_000L
    )
  )

  val licensedTranslations: List<LicensedTranslationSpec> = listOf(
    LicensedTranslationSpec(
      editionKey = "en.sahih",
      title = "Sahih International",
      translator = "Sahih International (Al-Muntada Al-Islami)",
      languageCode = "en",
      languageName = "English",
      sourceProvider = "Tanzil / Quran Foundation Content Sync",
      licenceId = "sahih_international_en",
      licenceText = "Educational Non-Commercial Attribution Licence (Offline Permitted)",
      version = "v2.1-sync",
      estimatedSizeBytes = 1_820_000L
    ),
    LicensedTranslationSpec(
      editionKey = "en.pickthall",
      title = "The Meaning of the Glorious Koran",
      translator = "Mohammed Marmaduke Pickthall",
      languageCode = "en",
      languageName = "English",
      sourceProvider = "Tanzil Public Domain Archive",
      licenceId = "pickthall_en",
      licenceText = "Public Domain (Unrestricted Offline & Bulk Storage)",
      version = "v1.4-pd",
      estimatedSizeBytes = 1_910_000L
    ),
    LicensedTranslationSpec(
      editionKey = "en.yusufali",
      title = "The Holy Qur'an: Text, Translation and Commentary",
      translator = "Abdullah Yusuf Ali",
      languageCode = "en",
      languageName = "English",
      sourceProvider = "Tanzil Public Domain Archive",
      licenceId = "yusufali_en",
      licenceText = "Public Domain (Unrestricted Offline & Bulk Storage)",
      version = "v1.5-pd",
      estimatedSizeBytes = 2_050_000L
    ),
    LicensedTranslationSpec(
      editionKey = "ur.jalandhry",
      title = "Fateh Muhammad Jalandhry Urdu Translation",
      translator = "Maulana Fateh Muhammad Jalandhry",
      languageCode = "ur",
      languageName = "Urdu",
      sourceProvider = "Tanzil / Open Urdu Quran Corpus",
      licenceId = "jalandhry_ur",
      licenceText = "Open Educational Attribution Licence (Offline Permitted)",
      version = "v2.0-ur",
      estimatedSizeBytes = 2_240_000L
    ),
    LicensedTranslationSpec(
      editionKey = "ur.maududi",
      title = "Tafheem-ul-Quran (Urdu Translation)",
      translator = "Sayyid Abul Ala Maududi",
      languageCode = "ur",
      languageName = "Urdu",
      sourceProvider = "Islamic Publications / Tanzil Verified",
      licenceId = "maududi_ur",
      licenceText = "Attribution Required • Non-Commercial Offline Study Permitted",
      version = "v1.8-ur",
      estimatedSizeBytes = 2_480_000L
    ),
    LicensedTranslationSpec(
      editionKey = "en.clearquran",
      title = "The Clear Quran (Mustafa Khattab)",
      translator = "Dr. Mustafa Khattab",
      languageCode = "en",
      languageName = "English",
      sourceProvider = "Quran Foundation Content Sync (Book of Signs Foundation)",
      licenceId = "clear_quran_en",
      licenceText = "Attribution Required • Official Content Sync Offline Licence",
      version = "v2.0-sync",
      estimatedSizeBytes = 1_890_000L
    )
  )

  val licensedTafsirs: List<LicensedTafsirSpec> = listOf(
    LicensedTafsirSpec(
      editionKey = "ibn_kathir",
      title = "Tafsir Ibn Kathir (Abridged English)",
      author = "Hafiz Imad al-Din Ibn Kathir (d. 774 AH)",
      languageCode = "en",
      sourceProvider = "Quran Foundation Content Sync / Classical Exegesis Corpus",
      licenceId = "tafsir_ibn_kathir_en",
      licenceText = "Scholarly Attribution • Offline Study Permitted via Content Sync",
      version = "v1.3",
      estimatedSizeBytes = 4_600_000L
    ),
    LicensedTafsirSpec(
      editionKey = "en.ibnkathir",
      title = "Tafsir Ibn Kathir (Abridged English)",
      author = "Hafiz Imad al-Din Ibn Kathir (d. 774 AH)",
      languageCode = "en",
      sourceProvider = "Quran Foundation Content Sync / Classical Exegesis Corpus",
      licenceId = "tafsir_ibn_kathir_en",
      licenceText = "Scholarly Attribution • Offline Study Permitted via Content Sync",
      version = "v1.3",
      estimatedSizeBytes = 4_600_000L
    ),
    LicensedTafsirSpec(
      editionKey = "ar.jalalayn",
      title = "Tafsir al-Jalalayn (Classical Arabic)",
      author = "Jalal al-Din al-Mahalli & Jalal al-Din al-Suyuti",
      languageCode = "ar",
      sourceProvider = "Al-Tafsir Classical Public Domain",
      licenceId = "tafsir_jalalayn_ar",
      licenceText = "Public Domain Classical Heritage Text",
      version = "v1.1",
      estimatedSizeBytes = 3_900_000L
    ),
    LicensedTafsirSpec(
      editionKey = "ur.usmani",
      title = "Tafsir-e-Usmani (Urdu Exegesis Notes)",
      author = "Allama Shabbir Ahmad Usmani",
      languageCode = "ur",
      sourceProvider = "Classical Subcontinent Exegesis Archive",
      licenceId = "tafsir_usmani_ur",
      licenceText = "Public Domain / Open Scholarly Attribution",
      version = "v1.2",
      estimatedSizeBytes = 4_350_000L
    )
  )
}
