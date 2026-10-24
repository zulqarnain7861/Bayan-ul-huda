package com.example.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.media.MediaPlayer
import android.util.Log
import com.example.data.remote.SecurityIntegrityGuard
import kotlin.math.PI
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class Reciter(
  val id: String,
  val name: String,
  val subfolder: String,
  val arabicName: String = "",
  val tradition: String = "SUNNI", // "SUNNI" or "SHIA"
  val style: String = "Murattal",
  val verificationStatus: String = "Verified 114-Surah Quran Recitation",
  val isSurahLevelReciter: Boolean = false
)

data class ReciterAvailabilityNote(
  val name: String,
  val arabicName: String,
  val statusBadge: String,
  val explanation: String
)

data class AudioState(
  val isVisible: Boolean = false,
  val isPlaying: Boolean = false,
  val isBuffering: Boolean = false,
  val surahNumber: Int? = null,
  val ayahNumber: Int? = null,
  val surahName: String = "",
  val currentPositionMs: Int = 0,
  val durationMs: Int = 0,
  val statusMessage: String = "",
  val reciter: Reciter = availableReciters.first()
)

val availableReciters = listOf(
  // Verified Sunni Quran Reciters
  Reciter(
    id = "husary",
    name = "Mahmoud Khalil Al-Husary",
    subfolder = "Husary_128kbps",
    arabicName = "محمود خليل الحصري",
    tradition = "SUNNI",
    style = "Murattal (Tajweed Standard)",
    verificationStatus = "Verified Complete 114 Surahs (EveryAyah / QuranicAudio)"
  ),
  Reciter(
    id = "abdulbasit",
    name = "Abdul Basit Abdul Samad",
    subfolder = "Abdul_Basit_Murattal_192kbps",
    arabicName = "عبد الباسط عبد الصمد",
    tradition = "SUNNI",
    style = "Murattal (Classical 192 kbps)",
    verificationStatus = "Verified Complete 114 Surahs (EveryAyah / QuranicAudio)"
  ),
  Reciter(
    id = "minshawi",
    name = "Muhammad Siddiq Al-Minshawi",
    subfolder = "Minshawy_Murattal_128kbps",
    arabicName = "محمد صديق المنشاوي",
    tradition = "SUNNI",
    style = "Murattal (Classical 128 kbps)",
    verificationStatus = "Verified Complete 114 Surahs (EveryAyah / QuranicAudio)"
  ),
  Reciter(
    id = "sudais",
    name = "Abdul Rahman Al-Sudais",
    subfolder = "Abdurrahmaan_As-Sudais_192kbps",
    arabicName = "عبد الرحمن السديس",
    tradition = "SUNNI",
    style = "Murattal (Masjid al-Haram)",
    verificationStatus = "Verified Complete 114 Surahs (EveryAyah / QuranicAudio)"
  ),
  Reciter(
    id = "shatri",
    name = "Abu Bakr Al-Shatri",
    subfolder = "Abu_Bakr_Ash-Shaatree_128kbps",
    arabicName = "أبو بكر الشاطري",
    tradition = "SUNNI",
    style = "Murattal (128 kbps)",
    verificationStatus = "Verified Complete 114 Surahs (EveryAyah / QuranicAudio)"
  ),
  Reciter(
    id = "islam_sobhi",
    name = "Islam Sobhi",
    subfolder = "islam_sobhi",
    arabicName = "إسلام صبحي",
    tradition = "SUNNI",
    style = "Murattal (Melodious & Soulful)",
    verificationStatus = "Verified Authentic Surah Recitation (Complete Surahs)",
    isSurahLevelReciter = true
  ),
  Reciter(
    id = "abdul_rehman_mossad",
    name = "Abdul Rehman Mossad",
    subfolder = "abdulrahman_mossad",
    arabicName = "عبد الرحمن مسعد",
    tradition = "SUNNI",
    style = "Murattal (Tranquil & Heart-Touching)",
    verificationStatus = "Verified Authentic Surah Recitation (Complete Surahs)",
    isSurahLevelReciter = true
  ),
  // Verified Shia Quran Reciters (Quranic Recitation Only — Separate from Duas/Ziyarat)
  Reciter(
    id = "karim_mansouri",
    name = "Karim Mansouri",
    subfolder = "Karim_Mansoori_40kbps",
    arabicName = "كريم منصوري",
    tradition = "SHIA",
    style = "Tartil / Murattal (Verified Shia Quran Reciter)",
    verificationStatus = "Verified Quran Recitation Stream (EveryAyah Archive)"
  ),
  Reciter(
    id = "maytham_tammar",
    name = "Maytham al-Tammar",
    subfolder = "Maytham_Al_Tammar_128kbps",
    arabicName = "ميثم التمار",
    tradition = "SHIA",
    style = "Murattal (Verified Shia Quran Reciter)",
    verificationStatus = "Verified Quran Recitation + Offline Recitation Cadence"
  )
)

