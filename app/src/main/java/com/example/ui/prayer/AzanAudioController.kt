package com.example.ui.prayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.prayer.MuezzinAudioOption
import com.example.data.prayer.PrayerAndAlarmConfig
import com.example.data.prayer.PrayerPreferencesManager
import com.example.data.prayer.PrayerTimeCalculator
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

data class FajrAlarmPlaybackState(
  val isActive: Boolean = false,
  val isFajrSpecialAlarm: Boolean = false,
  val prayerOrAlarmTitle: String = "",
  val muezzinTitle: String = "",
  val currentStageLabel: String = "",
  val remainingSeconds: Int = 0, // Max 900 seconds (15 minutes)
  val maxDurationSeconds: Int = 900,
  val includeFajrTathwibPhrase: Boolean = false,
  val arabicWakeMessage: String = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ • قُمْ إِلَى صَلَاةِ الْفَجْرِ",
  val transliterationMessage: String = PrayerTimeCalculator.FAJR_TATHWIB_TRANSLITERATION,
  val englishWakeMessage: String = "Prayer is better than sleep — Rise for the blessed Fajr prayer.",
  val urduWakeMessage: String = "نماز نیند سے بہتر ہے — فجر کی مبارک نماز کے لیے بیدار ہو جائیے۔"
)

object AzanAudioController {

  const val MAX_FAJR_ALARM_DURATION_SECONDS = 15 * 60 // 15 minutes strict cap
  const val STANDARD_AZAN_DURATION_SECONDS = 3 * 60 + 30 // 3.5 minutes for regular prayer Azan

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var activeJob: Job? = null
  private var toneSynthJob: Job? = null
  private var mediaPlayer: MediaPlayer? = null
  private var audioTrack: AudioTrack? = null
  private var previousSystemAlarmVolume: Int? = null
  private var audioFocusRequest: AudioFocusRequest? = null

  private val _playbackState = MutableStateFlow(FajrAlarmPlaybackState())
  val playbackState: StateFlow<FajrAlarmPlaybackState> = _playbackState.asStateFlow()

  fun startAzanOrFajrAlarm(
    context: Context,
    prayerName: String,
    isSpecial15MinFajrAlarm: Boolean,
    isTahajjudAlarm: Boolean = false
  ) {
    stopAlarm(context)
    val appCtx = context.applicationContext
    val config = PrayerPreferencesManager.loadConfig(appCtx)

    val isFajrRelated = isSpecial15MinFajrAlarm || prayerName.equals("Fajr", ignoreCase = true)
    val muezzin = if (isFajrRelated) {
      MuezzinAudioOption.entries.find { it.id == config.selectedMuezzinId }
        ?: MuezzinAudioOption.FAJR_TATHWIB
    } else {
      // Never insert Fajr-only Tathwib recording into Dhuhr/Asr/Maghrib/Isha
      val selected = MuezzinAudioOption.entries.find { it.id == config.selectedMuezzinId }
        ?: MuezzinAudioOption.MAKKAH_HARAM
      if (selected.includesFajrTathwib) MuezzinAudioOption.MAKKAH_HARAM else selected
    }

    val totalSeconds = if (isSpecial15MinFajrAlarm || isTahajjudAlarm) {
      MAX_FAJR_ALARM_DURATION_SECONDS
    } else {
      STANDARD_AZAN_DURATION_SECONDS
    }

    val title = when {
      isTahajjudAlarm -> "Tahajjud Wake-Up Alarm (Final Third of Night)"
      isSpecial15MinFajrAlarm -> "Fajr 15-Minute Wake-Up Alarm"
      else -> "$prayerName Azan (${muezzin.title})"
    }

    applyAlarmAudioFocusAndVolume(appCtx, config)
    if (config.vibrationEnabled) {
      triggerGentleVibration(appCtx)
    }

    _playbackState.value = FajrAlarmPlaybackState(
      isActive = true,
      isFajrSpecialAlarm = isSpecial15MinFajrAlarm || isTahajjudAlarm,
      prayerOrAlarmTitle = title,
      muezzinTitle = muezzin.title,
      currentStageLabel = if (isSpecial15MinFajrAlarm && config.progressiveSequence) {
        "Stage 1/3: Gentle Dawn Awakening Chime"
      } else {
        "Playing Authentic Azan Audio"
      },
      remainingSeconds = totalSeconds,
      maxDurationSeconds = totalSeconds,
      includeFajrTathwibPhrase = isFajrRelated
    )

    startAudioPlaybackEngine(
      context = appCtx,
      streamUrl = muezzin.streamUrl,
      volumeScalar = (config.alarmVolumePercent / 100f).coerceIn(0.2f, 1.0f),
      isProgressive15Min = isSpecial15MinFajrAlarm || isTahajjudAlarm
    )

    activeJob = scope.launch {
      var remaining = totalSeconds
      while (isActive && remaining > 0 && _playbackState.value.isActive) {
        delay(1000L)
        remaining -= 1
        val elapsed = totalSeconds - remaining
        val stage = if (isSpecial15MinFajrAlarm && config.progressiveSequence) {
          when {
            elapsed < 45 -> "Stage 1/3: Gentle Initial Awakening Audio"
            elapsed < 300 -> "Stage 2/3: Fajr Azan (الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ)"
            else -> "Stage 3/3: Progressive Fajr Wake-Up Reminder (15-Min Cap)"
          }
        } else {
          "Authentic Azan Playback Active"
        }
        _playbackState.value = _playbackState.value.copy(
          remainingSeconds = remaining,
          currentStageLabel = stage
        )
      }
      if (remaining <= 0) {
        // Automatically stop after 15 minutes (or standard Azan duration)
        stopAlarm(appCtx)
      }
    }
  }

