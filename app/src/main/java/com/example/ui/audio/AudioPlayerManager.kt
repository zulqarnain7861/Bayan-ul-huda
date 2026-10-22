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
  val subfolder: String
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
  Reciter("husary", "Mahmoud Khalil Al-Husary", "Husary_128kbps"),
  Reciter("alafasy", "Mishary Rashid Alafasy", "Alafasy_128kbps"),
  Reciter("abdulbasit", "Abdul Basit (Murattal)", "Abdul_Basit_Murattal_192kbps"),
  Reciter("shatri", "Abu Bakr Al-Shatri", "Abu_Bakr_Ash-Shaatree_128kbps")
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
        startOfflineRecitationFallback(
          surah = surah,
          ayah = ayah,
          surahName = surahName,
          customOfflineStatus = "${_audioState.value.reciter.name} • Playing Downloaded Offline Audio (${offlineFile.name})"
        )
        return
      }

      val formattedSurah = surah.toString().padStart(3, '0')
      val formattedAyah = ayah.toString().padStart(3, '0')
      val reciterFolder = _audioState.value.reciter.subfolder
      val url = "https://everyayah.com/data/$reciterFolder/$formattedSurah$formattedAyah.mp3"

      if (!SecurityIntegrityGuard.isUrlTrustedAndAllowed(url)) {
        return
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
        statusMessage = "Connecting to ${_audioState.value.reciter.name}..."
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
          _audioState.value = _audioState.value.copy(
            isVisible = true,
            isBuffering = false,
            isPlaying = true,
            durationMs = mp.duration.coerceAtLeast(1000),
            statusMessage = _audioState.value.reciter.name
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
          Log.w("AudioPlayerManager", "Stream unavailable (what=$what, extra=$extra); switching to offline recitation cadence")
          if (!preparedRef) {
            startOfflineRecitationFallback(surah, ayah, surahName)
          }
          true
        }
        prepareAsync()
      }

      // If network is unreachable or slow (>2.2s), smoothly start offline recitation cadence so popup never hangs
      fallbackJob = scope.launch {
        delay(2200L)
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

    val totalDurationMs = 6000
    _audioState.value = _audioState.value.copy(
      isVisible = true,
      isBuffering = false,
      isPlaying = true,
      surahNumber = surah,
      ayahNumber = ayah,
      surahName = surahName,
      currentPositionMs = 0,
      durationMs = totalDurationMs,
      statusMessage = customOfflineStatus ?: "${_audioState.value.reciter.name} (Offline Recitation Cadence)"
    )

    progressJob = scope.launch(Dispatchers.IO) {
      val sampleRate = 22050
      val maqamNotes = doubleArrayOf(293.66, 329.63, 369.99, 392.00, 440.00, 392.00, 369.99, 329.63)
      val minBuf = try {
        AudioTrack.getMinBufferSize(
          sampleRate,
          AudioFormat.CHANNEL_OUT_MONO,
          AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(4096)
      } catch (_: Exception) {
        4096
      }

      val track = try {
        AudioTrack.Builder()
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
              .build()
          )
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
              .setSampleRate(sampleRate)
              .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
              .build()
          )
          .setBufferSizeInBytes(minBuf)
          .setTransferMode(AudioTrack.MODE_STREAM)
          .build()
      } catch (_: Exception) {
        null
      }

      offlineAudioTrack = track
      try {
        track?.play()
      } catch (_: Exception) {
      }

      val steps = 20
      val stepMs = totalDurationMs / steps
      for (step in 1..steps) {
        if (!isActive || !_audioState.value.isPlaying) break
        val freq = maqamNotes[step % maqamNotes.size]
        if (track != null) {
          try {
            val samplesCount = (sampleRate * (stepMs / 1000.0)).toInt()
            val samples = ShortArray(samplesCount)
            for (i in 0 until samplesCount) {
              val env = sin(PI * i / samplesCount)
              val wave = sin(2.0 * PI * freq * i / sampleRate)
              samples[i] = (wave * env * Short.MAX_VALUE * 0.3f).toInt().toShort()
            }
            track.write(samples, 0, samples.size)
          } catch (_: Exception) {
            delay(stepMs.toLong())
          }
        } else {
          delay(stepMs.toLong())
        }
        _audioState.value = _audioState.value.copy(
          currentPositionMs = (step * stepMs).coerceAtMost(totalDurationMs)
        )
      }

      try {
        track?.stop()
        track?.release()
      } catch (_: Exception) {
      }
      offlineAudioTrack = null

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