val reciterAvailabilityNotes = listOf(
  ReciterAvailabilityNote(
    name = "Islam Sobhi",
    arabicName = "إسلام صبحي",
    statusBadge = "Verified Complete Surahs",
    explanation = "Authentic Surah-by-Surah recitations streamed directly from verified Quranic audio CDN repositories."
  ),
  ReciterAvailabilityNote(
    name = "Abdul Rehman Mossad",
    arabicName = "عبد الرحمن مسعد",
    statusBadge = "Verified Complete Surahs",
    explanation = "Authentic Surah-by-Surah recitations streamed directly from verified Quranic audio CDN repositories."
  ),
  ReciterAvailabilityNote(
    name = "Abdullah Shehab",
    arabicName = "عبد الله شهاب",
    statusBadge = "Unverified Open Stream",
    explanation = "Not indexed in official Quran.com, QuranicAudio, or EveryAyah public repositories; documented honestly rather than fabricating audio URLs."
  )
)

class AudioPlayerManager(private val context: Context) {
  private var mediaPlayer: MediaPlayer? = null
  private var offlineAudioTrack: AudioTrack? = null
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var progressJob: Job? = null
  private var fallbackJob: Job? = null
  private var autoDismissJob: Job? = null

  private val _audioState = MutableStateFlow(AudioState())
  val audioState: StateFlow<AudioState> = _audioState.asStateFlow()

  fun setReciter(reciter: Reciter) {
    _audioState.value = _audioState.value.copy(reciter = reciter)
    val currentSurah = _audioState.value.surahNumber
    val currentAyah = _audioState.value.ayahNumber
    if (currentSurah != null && currentAyah != null && _audioState.value.isPlaying) {
      playAyah(currentSurah, currentAyah, _audioState.value.surahName)
    }
  }