  fun snoozeActiveAlarm(context: Context, snoozeMinutes: Int) {
    val appCtx = context.applicationContext
    val wasFajr = _playbackState.value.isFajrSpecialAlarm
    stopAlarm(appCtx)
    PrayerAlarmScheduler.scheduleSnoozeAlarm(
      context = appCtx,
      snoozeMinutes = snoozeMinutes.coerceIn(1, 30),
      isFajrSpecialAlarm = wasFajr
    )
  }

  fun stopAlarm(context: Context) {
    activeJob?.cancel()
    activeJob = null
    toneSynthJob?.cancel()
    toneSynthJob = null

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
      audioTrack?.stop()
    } catch (_: Exception) {
    }
    try {
      audioTrack?.release()
    } catch (_: Exception) {
    }
    audioTrack = null

    restoreSystemAlarmVolumeAndFocus(context.applicationContext)
    _playbackState.value = FajrAlarmPlaybackState(isActive = false)
  }

  private fun startAudioPlaybackEngine(
    context: Context,
    streamUrl: String,
    volumeScalar: Float,
    isProgressive15Min: Boolean
  ) {
    toneSynthJob?.cancel()
    toneSynthJob = scope.launch(Dispatchers.IO) {
      var streamingStarted = false
      try {
        val mp = MediaPlayer()
        mp.setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()
        )
        mp.setDataSource(streamUrl)
        mp.isLooping = isProgressive15Min
        mp.setVolume(volumeScalar, volumeScalar)
        mp.setOnPreparedListener { player ->
          streamingStarted = true
          player.start()
        }
        mp.setOnCompletionListener { player ->
          if (isProgressive15Min && _playbackState.value.isActive) {
            player.seekTo(0)
            player.start()
          } else {
            stopAlarm(context)
          }
        }
        mp.prepareAsync()
        mediaPlayer = mp
      } catch (_: Exception) {
        streamingStarted = false
      }

      // Wait briefly to see if network stream connects; simultaneously or as offline fallback,
      // generate a calm melodic Maqam Hijaz cadence on USAGE_ALARM so offline wake-up always works.
      delay(1200L)
      if (!streamingStarted && isActive && _playbackState.value.isActive) {
        playOfflineMelodicAzanCadence(volumeScalar, isProgressive15Min)
      }
    }
  }

  private suspend fun playOfflineMelodicAzanCadence(
    baseVolume: Float,
    progressiveBoost: Boolean
  ) {
    val sampleRate = 22050
    // Maqam Hijaz notes (D4, Eb4, F#4, G4, A4, Bb4, C#5, D5)
    val hijazFrequencies = doubleArrayOf(293.66, 311.13, 369.99, 392.00, 440.00, 466.16, 554.37, 587.33)
    val minBuf = AudioTrack.getMinBufferSize(
      sampleRate,
      AudioFormat.CHANNEL_OUT_MONO,
      AudioFormat.ENCODING_PCM_16BIT
    ).coerceAtLeast(4096)

    val track = try {
      AudioTrack.Builder()
        .setAudioAttributes(
          AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
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
      return
    }

    audioTrack = track
    try {
      track.play()
      var cycle = 0
      while (_playbackState.value.isActive) {
        val currentVol = if (progressiveBoost) {
          (baseVolume * (0.55f + (cycle.coerceAtMost(6) * 0.075f))).coerceIn(0.25f, 1.0f)
        } else {
          baseVolume
        }
        for (freq in hijazFrequencies) {
          if (!_playbackState.value.isActive) break
          val durationSamples = (sampleRate * 0.45).toInt()
          val samples = ShortArray(durationSamples)
          for (i in 0 until durationSamples) {
            val env = sin(PI * i / durationSamples)
            val wave = sin(2.0 * PI * freq * i / sampleRate)
            samples[i] = (wave * env * currentVol * Short.MAX_VALUE * 0.45).toInt().toShort()
          }
          track.write(samples, 0, samples.size)
        }
        cycle++
        delay(800L)
      }
    } catch (_: Exception) {
    }
  }

  private fun applyAlarmAudioFocusAndVolume(context: Context, config: PrayerAndAlarmConfig) {
    try {
      val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK)
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_ALARM)
              .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
              .build()
          )
          .build()
        audioFocusRequest = req
        audioManager.requestAudioFocus(req)
      }

      if (config.boostSystemAlarmStream) {
        val maxVol = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM)
        val currentVol = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        previousSystemAlarmVolume = currentVol
        val targetVol = ((maxVol * config.alarmVolumePercent) / 100).coerceIn(1, maxVol)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, targetVol, 0)
      }
    } catch (_: SecurityException) {
      // Respect Do Not Disturb / OS volume policy restrictions transparently
    } catch (_: Exception) {
    }
  }

  private fun restoreSystemAlarmVolumeAndFocus(context: Context) {
    try {
      val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
      previousSystemAlarmVolume?.let { prev ->
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, prev, 0)
      }
      previousSystemAlarmVolume = null

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
      }
      audioFocusRequest = null
    } catch (_: Exception) {
    }
  }

  private fun triggerGentleVibration(context: Context) {
    try {
      val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vm?.defaultVibrator
      } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        vibrator?.vibrate(
          VibrationEffect.createWaveform(longArrayOf(0, 400, 300, 500), -1)
        )
      }
    } catch (_: Exception) {
    }
  }
}
