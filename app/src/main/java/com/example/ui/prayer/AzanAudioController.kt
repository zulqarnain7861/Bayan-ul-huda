package com.example.ui.prayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.data.prayer.MuezzinAudioOption
import com.example.data.prayer.PrayerAndAlarmConfig
import com.example.data.prayer.PrayerPreferencesManager
import com.example.data.prayer.PrayerTimeCalculator
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
  val isPureVocalWithoutMusic: Boolean = true,
  val arabicWakeMessage: String = "الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ • قُمْ إِلَى صَلَاةِ الْفَجْرِ",
  val transliterationMessage: String = PrayerTimeCalculator.FAJR_TATHWIB_TRANSLITERATION,
  val englishWakeMessage: String = "Prayer is better than sleep — Rise for the blessed Fajr prayer.",
  val urduWakeMessage: String = "نماز نیند سے بہتر ہے — فجر کی مبارک نماز کے لیے بیدار ہو جائیے۔"
)

object AzanAudioController {

  const val MAX_FAJR_ALARM_DURATION_SECONDS = 15 * 60 // 15 minutes strict cap
  const val STANDARD_AZAN_DURATION_SECONDS = 3 * 60 + 30 // 3.5 minutes for regular prayer Azan
  const val SHORT_TAKBIR_VOCAL_DURATION_SECONDS = 30 // 30 seconds pure vocal Takbir (no musical tone)
  const val IS_PURE_VOCAL_WITHOUT_MUSIC = true

  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var activeJob: Job? = null
  private var playbackJob: Job? = null
  private var mediaPlayer: MediaPlayer? = null
  private var previousSystemAlarmVolume: Int? = null
  private var audioFocusRequest: AudioFocusRequest? = null

  private val _playbackState = MutableStateFlow(FajrAlarmPlaybackState())
  val playbackState: StateFlow<FajrAlarmPlaybackState> = _playbackState.asStateFlow()

  fun startAzanOrFajrAlarm(
    context: Context,
    prayerName: String,
    isSpecial15MinFajrAlarm: Boolean,
    isTahajjudAlarm: Boolean = false,
    azanModeName: String = "FULL_AZAN"
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

    val isShortVocalTakbir = !isSpecial15MinFajrAlarm && !isTahajjudAlarm && azanModeName == "SHORT_TONE"
    val totalSeconds = when {
      isSpecial15MinFajrAlarm || isTahajjudAlarm -> MAX_FAJR_ALARM_DURATION_SECONDS
      isShortVocalTakbir -> SHORT_TAKBIR_VOCAL_DURATION_SECONDS
      else -> STANDARD_AZAN_DURATION_SECONDS
    }

    val title = when {
      isTahajjudAlarm -> "Tahajjud Wake-Up Alarm (Pure Vocal Azan, No Music)"
      isSpecial15MinFajrAlarm -> "Fajr 15-Minute Pure Vocal Azan Alarm (No Music)"
      isShortVocalTakbir -> "$prayerName Vocal Takbir Reminder (No Music)"
      else -> "$prayerName Pure Vocal Azan (${muezzin.title})"
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
        "Stage 1/2: Pure Vocal Fajr Azan (الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ — No Music)"
      } else {
        "Playing Pure Vocal Azan Audio (No Music)"
      },
      remainingSeconds = totalSeconds,
      maxDurationSeconds = totalSeconds,
      includeFajrTathwibPhrase = isFajrRelated,
      isPureVocalWithoutMusic = true
    )

    startPureVocalPlaybackEngine(
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
            elapsed < 240 -> "Stage 1/2: Pure Vocal Fajr Azan (الصَّلَاةُ خَيْرٌ مِنَ النَّوْمِ — No Music)"
            else -> "Stage 2/2: Pure Vocal Fajr Azan Repetition (15-Min Cap — No Music)"
          }
        } else {
          "Pure Vocal Azan Playback Active (No Music)"
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
    playbackJob?.cancel()
    playbackJob = null

    try {
      mediaPlayer?.stop()
    } catch (_: Exception) {
    }
    try {
      mediaPlayer?.release()
    } catch (_: Exception) {
    }
    mediaPlayer = null

    restoreSystemAlarmVolumeAndFocus(context.applicationContext)
    _playbackState.value = FajrAlarmPlaybackState(isActive = false)
  }

  private fun startPureVocalPlaybackEngine(
    context: Context,
    streamUrl: String,
    volumeScalar: Float,
    isProgressive15Min: Boolean
  ) {
    playbackJob?.cancel()
    val candidateVocalUrls = listOf(
      streamUrl,
      "https://www.islamcan.com/audio/adhan/azan1.mp3",
      "https://www.islamcan.com/audio/adhan/azan2.mp3"
    ).distinct()

    playbackJob = scope.launch(Dispatchers.IO) {
      var urlIndex = 0
      fun tryPlayVocalUrl(index: Int) {
        if (index >= candidateVocalUrls.size || !_playbackState.value.isActive) return
        try {
          try {
            mediaPlayer?.release()
          } catch (_: Exception) {
          }
          val mp = MediaPlayer()
          mp.setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_ALARM)
              .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
              .build()
          )
          mp.setDataSource(candidateVocalUrls[index])
          mp.isLooping = isProgressive15Min
          mp.setVolume(volumeScalar, volumeScalar)
          mp.setOnPreparedListener { player ->
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
          mp.setOnErrorListener { _, _, _ ->
            urlIndex += 1
            if (urlIndex < candidateVocalUrls.size && _playbackState.value.isActive) {
              tryPlayVocalUrl(urlIndex)
            }
            true
          }
          mp.prepareAsync()
          mediaPlayer = mp
        } catch (_: Exception) {
          // Pure vocal Azan only — never synthesize musical tones or chimes
        }
      }
      tryPlayVocalUrl(0)
    }
  }

  private fun applyAlarmAudioFocusAndVolume(context: Context, config: PrayerAndAlarmConfig) {
    try {
      val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager ?: return
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val req = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN_TRANSIENT)
          .setWillPauseWhenDucked(true)
          .setAudioAttributes(
            AudioAttributes.Builder()
              .setUsage(AudioAttributes.USAGE_ALARM)
              .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
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
