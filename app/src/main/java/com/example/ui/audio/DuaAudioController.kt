package com.example.ui.audio

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.model.DailyDuaItem
import java.util.Locale
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

enum class DuaAudioLanguageMode(val label: String) {
  ARABIC_ONLY("Arabic Vocal Only"),
  ARABIC_AND_ENGLISH("Arabic + English Translation"),
  ARABIC_AND_URDU("Arabic + Urdu Translation")
}

data class DuaAudioPlaybackState(
  val isPlaying: Boolean = false,
  val activeDuaId: String? = null,
  val activeDuaTitle: String = "",
  val activeStageLabel: String = "",
  val repeatCountTarget: Int = 1,
  val currentRepeatIteration: Int = 1,
  val languageMode: DuaAudioLanguageMode = DuaAudioLanguageMode.ARABIC_ONLY,
  val progressFraction: Float = 0f
)

/**
 * Pure-vocal Masnoon & Special Occasion Dua Audio Controller.
 *
 * Strictly uses `AudioAttributes.CONTENT_TYPE_SPEECH` and `USAGE_MEDIA` with zero musical chimes or tones.
 * Supports:
 * 1. Pure vocal Arabic recitation for all Masnoon & Special Occasion Duas (with optional English/Urdu translation read-aloud).
 * 2. 1x or 3x Sunnah repeat mode.
 * 3. Direct Quranic Reciter stream delegation for Quranic Rabbana & Ayat al-Kursi Duas.
 */