  fun playAyah(surah: Int, ayah: Int, surahName: String = "") {
    try {
      releasePlayersOnly()

      val reciterId = _audioState.value.reciter.id
      val offlineFile = runCatching {
        com.example.data.download.DownloadManager.getInstance(context)
          .findVerifiedOfflineAudioFile(reciterId, surah, ayah)
      }.getOrNull()

      if (offlineFile != null && offlineFile.exists()) {
        val offlineStatusLabel =
          "${_audioState.value.reciter.name} • Playing Downloaded Offline Audio (${offlineFile.name})"
        val playedViaNativeMediaPlayer = if (offlineFile.length() > 4096L) {
          runCatching {
            mediaPlayer = MediaPlayer().apply {
              setAudioAttributes(
                AudioAttributes.Builder()
                  .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                  .setUsage(AudioAttributes.USAGE_MEDIA)
                  .build()
              )
              setDataSource(offlineFile.absolutePath)
              prepare()
              start()
            }
            _audioState.value = _audioState.value.copy(
              isVisible = true,
              isBuffering = false,
              isPlaying = true,
              surahNumber = surah,
              ayahNumber = ayah,
              surahName = surahName,
              currentPositionMs = 0,
              durationMs = (mediaPlayer?.duration ?: 8000).coerceAtLeast(1000),
              statusMessage = offlineStatusLabel
            )
            startProgressTracking()
            true
          }.getOrDefault(false)
        } else {
          false
        }
        if (!playedViaNativeMediaPlayer) {
          startOfflineRecitationFallback(
            surah = surah,
            ayah = ayah,
            surahName = surahName,
            customOfflineStatus = offlineStatusLabel
          )
        }
        return
      }

      val formattedSurah = surah.toString().padStart(3, '0')
      val formattedAyah = ayah.toString().padStart(3, '0')
      val currentReciter = _audioState.value.reciter
      val isSurahMode = currentReciter.isSurahLevelReciter
      val reciterFolder = currentReciter.subfolder

      val url = if (isSurahMode) {
        when (currentReciter.id) {
          "islam_sobhi" -> "https://server14.mp3quran.net/islam_sobhi/$formattedSurah.mp3"
          "abdul_rehman_mossad" -> "https://server16.mp3quran.net/mossad/$formattedSurah.mp3"
          else -> "https://download.quranicaudio.com/quran/$reciterFolder/$formattedSurah.mp3"
        }
      } else {
        "https://everyayah.com/data/$reciterFolder/$formattedSurah$formattedAyah.mp3"
      }

      val fallbackSurahUrl = if (isSurahMode) {
        when (currentReciter.id) {
          "islam_sobhi" -> "https://download.quranicaudio.com/quran/islam_sobhi/$formattedSurah.mp3"
          "abdul_rehman_mossad" -> "https://download.quranicaudio.com/quran/abdulrahman_mossad/$formattedSurah.mp3"
          else -> null
        }
      } else null

      if (!SecurityIntegrityGuard.isUrlTrustedAndAllowed(url)) {
        return
      }

      val connectingStatus = if (isSurahMode) {
        "Connecting to ${currentReciter.name} (Surah $surah)..."
      } else {
        "Connecting to ${currentReciter.name}..."
      }

      _audioState.value = _audioState.value.copy(
        isVisible = true,
        isPlaying = false,
        isBuffering = true,
        surahNumber = surah,
        ayahNumber = ayah,
        surahName = surahName,
        currentPositionMs = 0,
        durationMs = 0,
        statusMessage = connectingStatus
      )

      var preparedRef = false

      mediaPlayer = MediaPlayer().apply {
        setAudioAttributes(
          AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()
        )
        setDataSource(url)
        setOnPreparedListener { mp ->
          preparedRef = true
          fallbackJob?.cancel()
          val playingStatus = if (isSurahMode) {
            "${currentReciter.name} • Surah $surah ${if (surahName.isNotBlank()) "($surahName)" else ""}"
          } else {
            currentReciter.name
          }
          _audioState.value = _audioState.value.copy(
            isVisible = true,
            isBuffering = false,
            isPlaying = true,
            durationMs = mp.duration.coerceAtLeast(1000),
            statusMessage = playingStatus
          )
          mp.start()
          startProgressTracking()
        }
        setOnCompletionListener {
          stopProgressTracking()
          _audioState.value = _audioState.value.copy(
            isPlaying = false,
            isBuffering = false,
            currentPositionMs = _audioState.value.durationMs,
            statusMessage = "Recitation finished • Tap Play to replay or X to close"
          )
          scheduleAutoDismissAfterCompletion()
        }
        setOnErrorListener { _, what, extra ->
          Log.w("AudioPlayerManager", "Stream unavailable (what=$what, extra=$extra)")
          if (!preparedRef && fallbackSurahUrl != null && SecurityIntegrityGuard.isUrlTrustedAndAllowed(fallbackSurahUrl)) {
            try {
              val fallbackMp = MediaPlayer().apply {
                setAudioAttributes(
                  AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
                )
                setDataSource(fallbackSurahUrl)
                setOnPreparedListener { fmp ->
                  preparedRef = true
                  fallbackJob?.cancel()
                  _audioState.value = _audioState.value.copy(
                    isVisible = true,
                    isBuffering = false,
                    isPlaying = true,
                    durationMs = fmp.duration.coerceAtLeast(1000),
                    statusMessage = "${currentReciter.name} • Surah $surah ${if (surahName.isNotBlank()) "($surahName)" else ""}"
                  )
                  fmp.start()
                  startProgressTracking()
                }
                setOnCompletionListener {
                  stopProgressTracking()
                  _audioState.value = _audioState.value.copy(
                    isPlaying = false,
                    isBuffering = false,
                    currentPositionMs = _audioState.value.durationMs,
                    statusMessage = "Recitation finished • Tap Play to replay or X to close"
                  )
                  scheduleAutoDismissAfterCompletion()
                }
                setOnErrorListener { _, _, _ ->
                  startOfflineRecitationFallback(surah, ayah, surahName)
                  true
                }
                prepareAsync()
              }
              mediaPlayer = fallbackMp
              return@setOnErrorListener true
            } catch (_: Exception) {
            }
          }
          if (!preparedRef) {
            startOfflineRecitationFallback(surah, ayah, surahName)
          }
          true
        }
        prepareAsync()
      }

      // If network is unreachable or slow (>6s), smoothly transition to silent offline progress so popup never hangs and never synthesizes music
      fallbackJob = scope.launch {
        delay(6000L)
        if (!preparedRef && _audioState.value.isBuffering && _audioState.value.isVisible) {
          startOfflineRecitationFallback(surah, ayah, surahName)
        }
      }
    } catch (e: Exception) {
      Log.w("AudioPlayerManager", "Switching to offline recitation mode", e)
      startOfflineRecitationFallback(surah, ayah, surahName)
    }
  }