class DuaAudioController(
  private val context: Context,
  private val quranAudioPlayerManager: AudioPlayerManager
) {
  private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
  private var tts: TextToSpeech? = null
  private var isTtsReady = false
  private var activeProgressJob: Job? = null

  private val _playbackState = MutableStateFlow(DuaAudioPlaybackState())
  val playbackState: StateFlow<DuaAudioPlaybackState> = _playbackState.asStateFlow()

  init {
    initTextToSpeech()
  }

  private fun initTextToSpeech() {
    runCatching {
      tts = TextToSpeech(context.applicationContext) { status ->
        if (status == TextToSpeech.SUCCESS) {
          isTtsReady = true
          tts?.setAudioAttributes(
            AudioAttributes.Builder()
              .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
              .setUsage(AudioAttributes.USAGE_MEDIA)
              .build()
          )
          tts?.setSpeechRate(0.88f)
        }
      }
    }
  }

  fun setLanguageMode(mode: DuaAudioLanguageMode) {
    _playbackState.value = _playbackState.value.copy(languageMode = mode)
  }

  fun toggleRepeatTarget() {
    val current = _playbackState.value.repeatCountTarget
    val next = if (current == 1) 3 else 1
    _playbackState.value = _playbackState.value.copy(repeatCountTarget = next)
  }

  fun playDuaVocal(
    dua: DailyDuaItem,
    preferQuranReciterIfAvailable: Boolean = false
  ) {
    activeProgressJob?.cancel()
    runCatching { tts?.stop() }

    val quranRef = dua.quranSurahAyah
    if (preferQuranReciterIfAvailable && quranRef != null) {
      quranAudioPlayerManager.playAyah(
        surah = quranRef.first,
        ayah = quranRef.second,
        surahName = dua.title
      )
      _playbackState.value = _playbackState.value.copy(
        isPlaying = true,
        activeDuaId = dua.id,
        activeDuaTitle = dua.title,
        activeStageLabel = "Streaming Verified Quran Reciter (${quranAudioPlayerManager.audioState.value.reciter.name} • Pure Vocal, No Music)",
        currentRepeatIteration = 1,
        progressFraction = 0.25f
      )
      activeProgressJob = scope.launch {
        val totalMs = 7000L
        val steps = 28
        for (step in 1..steps) {
          if (!isActive || !_playbackState.value.isPlaying) break
          delay(totalMs / steps)
          _playbackState.value = _playbackState.value.copy(
            progressFraction = (step.toFloat() / steps.toFloat()).coerceIn(0f, 1f)
          )
        }
        if (_playbackState.value.activeDuaId == dua.id) {
          _playbackState.value = _playbackState.value.copy(
            isPlaying = false,
            progressFraction = 1f,
            activeStageLabel = "Vocal Recitation Completed"
          )
        }
      }
      return
    }

    val repeatTarget = _playbackState.value.repeatCountTarget
    val langMode = _playbackState.value.languageMode

    _playbackState.value = _playbackState.value.copy(
      isPlaying = true,
      activeDuaId = dua.id,
      activeDuaTitle = dua.title,
      activeStageLabel = "Playing Pure Vocal Dua (${langMode.label} • No Music)",
      currentRepeatIteration = 1,
      progressFraction = 0f
    )

    activeProgressJob = scope.launch {
      for (iteration in 1..repeatTarget) {
        if (!isActive || !_playbackState.value.isPlaying) break

        _playbackState.value = _playbackState.value.copy(
          currentRepeatIteration = iteration,
          activeStageLabel = "Arabic Vocal Recitation ($iteration of $repeatTarget • Pure Speech, No Music)"
        )

        speakSegmentIfAvailable(dua.arabicText, Locale("ar"))
        val approxArabicDurationMs = (dua.arabicText.length * 42L).coerceIn(2800L, 9000L)
        val steps = 20
        for (s in 1..steps) {
          if (!isActive || !_playbackState.value.isPlaying) break
          delay(approxArabicDurationMs / steps)
          val baseProgress = (iteration - 1).toFloat() / repeatTarget.toFloat()
          val iterProgress = (s.toFloat() / steps.toFloat()) / repeatTarget.toFloat()
          _playbackState.value = _playbackState.value.copy(
            progressFraction = (baseProgress + iterProgress).coerceIn(0f, 1f)
          )
        }

        if (!isActive || !_playbackState.value.isPlaying) break

        if (langMode == DuaAudioLanguageMode.ARABIC_AND_ENGLISH) {
          _playbackState.value = _playbackState.value.copy(
            activeStageLabel = "English Translation Vocal Read-Aloud ($iteration of $repeatTarget)"
          )
          speakSegmentIfAvailable(dua.englishTranslation, Locale.US)
          delay((dua.englishTranslation.length * 32L).coerceIn(2000L, 6500L))
        } else if (langMode == DuaAudioLanguageMode.ARABIC_AND_URDU) {
          _playbackState.value = _playbackState.value.copy(
            activeStageLabel = "Urdu Translation Vocal Read-Aloud ($iteration of $repeatTarget)"
          )
          speakSegmentIfAvailable(dua.urduTranslation, Locale("ur"))
          delay((dua.urduTranslation.length * 35L).coerceIn(2000L, 6500L))
        }
      }

      if (_playbackState.value.isPlaying && _playbackState.value.activeDuaId == dua.id) {
        _playbackState.value = _playbackState.value.copy(
          isPlaying = false,
          progressFraction = 1f,
          activeStageLabel = "Completed Pure Vocal Playback • Tap Play to listen again"
        )
      }
    }
  }

  private fun speakSegmentIfAvailable(text: String, locale: Locale) {
    if (!isTtsReady) return
    runCatching {
      val engine = tts ?: return
      engine.language = locale
      val params = Bundle().apply {
        putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 1.0f)
      }
      engine.speak(text, TextToSpeech.QUEUE_FLUSH, params, "dua_${System.currentTimeMillis()}")
    }
  }

  fun stop() {
    activeProgressJob?.cancel()
    activeProgressJob = null
    runCatching { tts?.stop() }
    _playbackState.value = _playbackState.value.copy(
      isPlaying = false,
      activeDuaId = null,
      activeDuaTitle = "",
      activeStageLabel = "",
      progressFraction = 0f
    )
  }
}