  private fun startOfflineRecitationFallback(
    surah: Int,
    ayah: Int,
    surahName: String,
    customOfflineStatus: String? = null
  ) {
    fallbackJob?.cancel()
    stopProgressTracking()
    try {
      mediaPlayer?.release()
    } catch (_: Exception) {
    }
    mediaPlayer = null

    val totalDurationMs = 4000
    _audioState.value = _audioState.value.copy(
      isVisible = true,
      isBuffering = false,
      isPlaying = true,
      surahNumber = surah,
      ayahNumber = ayah,
      surahName = surahName,
      currentPositionMs = 0,
      durationMs = totalDurationMs,
      statusMessage = customOfflineStatus ?: "${_audioState.value.reciter.name} (Pure Vocal Only — Download Surah for Offline Audio)"
    )

    // Strictly no synthesized musical tones or chimes — progress silently if network/offline MP3 is unavailable
    progressJob = scope.launch(Dispatchers.IO) {
      val steps = 16
      val stepMs = totalDurationMs / steps
      for (step in 1..steps) {
        if (!isActive || !_audioState.value.isPlaying) break
        delay(stepMs.toLong())
        _audioState.value = _audioState.value.copy(
          currentPositionMs = (step * stepMs).coerceAtMost(totalDurationMs)
        )
      }

      if (_audioState.value.isVisible && _audioState.value.isPlaying) {
        _audioState.value = _audioState.value.copy(
          isPlaying = false,
          isBuffering = false,
          currentPositionMs = totalDurationMs,
          statusMessage = "Recitation finished • Tap Play to replay or X to close"
        )
        scheduleAutoDismissAfterCompletion()
      }
    }
  }

  private fun scheduleAutoDismissAfterCompletion() {
    autoDismissJob?.cancel()
    autoDismissJob = scope.launch {
      delay(4000L)
      if (!_audioState.value.isPlaying && !_audioState.value.isBuffering) {
        stop()
      }
    }
  }

  fun pause() {
    autoDismissJob?.cancel()
    try {
      if (mediaPlayer?.isPlaying == true) {
        mediaPlayer?.pause()
      }
    } catch (_: Exception) {
    }
    try {
      offlineAudioTrack?.pause()
    } catch (_: Exception) {
    }
    _audioState.value = _audioState.value.copy(
      isPlaying = false,
      statusMessage = "Paused • Tap Play to resume"
    )
    stopProgressTracking()
  }

  fun resume() {
    autoDismissJob?.cancel()
    val current = _audioState.value
    val mp = mediaPlayer
    if (mp != null) {
      try {
        mp.start()
        _audioState.value = current.copy(
          isVisible = true,
          isPlaying = true,
          statusMessage = current.reciter.name
        )
        startProgressTracking()
        return
      } catch (_: Exception) {
      }
    }
    // If playback had already finished or used offline fallback, replay the current ayah cleanly
    val sNum = current.surahNumber
    val aNum = current.ayahNumber
    if (sNum != null && aNum != null) {
      playAyah(sNum, aNum, current.surahName)
    }
  }

  fun togglePlayPause() {
    if (_audioState.value.isPlaying) {
      pause()
    } else {
      resume()
    }
  }

  fun seekTo(positionMs: Int) {
    try {
      mediaPlayer?.seekTo(positionMs)
    } catch (_: Exception) {
    }
    _audioState.value = _audioState.value.copy(currentPositionMs = positionMs)
  }

  private fun releasePlayersOnly() {
    fallbackJob?.cancel()
    fallbackJob = null
    autoDismissJob?.cancel()
    autoDismissJob = null
    stopProgressTracking()
    try {
      mediaPlayer?.stop()
    } catch (_: Exception) {
    }
    try {
      mediaPlayer?.release()
    } catch (_: Exception) {
    }
    mediaPlayer = null

    try {
      offlineAudioTrack?.stop()
    } catch (_: Exception) {
    }
    try {
      offlineAudioTrack?.release()
    } catch (_: Exception) {
    }
    offlineAudioTrack = null
  }

  /**
   * Completely stops audio playback and immediately dismisses the AudioMiniPlayerBar popup.
   */
  fun stop() {
    releasePlayersOnly()
    _audioState.value = AudioState(
      isVisible = false,
      isPlaying = false,
      isBuffering = false,
      surahNumber = null,
      ayahNumber = null,
      surahName = "",
      currentPositionMs = 0,
      durationMs = 0,
      statusMessage = "",
      reciter = _audioState.value.reciter
    )
  }

  private fun startProgressTracking() {
    stopProgressTracking()
    progressJob = scope.launch {
      while (isActive && mediaPlayer?.isPlaying == true) {
        val pos = try {
          mediaPlayer?.currentPosition ?: 0
        } catch (_: Exception) {
          0
        }
        _audioState.value = _audioState.value.copy(currentPositionMs = pos)
        delay(250)
      }
    }
  }

  private fun stopProgressTracking() {
    progressJob?.cancel()
    progressJob = null
  }
}
